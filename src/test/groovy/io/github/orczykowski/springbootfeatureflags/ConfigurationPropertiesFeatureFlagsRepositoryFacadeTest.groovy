package io.github.orczykowski.springbootfeatureflags

import spock.lang.Specification
import spock.lang.Subject

import static io.github.orczykowski.springbootfeatureflags.ConfigurationPropertiesFeatureFlagsRepositoryFacade.ConfigurationPropertiesFeatureFlagsRepository
import static io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinitionTestFactory.disableForAll
import static io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinitionTestFactory.disableForUser
import static io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinitionTestFactory.enableForAll
import static io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinitionTestFactory.enableForUser

class ConfigurationPropertiesFeatureFlagsRepositoryFacadeTest extends Specification {
    static def definitions = [
            enableForUser("flag_1", "123"),
            enableForAll("flag_2"),
            disableForAll("flag_3"),
            disableForUser("flag_4", "456")] as Set
    def repository = new ConfigurationPropertiesFeatureFlagsRepository(definitions)

    @Subject
    def facade = new ConfigurationPropertiesFeatureFlagsRepositoryFacade(repository)

    def "should return only enabled feature flags"() {
        when:
          def result = facade.findAllEnabledFeatureFlags()

        then:
          def flags = result
                  .sorted((f1, f2) -> f1.name().value() <=> f2.name().value())
                  .toList()

          flags.size() == 2
          flags[0].name().value() == "flag_1"
          flags[1].name().value() == "flag_2"
    }

    def "should return flag details"() {
        when:
          def maybeDefinition = facade.findDefinition(deff.name())

        then:
          maybeDefinition.isPresent()

        and:
          def definition = maybeDefinition.get()
          definition == deff

        where:
          deff << definitions.toList()
    }

    def "should return empty when flag does not exist"() {
        given:
          def flagName = new FeatureFlagName("non_existing_flag")

        when:
          def maybeDefinition = facade.findDefinition(flagName)

        then:
          maybeDefinition.isEmpty()
    }

    def "should return work properly even if no flag is defined"() {
        given:
          def repository = new ConfigurationPropertiesFeatureFlagsRepository(definitions)
        when:
          def result = repository.definitions()

        then:
          result.isEmpty()

        where:
          definitions << [null, [] as Set]
    }

}
