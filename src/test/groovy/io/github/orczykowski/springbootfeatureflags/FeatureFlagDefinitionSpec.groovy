package io.github.orczykowski.springbootfeatureflags

import spock.lang.Specification

import static io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinition.FeatureFlagState.ANYBODY
import static io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinition.FeatureFlagState.NOBODY
import static io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinition.FeatureFlagState.RESTRICTED

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
          null | ANYBODY
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
          null          | ANYBODY
          Set.of()      | ANYBODY
          null          | NOBODY
          Set.of()      | NOBODY
    }

    def "should not allow to create flag definition when flag is restricted for users but entitled users id empty or not provided"() {
        when:
          new FeatureFlagDefinition(NAME, RESTRICTED, entitledUsers)

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
          status     | entitledUsers || expctedResult
          ANYBODY    | null          || true
          ANYBODY    | Set.of()      || true
          ANYBODY    | Set.of(USER)  || true
          RESTRICTED | Set.of(USER)  || true
          NOBODY     | null          || false
          NOBODY     | Set.of()      || false
          NOBODY     | Set.of(USER)  || false
    }

    def "should return true when feature flag is enabled for USER"() {
        when:
          def definition = new FeatureFlagDefinition(NAME, status, entitledUsers)

        then:
          definition.isEnableForUser(USER) == expectedResult

        where:
          status     | entitledUsers                                       || expectedResult
          ANYBODY    | null                                                || true
          ANYBODY    | Set.of()                                            || true
          ANYBODY    | Set.of(USER)                                        || true
          RESTRICTED | Set.of(USER)                                        || true
          RESTRICTED | Set.of(new FeatureFlagDefinition.User("El Polaco")) || false
          NOBODY     | null                                                || false
          NOBODY     | Set.of()                                            || false
          NOBODY     | Set.of(USER)                                        || false

    }

    def "should update state of feature flag definition"() {
        given:
          def featureFlag = new FeatureFlagDefinition(NAME, initial, Set.of(USER))

        when:
          def result = featureFlag.update(destination, Set.of(USER))

        then:
          result.enabled() == destination

        where:
          initial    || destination
          ANYBODY    || ANYBODY
          ANYBODY    || NOBODY
          ANYBODY    || RESTRICTED
          NOBODY     || ANYBODY
          NOBODY     || NOBODY
          NOBODY     || RESTRICTED
          RESTRICTED || ANYBODY
          RESTRICTED || NOBODY
          RESTRICTED || RESTRICTED
    }

    def "should not allow to update feature flag definition with RESTRICTED to users and not provide users"() {
        given:
          def featureFlag = new FeatureFlagDefinition(NAME, RESTRICTED, Set.of(USER))

        when:
          featureFlag.update(RESTRICTED, users)

        then:
          thrown(InvalidFeatureFlagsException)

        where:
          users << [Set.of(), null]
    }

    def "should update entitled users"() {
        given:
          def featureFlag = new FeatureFlagDefinition(NAME, RESTRICTED, Set.of(USER))

        and:
          def batman = new FeatureFlagDefinition.User("Batman")
          def newEntitledUsers = Set.of(batman)

        when:
          def result = featureFlag.update(RESTRICTED, newEntitledUsers)

        then:
          result.entitledUsers().size() == 1
          result.entitledUsers()[0] == batman
    }

}
