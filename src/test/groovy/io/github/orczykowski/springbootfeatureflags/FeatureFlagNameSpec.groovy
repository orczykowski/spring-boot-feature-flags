package io.github.orczykowski.springbootfeatureflags


import spock.lang.Specification

class FeatureFlagNameSpec extends Specification {

    def "should trim white space from flag name"() {
        when:
          def flagName = new FeatureFlagName(name)

        then:
          flagName.value() == "SOME"

        where:
          name << [" SOME", "SOME ", " SOME "]
    }

    def "should print feature flag name without any extra signs"() {
        given:
          def name = new FeatureFlagName("SOME_NAME")

        expect:
          name.toString() == "SOME_NAME"
    }

    def "should create flag from very long string"() {
        given:
          def longStrName = "1" * 120
        when:
          def flag = new FeatureFlagName(longStrName)

        then:
          flag != null
          flag.toString() == longStrName
    }

    def "should not create Feature flag name with null or blank string"() {
        when:
          new FeatureFlagName(param)

        then:
          def ex = thrown(InvalidFeatureFlagsException)
          ex.message == "Feature flag name can not be null or blank"

        where:
          param << [null, "", " "]
    }

    def "should not create Feature flag name from too long string"() {
        when:
          new FeatureFlagName("1" * 121)

        then:
          def ex = thrown(InvalidFeatureFlagsException)
          ex.message == "Feature flag name is too long"
    }
}