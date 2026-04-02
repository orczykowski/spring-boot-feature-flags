package io.github.orczykowski.springbootfeatureflags

import io.github.orczykowski.springbootfeatureflags.exceptions.FeatureFlagInvalidCommandException
import spock.lang.Specification

class DeleteFeatureFlagCommandTest extends Specification {

    def "should throw exception when flag name is null"() {
        when:
          new FeatureFlagManager.FeatureFlagCommand.DeleteFeatureFlag(null)

        then:
          def ex = thrown(FeatureFlagInvalidCommandException)
          ex.message == "Cannot delete feature flag. Flag name cannot be null"
    }
}
