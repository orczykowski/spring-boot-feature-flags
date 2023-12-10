package io.github.orczykowski.springbootfeatureflags


import spock.lang.Specification

class DuplicatedFeatureFlagExceptionTest extends Specification {
    def "should contain descriptive message"() {
        when:
          def ex = new DuplicatedFeatureFlagException(new FeatureFlagDefinition.FeatureFlagName("ABC"))

        then:
          ex.message == "feature flag definition with name [ABC] already exists"
    }
}
