package io.github.orczykowski.springbootfeatureflags.api

import io.github.orczykowski.springbootfeatureflags.FeatureFlagAssignmentRepository
import io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinition
import io.github.orczykowski.springbootfeatureflags.FeatureFlagEntitledUsers
import io.github.orczykowski.springbootfeatureflags.FeatureFlagManager
import io.github.orczykowski.springbootfeatureflags.FeatureFlagName
import io.github.orczykowski.springbootfeatureflags.FeatureFlagRepository
import io.github.orczykowski.springbootfeatureflags.FeatureFlagState
import io.github.orczykowski.springbootfeatureflags.FeatureFlagUser
import io.github.orczykowski.springbootfeatureflags.exceptions.FeatureFlagDuplicatedFeatureFlagException
import io.github.orczykowski.springbootfeatureflags.exceptions.FeatureFlagsNotFoundException
import io.github.orczykowski.springbootfeatureflags.exceptions.FeatureFlagInvalidCommandException
import spock.lang.Specification
import spock.lang.Subject

import static io.github.orczykowski.springbootfeatureflags.FeatureFlagState.NOBODY

class FeatureFlagManagerSpec extends Specification {
    private static final FeatureFlagName NAME = new FeatureFlagName("Gustaw")
    private static final FeatureFlagUser USER = new FeatureFlagUser("Batman")

    private FeatureFlagRepository repository = Mock()
    private FeatureFlagAssignmentRepository assignmentRepository = Mock()

    @Subject
    FeatureFlagManager manager = new FeatureFlagManager(repository, assignmentRepository)

    def "should throw exception when try execute null command"() {
        when:
          manager.execute(null)

        then:
          thrown(FeatureFlagInvalidCommandException)
    }

    def "should create and store new feature flag definition"() {
        given:
          def command = new FeatureFlagManager.FeatureFlagCommand.CreateFeatureFlag(NAME, FeatureFlagState.ANYBODY, FeatureFlagEntitledUsers.empty())
          repository.findByName(NAME) >> Optional.empty()

        when:
          manager.create(command)

        then:
          1 * repository.save({
              assert it.name == NAME
              assert it.enabled == FeatureFlagState.ANYBODY
          })
          1 * assignmentRepository.saveAssignments(NAME, FeatureFlagEntitledUsers.empty())
    }

    def "should return information about name conflict when try create two flags with same name"() {
        given:
          def command = new FeatureFlagManager.FeatureFlagCommand.CreateFeatureFlag(NAME, FeatureFlagState.ANYBODY, FeatureFlagEntitledUsers.empty())

        and:
          def existingFlagWithSameName = new FeatureFlagDefinition(NAME, NOBODY)
          repository.findByName(NAME) >> Optional.of(existingFlagWithSameName)

        when:
          manager.create(command)

        then:
          thrown(FeatureFlagDuplicatedFeatureFlagException)
    }

    def "should enable feature flag"() {
        given:
          def existing = new FeatureFlagDefinition(NAME, FeatureFlagState.NOBODY)
          repository.findByName(NAME) >> Optional.of(existing)

        when:
          manager.execute(new FeatureFlagManager.FeatureFlagCommand.Enable(NAME))

        then:
          1 * repository.save({
              assert it.name == NAME
              assert it.enabled == FeatureFlagState.ANYBODY
          })
    }

    def "should disable feature flag"() {
        given:
          def existing = new FeatureFlagDefinition(NAME, FeatureFlagState.ANYBODY)
          repository.findByName(NAME) >> Optional.of(existing)

        when:
          manager.execute(new FeatureFlagManager.FeatureFlagCommand.Disable(NAME))

        then:
          1 * repository.save({
              assert it.name == NAME
              assert it.enabled == FeatureFlagState.NOBODY
          })
    }

    def "should add restricted user"() {
        given:
          def existing = new FeatureFlagDefinition(NAME, FeatureFlagState.ANYBODY)
          repository.findByName(NAME) >> Optional.of(existing)

        when:
          manager.execute(new FeatureFlagManager.FeatureFlagCommand.AddRestrictedUser(NAME, USER))

        then:
          1 * repository.save({
              assert it.name == NAME
              assert it.enabled == FeatureFlagState.RESTRICTED
          })
          1 * assignmentRepository.addUser(NAME, USER)
    }

    def "should remove restricted user"() {
        when:
          manager.execute(new FeatureFlagManager.FeatureFlagCommand.RemoveRestrictedUser(NAME, USER))

        then:
          1 * assignmentRepository.removeUser(NAME, USER)
    }

    def "should delete feature flag and its assignments"() {
        when:
          manager.execute(new FeatureFlagManager.FeatureFlagCommand.DeleteFeatureFlag(NAME))

        then:
          1 * assignmentRepository.removeAllByFlagName(NAME)
          1 * repository.removeByName(NAME)
    }

    def "should throw exception when enabling non existing feature flag"() {
        given:
          repository.findByName(NAME) >> Optional.empty()

        when:
          manager.execute(new FeatureFlagManager.FeatureFlagCommand.Enable(NAME))

        then:
          thrown(FeatureFlagsNotFoundException)
    }

    def "should restrict feature flag"() {
        given:
          def existing = new FeatureFlagDefinition(NAME, FeatureFlagState.ANYBODY)
          repository.findByName(NAME) >> Optional.of(existing)

        when:
          manager.execute(new FeatureFlagManager.FeatureFlagCommand.Restrict(NAME))

        then:
          1 * repository.save({
              assert it.name == NAME
              assert it.enabled == FeatureFlagState.RESTRICTED
          })
    }

    def "should throw exception when restricting non existing feature flag"() {
        given:
          repository.findByName(NAME) >> Optional.empty()

        when:
          manager.execute(new FeatureFlagManager.FeatureFlagCommand.Restrict(NAME))

        then:
          thrown(FeatureFlagsNotFoundException)
    }

    def "should transition flag from NOBODY to RESTRICTED"() {
        given:
          def existing = new FeatureFlagDefinition(NAME, FeatureFlagState.NOBODY)
          repository.findByName(NAME) >> Optional.of(existing)

        when:
          manager.execute(new FeatureFlagManager.FeatureFlagCommand.Restrict(NAME))

        then:
          1 * repository.save({
              assert it.name == NAME
              assert it.enabled == FeatureFlagState.RESTRICTED
          })
    }

    def "should transition flag from RESTRICTED to ANYBODY"() {
        given:
          def existing = new FeatureFlagDefinition(NAME, FeatureFlagState.RESTRICTED)
          repository.findByName(NAME) >> Optional.of(existing)

        when:
          manager.execute(new FeatureFlagManager.FeatureFlagCommand.Enable(NAME))

        then:
          1 * repository.save({
              assert it.name == NAME
              assert it.enabled == FeatureFlagState.ANYBODY
          })
    }

    def "should transition flag from RESTRICTED to NOBODY"() {
        given:
          def existing = new FeatureFlagDefinition(NAME, FeatureFlagState.RESTRICTED)
          repository.findByName(NAME) >> Optional.of(existing)

        when:
          manager.execute(new FeatureFlagManager.FeatureFlagCommand.Disable(NAME))

        then:
          1 * repository.save({
              assert it.name == NAME
              assert it.enabled == FeatureFlagState.NOBODY
          })
    }

    def "should throw exception when disabling non existing feature flag"() {
        given:
          repository.findByName(NAME) >> Optional.empty()

        when:
          manager.execute(new FeatureFlagManager.FeatureFlagCommand.Disable(NAME))

        then:
          thrown(FeatureFlagsNotFoundException)
    }

}
