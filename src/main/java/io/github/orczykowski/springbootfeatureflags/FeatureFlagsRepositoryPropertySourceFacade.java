package io.github.orczykowski.springbootfeatureflags;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


class FeatureFlagsRepositoryPropertySourceFacade implements FeatureFlagSupplier, FeatureFlagRepository {

    private final Map<FeatureFlagName, FeatureFlagDefinition> definitions;

    FeatureFlagsRepositoryPropertySourceFacade(final FeatureFlagsPropertySource definitionsRepository) {
        this.definitions = definitionsRepository.definitions()
                .stream()
                .map(FeatureFlagsPropertySource.FeatureFlagDefinitionDto::asDefinition)
                .collect(Collectors.toConcurrentMap(FeatureFlagDefinition::name, Function.identity()));
    }

    @Override
    public Stream<FeatureFlagDefinition> findAllEnabledFeatureFlags() {
        return definitions
                .values()
                .stream()
                .filter(FeatureFlagDefinition::isEnable);
    }

    @Override
    public Optional<FeatureFlagDefinition> findByName(final FeatureFlagName featureFlagName) {
        return Optional.ofNullable(definitions.get(featureFlagName));
    }

    @Override
    public void removeByName(final FeatureFlagName flagName) {
        definitions.remove(flagName);
    }

    @Override
    public FeatureFlagDefinition save(final FeatureFlagDefinition definition) {
        return definitions.put(definition.name(), definition);
    }

    @Override
    public Stream<FeatureFlagDefinition> findAll() {
        return definitions.values().stream();
    }

}
