package io.github.orczykowski.springbootfeatureflags


import spock.lang.Specification

class UpsertFeatureFlagCommandSpec extends Specification {
    def "should throw exception when try create command without flag name"() {
        when:
          new FeatureFlagCommand.UpsertFeatureFlagCommand(null, FeatureFlagDefinition.FeatureFlagState.OFF, Set.of())

        then:
          def ex = thrown(InvalidCommandException)
          ex.message == "Cannot create upsert feature flag command with null flag name"
    }
}

