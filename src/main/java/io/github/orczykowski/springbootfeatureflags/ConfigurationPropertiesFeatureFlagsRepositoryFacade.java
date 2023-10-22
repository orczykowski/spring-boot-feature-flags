package io.github.orczykowski.springbootfeatureflags;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


class ConfigurationPropertiesFeatureFlagsRepositoryFacade implements FeatureFlagRepository {

    private final ConfigurationPropertiesFeatureFlagsRepository definitionsRepository;

    ConfigurationPropertiesFeatureFlagsRepositoryFacade(final ConfigurationPropertiesFeatureFlagsRepository definitionsRepository) {
        this.definitionsRepository = definitionsRepository;
    }

    @Override
    public Stream<FeatureFlagDefinition> findAllEnabledFeatureFlags() {
        return definitionsRepository.definitions()
                .values()
                .stream()
                .filter(FeatureFlagDefinition::isEnable);
    }

    @Override
    public Optional<FeatureFlagDefinition> findDefinition(final FeatureFlagName featureFlagName) {
        return Optional.ofNullable(definitionsRepository
                .definitions()
                .get(featureFlagName));
    }

    @ConfigurationProperties(prefix = "feature-flags")
    public static class ConfigurationPropertiesFeatureFlagsRepository {
        private static final Logger log = LoggerFactory.getLogger(ConfigurationPropertiesFeatureFlagsRepository.class);
        private final Map<FeatureFlagName, FeatureFlagDefinition> definitions;

        @ConstructorBinding
        ConfigurationPropertiesFeatureFlagsRepository(Set<FeatureFlagDefinition> definitions) {
            log.debug("all feature flags definitions: {}", definitions);
            this.definitions = Objects.requireNonNullElseGet(definitions, () -> new HashSet<FeatureFlagDefinition>())
                    .stream()
                    .collect(Collectors.toUnmodifiableMap(FeatureFlagDefinition::name, Function.identity()));
        }

        public Map<FeatureFlagName, FeatureFlagDefinition> definitions() {
            return definitions;
        }
    }
}
