package io.github.orczykowski.springbootfeatureflags

import io.github.orczykowski.springbootfeatureflags.exceptions.FeatureFlagInvalidCommandException
import spock.lang.Specification

class FeatureFlagCommandSpec extends Specification {

    def "should throw exception when creating CreateFeatureFlag without flag name"() {
        when:
          new FeatureFlagManager.FeatureFlagCommand.CreateFeatureFlag(null, FeatureFlagState.NOBODY, FeatureFlagEntitledUsers.empty())

        then:
          thrown(FeatureFlagInvalidCommandException)
    }

    def "should throw exception when creating CreateFeatureFlag without state"() {
        when:
          new FeatureFlagManager.FeatureFlagCommand.CreateFeatureFlag(new FeatureFlagName("test"), null, FeatureFlagEntitledUsers.empty())

        then:
          thrown(FeatureFlagInvalidCommandException)
    }

    def "should default users to empty when null"() {
        when:
          def cmd = new FeatureFlagManager.FeatureFlagCommand.CreateFeatureFlag(new FeatureFlagName("test"), FeatureFlagState.ANYBODY, null)

        then:
          cmd.users() == FeatureFlagEntitledUsers.empty()
    }

    def "should throw exception when creating DeleteFeatureFlag without flag name"() {
        when:
          new FeatureFlagManager.FeatureFlagCommand.DeleteFeatureFlag(null)

        then:
          thrown(FeatureFlagInvalidCommandException)
    }

    def "should throw exception when creating Enable without flag name"() {
        when:
          new FeatureFlagManager.FeatureFlagCommand.Enable(null)

        then:
          thrown(FeatureFlagInvalidCommandException)
    }

    def "should throw exception when creating Disable without flag name"() {
        when:
          new FeatureFlagManager.FeatureFlagCommand.Disable(null)

        then:
          thrown(FeatureFlagInvalidCommandException)
    }

    def "should throw exception when creating Restrict without flag name"() {
        when:
          new FeatureFlagManager.FeatureFlagCommand.Restrict(null)

        then:
          thrown(FeatureFlagInvalidCommandException)
    }

    def "should throw exception when creating AddRestrictedUser without flag name"() {
        when:
          new FeatureFlagManager.FeatureFlagCommand.AddRestrictedUser(null, new FeatureFlagUser("u1"))

        then:
          thrown(FeatureFlagInvalidCommandException)
    }

    def "should throw exception when creating AddRestrictedUser without user"() {
        when:
          new FeatureFlagManager.FeatureFlagCommand.AddRestrictedUser(new FeatureFlagName("test"), null)

        then:
          thrown(FeatureFlagInvalidCommandException)
    }

    def "should throw exception when creating RemoveRestrictedUser without flag name"() {
        when:
          new FeatureFlagManager.FeatureFlagCommand.RemoveRestrictedUser(null, new FeatureFlagUser("u1"))

        then:
          thrown(FeatureFlagInvalidCommandException)
    }

    def "should throw exception when creating RemoveRestrictedUser without user"() {
        when:
          new FeatureFlagManager.FeatureFlagCommand.RemoveRestrictedUser(new FeatureFlagName("test"), null)

        then:
          thrown(FeatureFlagInvalidCommandException)
    }
}
