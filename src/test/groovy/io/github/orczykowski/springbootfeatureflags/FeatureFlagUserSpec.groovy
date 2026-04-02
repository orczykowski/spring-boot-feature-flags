package io.github.orczykowski.springbootfeatureflags

import io.github.orczykowski.springbootfeatureflags.exceptions.FeatureFlagInvalidFeatureFlagsException
import spock.lang.Specification

class FeatureFlagUserSpec extends Specification {

    def "should print user name without any extra signs"() {
        given:
          def name = new FeatureFlagUser("gustaw+1")

        expect:
          name.toString() == "gustaw+1"
    }

    def "should not create User with null or blank string"() {
        when:
          new FeatureFlagUser((String) param)

        then:
          def ex = thrown(FeatureFlagInvalidFeatureFlagsException)
          ex.message == "User identifier cannot be null or blank."

        where:
          param << [null, "", " "]
    }

    def "should not create User with null"() {
        when:
          new FeatureFlagUser((Number) null)

        then:
          def ex = thrown(FeatureFlagInvalidFeatureFlagsException)
          ex.message == "User identifier cannot be null or blank."
    }

    def "should create user from string value"() {
        when:
          def user = new FeatureFlagUser("USER_123")

        then:
          user != null
          user.id() == "USER_123"
    }

    def "should create user from numeric value"() {
        when:
          def user = new FeatureFlagUser(id)

        then:
          user != null
          user.id() == "$id"

        where:
          id << [-1, 0, 1, 2L, 2131231232312312L, 2.31, 2f]
    }
}
