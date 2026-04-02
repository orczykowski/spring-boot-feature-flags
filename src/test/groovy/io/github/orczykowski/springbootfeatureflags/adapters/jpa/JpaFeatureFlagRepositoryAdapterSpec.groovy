package io.github.orczykowski.springbootfeatureflags.adapters.jpa

import io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinition
import io.github.orczykowski.springbootfeatureflags.FeatureFlagName
import io.github.orczykowski.springbootfeatureflags.FeatureFlagState
import spock.lang.Specification
import spock.lang.Subject

class JpaFeatureFlagRepositoryAdapterSpec extends Specification {

    def jpaRepository = Mock(SpringDataJpaFeatureFlagRepository)

    @Subject
    def adapter = new JpaFeatureFlagRepositoryAdapter(jpaRepository)

    def "should find all enabled feature flags excluding NOBODY"() {
        given:
          def enabledEntity = JpaFeatureFlagEntity.fromDomain(
                  new FeatureFlagDefinition(new FeatureFlagName("ENABLED"), FeatureFlagState.ANYBODY))
          def disabledEntity = JpaFeatureFlagEntity.fromDomain(
                  new FeatureFlagDefinition(new FeatureFlagName("DISABLED"), FeatureFlagState.NOBODY))
          jpaRepository.findAll() >> [enabledEntity, disabledEntity]

        when:
          def result = adapter.findAllEnabledFeatureFlags().toList()

        then:
          result.size() == 1
          result[0].name().value() == "ENABLED"
    }

    def "should find by name when exists"() {
        given:
          def flagName = new FeatureFlagName("TEST")
          def entity = JpaFeatureFlagEntity.fromDomain(
                  new FeatureFlagDefinition(flagName, FeatureFlagState.ANYBODY))
          jpaRepository.findById("TEST") >> Optional.of(entity)

        when:
          def result = adapter.findByName(flagName)

        then:
          result.isPresent()
          result.get().name() == flagName
    }

    def "should return empty when flag not found"() {
        given:
          jpaRepository.findById("MISSING") >> Optional.empty()

        when:
          def result = adapter.findByName(new FeatureFlagName("MISSING"))

        then:
          result.isEmpty()
    }

    def "should save feature flag"() {
        given:
          def definition = new FeatureFlagDefinition(
                  new FeatureFlagName("FLAG"), FeatureFlagState.RESTRICTED)

        when:
          def result = adapter.save(definition)

        then:
          1 * jpaRepository.save(_ as JpaFeatureFlagEntity)
          result == definition
    }

    def "should remove by name"() {
        when:
          adapter.removeByName(new FeatureFlagName("FLAG"))

        then:
          1 * jpaRepository.deleteById("FLAG")
    }

    def "should find all feature flags"() {
        given:
          def entity = JpaFeatureFlagEntity.fromDomain(
                  new FeatureFlagDefinition(new FeatureFlagName("F1"), FeatureFlagState.ANYBODY))
          jpaRepository.findAll() >> [entity]

        when:
          def result = adapter.findAll().toList()

        then:
          result.size() == 1
          result[0].name().value() == "F1"
    }
}
