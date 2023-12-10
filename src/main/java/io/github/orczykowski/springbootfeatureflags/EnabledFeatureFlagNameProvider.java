package io.github.orczykowski.springbootfeatureflags;

import java.util.Optional;
import java.util.stream.Stream;

class EnabledFeatureFlagNameProvider {
    private final FeatureFlagSupplier featureFlagSupplier;
    private final UserContextProvider userContextProvider;

    EnabledFeatureFlagNameProvider(
            final FeatureFlagSupplier featureFlagSupplier,
            final UserContextProvider userContextProvider) {
        this.featureFlagSupplier = featureFlagSupplier;
        this.userContextProvider = userContextProvider;
    }

    Stream<FeatureFlagDefinition.FeatureFlagName> provide() {
        return featureFlagSupplier.findAllEnabledFeatureFlags()
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
