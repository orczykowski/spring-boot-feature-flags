package io.github.orczykowski.springbootfeatureflags

import spock.lang.Specification

import static io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinition.FeatureFlagState.OFF
import static io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinition.FeatureFlagState.ON
import static io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinition.FeatureFlagState.RESTRICTED_FOR_USERS

class FeatureFlagDefinitionSpec extends Specification {
    private static final NAME = new FeatureFlagName("laCucaracha")
    private static final USER = new User("SOY_CAPITAN")


    def "should not create Feature flag without obligatory data"() {
        when:
          new FeatureFlagDefinition(name, enabled, Set.of())

        then:
          thrown(ConfigurationFeatureFlagsException)

        where:
          name | enabled
          null | ON
          NAME | null

    }

    def "should create Feature Flags Definition without entitled users"() {
        when:
          def definition = new FeatureFlagDefinition(NAME, ON, entitledUsers)

        then:
          definition != null

        where:
          entitledUsers << [null, Set.of()]
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
          status               | entitledUsers                 || expectedResult
          ON                   | null                          || true
          ON                   | Set.of()                      || true
          ON                   | Set.of(USER)                  || true
          RESTRICTED_FOR_USERS | Set.of(USER)                  || true
          RESTRICTED_FOR_USERS | Set.of(new User("El Polaco")) || false
          OFF                  | null                          || false
          OFF                  | Set.of()                      || false
          OFF                  | Set.of(USER)                  || false

    }
}
