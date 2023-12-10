package io.github.orczykowski.springbootfeatureflags


import spock.lang.Specification

class UserSpec extends Specification {

    def "should print user name without any extra signs"() {
        given:
          def name = new FeatureFlagDefinition.User("gustaw+1")

        expect:
          name.toString() == "gustaw+1"
    }

    def "should not create User with null or blank string"() {
        when:
          new FeatureFlagDefinition.User((String) param)

        then:
          def ex = thrown(InvalidFeatureFlagsException)
          ex.message == "User identifier cannot be null or blank."

        where:
          param << [null, "", " "]
    }

    def "should not create User with null"() {
        when:
          new FeatureFlagDefinition.User((Number) null)

        then:
          def ex = thrown(InvalidFeatureFlagsException)
          ex.message == "User identifier cannot be null or blank."
    }

    def "should create user from string value"() {
        when:
          def user = new FeatureFlagDefinition.User("USER_123")

        then:
          user != null
          user.id() == "USER_123"
    }

    def "should create user from numeric value"() {
        when:
          def user = new FeatureFlagDefinition.User(id)

        then:
          user != null
          user.id() == "$id"

        where:
          id << [-1, 0, 1, 2L, 2131231232312312L, 2.31, 2f]
    }
}
