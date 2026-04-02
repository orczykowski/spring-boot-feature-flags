package io.github.orczykowski.springbootfeatureflags.adapters.jpa

import io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinition
import io.github.orczykowski.springbootfeatureflags.FeatureFlagName
import io.github.orczykowski.springbootfeatureflags.FeatureFlagState
import spock.lang.Specification

class JpaFeatureFlagEntitySpec extends Specification {

    def "should map domain to entity and back for ANYBODY state"() {
        given:
          def definition = new FeatureFlagDefinition(
                  new FeatureFlagName("TEST_FLAG"),
                  FeatureFlagState.ANYBODY
          )

        when:
          def entity = JpaFeatureFlagEntity.fromDomain(definition)
          def result = entity.toDomain()

        then:
          result.name().value() == "TEST_FLAG"
          result.enabled() == FeatureFlagState.ANYBODY
    }

    def "should map domain to entity and back for RESTRICTED state"() {
        given:
          def definition = new FeatureFlagDefinition(
                  new FeatureFlagName("RESTRICTED_FLAG"),
                  FeatureFlagState.RESTRICTED
          )

        when:
          def entity = JpaFeatureFlagEntity.fromDomain(definition)
          def result = entity.toDomain()

        then:
          result.name().value() == "RESTRICTED_FLAG"
          result.enabled() == FeatureFlagState.RESTRICTED
    }

    def "should map domain to entity and back for NOBODY state"() {
        given:
          def definition = new FeatureFlagDefinition(
                  new FeatureFlagName("DISABLED_FLAG"),
                  FeatureFlagState.NOBODY
          )

        when:
          def entity = JpaFeatureFlagEntity.fromDomain(definition)
          def result = entity.toDomain()

        then:
          result.name().value() == "DISABLED_FLAG"
          result.enabled() == FeatureFlagState.NOBODY
    }
}
