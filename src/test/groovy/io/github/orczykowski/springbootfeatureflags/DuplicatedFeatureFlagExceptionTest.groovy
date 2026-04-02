package io.github.orczykowski.springbootfeatureflags

import io.github.orczykowski.springbootfeatureflags.exceptions.FeatureFlagDuplicatedFeatureFlagException
import spock.lang.Specification

class DuplicatedFeatureFlagExceptionTest extends Specification {
    def "should contain descriptive message"() {
        when:
          def ex = new FeatureFlagDuplicatedFeatureFlagException(new FeatureFlagName("ABC"))

        then:
          ex.message == "feature flag definition with name [ABC] already exists"
    }
}
