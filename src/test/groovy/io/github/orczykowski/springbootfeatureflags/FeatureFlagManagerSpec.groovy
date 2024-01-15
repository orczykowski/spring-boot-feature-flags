package io.github.orczykowski.springbootfeatureflags


import spock.lang.Specification
import spock.lang.Subject

import static FeatureFlagState.ANYBODY
import static FeatureFlagState.NOBODY

class FeatureFlagManagerSpec extends Specification {
    private static final FeatureFlagName NAME = new FeatureFlagName("Gustaw")

    private FeatureFlagRepository repository = Mock()

    @Subject
    FeatureFlagManager manager = new FeatureFlagManager(repository)

    def "should throw exception when try create feature flag from null command"() {
        when:
          manager.create(null)

        then:
          thrown(InvalidCommandException)
    }

    def "should create and store new feature flag definition"() {
        given:
          def command = new FeatureFlagCommand.UpsertFeatureFlagCommand(NAME, ANYBODY, Set.of())
          repository.findByName(NAME) >> Optional.empty()

        when:
          manager.create(command)

        then:
          1 * repository.save({
              assert it.name == NAME
              assert it.enabled == ANYBODY
              assert it.entitledUsers == Set.of()
          })
    }

    def "should return information about name conflict when try create two flags with same name"() {
        given:
          def command = new FeatureFlagCommand.UpsertFeatureFlagCommand(NAME, ANYBODY, Set.of())

        and:
          def existingFlagWithSameName = new FeatureFlagDefinition(NAME, NOBODY, Set.of())
          repository.findByName(NAME) >> Optional.of(existingFlagWithSameName)

        when:
          manager.create(command)

        then:
          thrown(DuplicatedFeatureFlagException)
    }

    def "should throw exception when try update feature flag from null command"() {
        when:
          manager.update(null)

        then:
          thrown(InvalidCommandException)
    }

    def "should throw exception when try update non existing feature flag definition"() {
        given:
          def command = new FeatureFlagCommand.UpsertFeatureFlagCommand(NAME, ANYBODY, Set.of())
          repository.findByName(NAME) >> Optional.empty()

        when:
          manager.update(command)

        then:
          thrown(FeatureFlagsNotFoundException)
    }

    def "should update and store feature flag"() {
        given:
          def command = new FeatureFlagCommand.UpsertFeatureFlagCommand(NAME, ANYBODY, Set.of())

        and:
          def existingFlag = new FeatureFlagDefinition(NAME, NOBODY, Set.of())
          repository.findByName(NAME) >> Optional.of(existingFlag)

        when:
          manager.update(command)

        then:
          1 * repository.save({
              assert it.name == NAME
              assert it.enabled == ANYBODY
              assert it.entitledUsers == Set.of()
          })
    }

    def "should throw exception when try delete feature flag from null command"() {
        when:
          manager.delete(null)

        then:
          thrown(InvalidCommandException)
    }


}
