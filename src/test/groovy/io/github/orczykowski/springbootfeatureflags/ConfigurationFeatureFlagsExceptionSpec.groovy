package io.github.orczykowski.springbootfeatureflags

import spock.lang.Specification

class ConfigurationFeatureFlagsExceptionSpec extends Specification {

    def "should add prefix to message"() {
        when:
          def ex = new ConfigurationFeatureFlagsException("Some message")

        then:
          ex.message == "Invalid feature flags configuration. Some message"
    }
}
