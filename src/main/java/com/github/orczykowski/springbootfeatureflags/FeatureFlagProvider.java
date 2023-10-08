package com.github.orczykowski.springbootfeatureflags;

import java.util.stream.Stream;

class FeatureFlagProvider {
    //todo
    private final FeatureFlagRepository featureFlagRepository;
    private final UserContextProvider userContextProvider;

    FeatureFlagProvider(
            final FeatureFlagRepository featureFlagRepository,
            final UserContextProvider userContextProvider) {
        this.featureFlagRepository = featureFlagRepository;
        this.userContextProvider = userContextProvider;
    }

    Stream<FeatureFlagName> provide() {
        return userContextProvider
                .provide()
                .map(this::findAllEnabledFeaturesNamesFor)
                .orElseGet(this::findEnabledFeaturesNamesForAllUsers);
    }

    private Stream<FeatureFlagName> findAllEnabledFeaturesNamesFor(final User user) {
        return Stream.concat(findEnabledFeaturesNamesForAllUsers(), findEnabledFeaturesNamesFor(user));
    }

    private Stream<FeatureFlagName> findEnabledFeaturesNamesForAllUsers() {
        return featureFlagRepository
                .findFeaturesEnabledForAllUsers()
                .map(FeatureFlagDefinition::name);
    }

    private Stream<FeatureFlagName> findEnabledFeaturesNamesFor(final User user) {
        return featureFlagRepository.findFeaturesEnabledFor(user).map(FeatureFlagDefinition::name);
    }
}
