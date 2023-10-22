package io.github.orczykowski.springbootfeatureflags;

import java.util.Optional;
import java.util.stream.Stream;

class FeatureFlagProvider {
    private final FeatureFlagRepository featureFlagRepository;
    private final UserContextProvider userContextProvider;

    FeatureFlagProvider(
            final FeatureFlagRepository featureFlagRepository,
            final UserContextProvider userContextProvider) {
        this.featureFlagRepository = featureFlagRepository;
        this.userContextProvider = userContextProvider;
    }

    Stream<FeatureFlagName> provide() {
        return featureFlagRepository.findAllEnabledFeatureFlags()
                .filter(FeatureFlagDefinition::isEnable)
                .filter(this::filterForUserIfNeeded)
                .map(FeatureFlagDefinition::name);
    }

    private boolean filterForUserIfNeeded(final FeatureFlagDefinition flag) {
        return maybeUserContextProvider()
                .flatMap(UserContextProvider::provide)
                .map(flag::isEnableForUser)
                .orElseGet(flag::isEnableForALlUser);
    }

    private Optional<UserContextProvider> maybeUserContextProvider() {
        return Optional.ofNullable(userContextProvider);
    }
}
