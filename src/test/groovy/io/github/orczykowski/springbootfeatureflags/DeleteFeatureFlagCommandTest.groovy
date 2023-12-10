package io.github.orczykowski.springbootfeatureflags


import spock.lang.Specification

class DeleteFeatureFlagCommandTest extends Specification {

    def "should throw exception when flag name is null"() {
        when:
          new FeatureFlagCommand.DeleteFeatureFlagCommand(null)

        then:
          def ex = thrown(InvalidCommandException)
          ex.message == "Cannot delete feature flag. Feature flag name cannot be null"
    }
}
