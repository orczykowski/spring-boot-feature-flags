package io.github.orczykowski.springbootfeatureflags

import spock.lang.Specification
import spock.lang.Subject

import static io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinitionTestFactory.disableForAllDto
import static io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinitionTestFactory.disableForUserDto
import static io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinitionTestFactory.enableForAllDto
import static io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinitionTestFactory.enableForUserDto

class FeatureFlagsRepositoryPropertySourceFacadeSpec extends Specification {
    static def definitions = [
            enableForUserDto("flag_1", "123"),
            enableForAllDto("flag_2"),
            disableForAllDto("flag_3"),
            disableForUserDto("flag_4", "456")]
    def repository = new FeatureFlagsPropertySource(definitions)

    @Subject
    def facade = new FeatureFlagsRepositoryPropertySourceFacade(repository)


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
          def flagName = new FeatureFlagName("flag_1")
          def maybeDefinition = facade.findByName(flagName)

        then:
          maybeDefinition.isPresent()

        and:
          def definition = maybeDefinition.get()
          definition.name() == flagName
    }

    def "should return empty when flag does not exist"() {
        given:
          def flagName = new FeatureFlagName("non_existing_flag")

        when:
          def maybeDefinition = facade.findByName(flagName)

        then:
          maybeDefinition.isEmpty()
    }

    def "should return work properly even if no flag is defined"() {
        given:
          def repository = new FeatureFlagsPropertySource(definitions)
        when:
          def result = repository.definitions()

        then:
          result.isEmpty()

        where:
          definitions << [null, []]
    }

}
