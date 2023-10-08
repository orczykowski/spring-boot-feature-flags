package com.github.orczykowski.springbootfeatureflags;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;


class FeatureFlagConfigurationPropertiesRepositoryFacade implements FeatureFlagRepository {
    private static final Logger log = LoggerFactory.getLogger(FeatureFlagConfigurationPropertiesRepositoryFacade.class);
    private final Map<FeatureFlagName, FeatureFlagDefinition> allEnabledFeatureFlags;
    private final Map<User, Set<FeatureFlagDefinition>> userEnabledFeatureFlags;

    FeatureFlagConfigurationPropertiesRepositoryFacade(final FeatureFlagsDefinitionsConfiguration config) {
        final var definitions = config.definitions();
        log.debug("feature flags configuration {}", definitions);
        this.allEnabledFeatureFlags = definitions.stream()
                .filter(FeatureFlagDefinition::isEnabled)
                .collect(toUnmodifiableMap(FeatureFlagDefinition::name, identity()));
        this.userEnabledFeatureFlags = definitions.stream()
                .filter(FeatureFlagDefinition::isDefinedForUsers)
                .flatMap(this::asPairs).collect(groupingBy(Pair::user, mapping(Pair::second, toUnmodifiableSet())));
    }

    @Override
    public Stream<FeatureFlagDefinition> findFeaturesEnabledForAllUsers() {
        return allEnabledFeatureFlags.values().stream().filter(FeatureFlagDefinition::isDefinedForAllUsers);
    }

    @Override
    public Stream<FeatureFlagDefinition> findFeaturesEnabledFor(final User user) {
        return userEnabledFeatureFlags.getOrDefault(user, Collections.emptySet()).stream();
    }

    private Stream<Pair<User, FeatureFlagDefinition>> asPairs(final FeatureFlagDefinition featureFlagDefinition) {
        return featureFlagDefinition.entitledUsers().stream().map(user -> new Pair<>(user, featureFlagDefinition));
    }

    private record Pair<T, R>(T user, R second) {
    }

}
