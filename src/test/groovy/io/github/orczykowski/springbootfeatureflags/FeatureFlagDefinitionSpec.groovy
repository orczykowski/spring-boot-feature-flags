package io.github.orczykowski.springbootfeatureflags

import spock.lang.Specification

import static io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinition.FeatureFlagState.OFF
import static io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinition.FeatureFlagState.ON
import static io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinition.FeatureFlagState.RESTRICTED_FOR_USERS

class FeatureFlagDefinitionSpec extends Specification {
    private static final NAME = new FeatureFlagDefinition.FeatureFlagName("laCucaracha")
    private static final USER = new FeatureFlagDefinition.User("SOY_CAPITAN")


    def "should not create Feature flag without obligatory data"() {
        when:
          new FeatureFlagDefinition(name, enabled, Set.of())

        then:
          thrown(InvalidFeatureFlagsException)

        where:
          name | enabled
          null | ON
          NAME | null

    }

    def "should create Feature Flags Definition without entitled users"() {
        when:
          def definition = new FeatureFlagDefinition(NAME, enabled, entitledUsers)

        then:
          definition != null
          definition.entitledUsers() == [] as Set

        where:
          entitledUsers | enabled
          null          | ON
          Set.of()      | ON
          null          | OFF
          Set.of()      | OFF
    }

    def "should not allow to create flag definition when flag is restricted for users but entitled users id empty or not provided"() {
        when:
          new FeatureFlagDefinition(NAME, RESTRICTED_FOR_USERS, entitledUsers)

        then:
          thrown(InvalidFeatureFlagsException)

        where:
          entitledUsers << [Set.of(), null]
    }

    def "should return true when feature flag is empowered"() {
        when:
          def definition = new FeatureFlagDefinition(NAME, status, entitledUsers)

        then:
          definition.isEmpowered() == expctedResult

        where:
          status               | entitledUsers || expctedResult
          ON                   | null          || true
          ON                   | Set.of()      || true
          ON                   | Set.of(USER)  || true
          RESTRICTED_FOR_USERS | Set.of(USER)  || true
          OFF                  | null          || false
          OFF                  | Set.of()      || false
          OFF                  | Set.of(USER)  || false
    }

    def "should return true when feature flag is enabled for USER"() {
        when:
          def definition = new FeatureFlagDefinition(NAME, status, entitledUsers)

        then:
          definition.isEnableForUser(USER) == expectedResult

        where:
          status               | entitledUsers                                       || expectedResult
          ON                   | null                                                || true
          ON                   | Set.of()                                            || true
          ON                   | Set.of(USER)                                        || true
          RESTRICTED_FOR_USERS | Set.of(USER)                                        || true
          RESTRICTED_FOR_USERS | Set.of(new FeatureFlagDefinition.User("El Polaco")) || false
          OFF                  | null                                                || false
          OFF                  | Set.of()                                            || false
          OFF                  | Set.of(USER)                                        || false

    }

    def "should update state of feature flag definition"() {
        given:
          def featureFlag = new FeatureFlagDefinition(NAME, initial, Set.of(USER))

        when:
          def result = featureFlag.update(destination, Set.of(USER))

        then:
          result.enabled() == destination

        where:
          initial              || destination
          ON                   || ON
          ON                   || OFF
          ON                   || RESTRICTED_FOR_USERS
          OFF                  || ON
          OFF                  || OFF
          OFF                  || RESTRICTED_FOR_USERS
          RESTRICTED_FOR_USERS || ON
          RESTRICTED_FOR_USERS || OFF
          RESTRICTED_FOR_USERS || RESTRICTED_FOR_USERS
    }

    def "should not allow to update feature flag definition with RESTRICTED TO USERS and not provide users"() {
        given:
          def featureFlag = new FeatureFlagDefinition(NAME, RESTRICTED_FOR_USERS, Set.of(USER))

        when:
          featureFlag.update(RESTRICTED_FOR_USERS, users)

        then:
          thrown(InvalidFeatureFlagsException)

        where:
          users << [Set.of(), null]
    }

    def "should update entitled users"() {
        given:
          def featureFlag = new FeatureFlagDefinition(NAME, RESTRICTED_FOR_USERS, Set.of(USER))

        and:
          def batman = new FeatureFlagDefinition.User("Batman")
          def newEntitledUsers = Set.of(batman)

        when:
          def result = featureFlag.update(RESTRICTED_FOR_USERS, newEntitledUsers)

        then:
          result.entitledUsers().size() == 1
          result.entitledUsers()[0] == batman
    }

}
