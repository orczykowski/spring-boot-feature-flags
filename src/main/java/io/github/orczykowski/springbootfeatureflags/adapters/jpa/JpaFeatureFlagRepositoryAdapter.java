package io.github.orczykowski.springbootfeatureflags.adapters.jpa;

import io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinition;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagName;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagRepository;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagState;

import java.util.Optional;
import java.util.stream.Stream;

public class JpaFeatureFlagRepositoryAdapter implements FeatureFlagRepository {

    private final SpringDataJpaFeatureFlagRepository jpaRepository;

    public JpaFeatureFlagRepositoryAdapter(final SpringDataJpaFeatureFlagRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Stream<FeatureFlagDefinition> findAllEnabledFeatureFlags() {
        return jpaRepository.findAll().stream()
                .map(JpaFeatureFlagEntity::toDomain)
                .filter(def -> !FeatureFlagState.NOBODY.equals(def.enabled()));
    }

    @Override
    public Optional<FeatureFlagDefinition> findByName(final FeatureFlagName featureFlagName) {
        return jpaRepository.findById(featureFlagName.value())
                .map(JpaFeatureFlagEntity::toDomain);
    }

    @Override
    public void removeByName(final FeatureFlagName flagName) {
        jpaRepository.deleteById(flagName.value());
    }

    @Override
    public FeatureFlagDefinition save(final FeatureFlagDefinition definition) {
        jpaRepository.save(JpaFeatureFlagEntity.fromDomain(definition));
        return definition;
    }

    @Override
    public Stream<FeatureFlagDefinition> findAll() {
        return jpaRepository.findAll().stream()
                .map(JpaFeatureFlagEntity::toDomain);
    }
}
