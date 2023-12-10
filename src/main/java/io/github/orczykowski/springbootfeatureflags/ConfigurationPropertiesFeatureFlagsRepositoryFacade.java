package io.github.orczykowski.springbootfeatureflags;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


class ConfigurationPropertiesFeatureFlagsRepositoryFacade implements FeatureFlagSupplier, FeatureFlagRepository {

    private final Map<FeatureFlagDefinition.FeatureFlagName, FeatureFlagDefinition> definitions;

    ConfigurationPropertiesFeatureFlagsRepositoryFacade(final ConfigurationPropertiesFeatureFlagsRepository definitionsRepository) {
        this.definitions = new ConcurrentHashMap<>(definitionsRepository.definitions());
    }

    @Override
    public Stream<FeatureFlagDefinition> findAllEnabledFeatureFlags() {
        return definitions
                .values()
                .stream()
                .filter(FeatureFlagDefinition::isEnable);
    }

    @Override
    public Optional<FeatureFlagDefinition> findByName(final FeatureFlagDefinition.FeatureFlagName featureFlagName) {
        return Optional.ofNullable(definitions.get(featureFlagName));
    }

    @Override
    public void removeByName(final FeatureFlagDefinition.FeatureFlagName flagName) {
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

    @ConfigurationProperties(prefix = "feature-flags")
    public static class ConfigurationPropertiesFeatureFlagsRepository {
        private static final Logger log = LoggerFactory.getLogger(ConfigurationPropertiesFeatureFlagsRepository.class);
        private final Map<FeatureFlagDefinition.FeatureFlagName, FeatureFlagDefinition> definitions;

        @ConstructorBinding
        ConfigurationPropertiesFeatureFlagsRepository(Set<FeatureFlagDefinition> definitions) {
            log.debug("all feature flags definitions: {}", definitions);
            this.definitions = Objects.requireNonNullElseGet(definitions, this::emptyFlags)
                    .stream()
                    .collect(Collectors.toMap(FeatureFlagDefinition::name, Function.identity()));
        }

        public Map<FeatureFlagDefinition.FeatureFlagName, FeatureFlagDefinition> definitions() {
            return definitions;
        }

        private Set<FeatureFlagDefinition> emptyFlags() {
            return Collections.emptySet();
        }
    }
}
