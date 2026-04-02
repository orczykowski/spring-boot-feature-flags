package io.github.orczykowski.springbootfeatureflags

import io.github.orczykowski.springbootfeatureflags.exceptions.FeatureFlagInvalidFeatureFlagsException
import spock.lang.Specification

import static FeatureFlagState.ANYBODY
import static FeatureFlagState.NOBODY
import static FeatureFlagState.RESTRICTED

class FeatureFlagDefinitionSpec extends Specification {
    private static final NAME = new FeatureFlagName("laCucaracha")


    def "should not create Feature flag without obligatory data"() {
        when:
          new FeatureFlagDefinition(name, enabled)

        then:
          thrown(FeatureFlagInvalidFeatureFlagsException)

        where:
          name | enabled
          null | ANYBODY
          NAME | null

    }

    def "should create Feature Flags Definition"() {
        when:
          def definition = new FeatureFlagDefinition(NAME, enabled)

        then:
          definition != null
          definition.name() == NAME
          definition.enabled() == enabled

        where:
          enabled << [ANYBODY, NOBODY, RESTRICTED]
    }

    def "should return true when feature flag is enabled for all users"() {
        when:
          def definition = new FeatureFlagDefinition(NAME, status)

        then:
          definition.isEnableForALlUser() == expectedResult

        where:
          status     || expectedResult
          ANYBODY    || true
          RESTRICTED || false
          NOBODY     || false
    }

    def "should return true when feature flag is restricted"() {
        when:
          def definition = new FeatureFlagDefinition(NAME, status)

        then:
          definition.isRestricted() == expectedResult

        where:
          status     || expectedResult
          RESTRICTED || true
          ANYBODY    || false
          NOBODY     || false
    }

    def "should update state of feature flag definition"() {
        given:
          def featureFlag = new FeatureFlagDefinition(NAME, initial)

        when:
          def result = featureFlag.update(destination)

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

}
