package io.github.orczykowski.springbootfeatureflags.adapters.property

import io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinition
import io.github.orczykowski.springbootfeatureflags.FeatureFlagName
import io.github.orczykowski.springbootfeatureflags.FeatureFlagState
import io.github.orczykowski.springbootfeatureflags.FeatureFlagsPropertySource
import spock.lang.Specification
import spock.lang.Subject

class PropertyFeatureFlagRepositoryAdapterSpec extends Specification {

    def assignmentRepository = new InMemoryFeatureFlagAssignmentRepository()

    @Subject
    PropertyFeatureFlagRepositoryAdapter adapter

    def setup() {
        def definitions = [
                new FeatureFlagsPropertySource.FeatureFlagDefinitionDto("ENABLED_FLAG", FeatureFlagState.ANYBODY, [] as Set),
                new FeatureFlagsPropertySource.FeatureFlagDefinitionDto("DISABLED_FLAG", FeatureFlagState.NOBODY, [] as Set),
                new FeatureFlagsPropertySource.FeatureFlagDefinitionDto("RESTRICTED_FLAG", FeatureFlagState.RESTRICTED, ["user-1", "user-2"] as Set)
        ]
        def propertySource = new FeatureFlagsPropertySource(definitions)
        adapter = new PropertyFeatureFlagRepositoryAdapter(propertySource, assignmentRepository)
    }

    def "should return all flags"() {
        when:
            def result = adapter.findAll().toList()

        then:
            result.size() == 3
    }

    def "should return only enabled flags (ANYBODY and RESTRICTED)"() {
        when:
            def result = adapter.findAllEnabledFeatureFlags().toList()

        then:
            result.size() == 2
            result.collect { it.name().value() }.containsAll(["ENABLED_FLAG", "RESTRICTED_FLAG"])
    }

    def "should find flag by name"() {
        when:
            def result = adapter.findByName(new FeatureFlagName("ENABLED_FLAG"))

        then:
            result.isPresent()
            result.get().name().value() == "ENABLED_FLAG"
            result.get().enabled() == FeatureFlagState.ANYBODY
    }

    def "should return empty when flag does not exist"() {
        when:
            def result = adapter.findByName(new FeatureFlagName("NON_EXISTENT"))

        then:
            result.isEmpty()
    }

    def "should save a new flag"() {
        given:
            def newFlag = new FeatureFlagDefinition(new FeatureFlagName("NEW_FLAG"), FeatureFlagState.ANYBODY)

        when:
            adapter.save(newFlag)

        then:
            adapter.findByName(new FeatureFlagName("NEW_FLAG")).isPresent()
    }

    def "should overwrite existing flag on save"() {
        given:
            def updated = new FeatureFlagDefinition(new FeatureFlagName("ENABLED_FLAG"), FeatureFlagState.NOBODY)

        when:
            adapter.save(updated)

        then:
            adapter.findByName(new FeatureFlagName("ENABLED_FLAG")).get().enabled() == FeatureFlagState.NOBODY
    }

    def "should remove flag by name"() {
        when:
            adapter.removeByName(new FeatureFlagName("ENABLED_FLAG"))

        then:
            adapter.findByName(new FeatureFlagName("ENABLED_FLAG")).isEmpty()
    }

    def "should populate assignments from property source for restricted flags"() {
        expect:
            assignmentRepository.findUsersByFlagName(new FeatureFlagName("RESTRICTED_FLAG")).size() == 2
    }

    def "should not populate assignments for non-restricted flags"() {
        expect:
            assignmentRepository.findUsersByFlagName(new FeatureFlagName("ENABLED_FLAG")).isEmpty()
    }
}
