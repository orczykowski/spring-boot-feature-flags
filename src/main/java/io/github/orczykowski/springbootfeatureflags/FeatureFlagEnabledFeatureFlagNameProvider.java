package io.github.orczykowski.springbootfeatureflags;

import java.util.Optional;
import java.util.stream.Stream;

public class FeatureFlagEnabledFeatureFlagNameProvider {
    private final FeatureFlagSupplier featureFlagSupplier;
    private final FeatureFlagUserContextProvider userContextProvider;
    private final FeatureFlagAssignmentSupplier assignmentSupplier;

    FeatureFlagEnabledFeatureFlagNameProvider(
            final FeatureFlagSupplier featureFlagSupplier,
            final FeatureFlagUserContextProvider userContextProvider,
            final FeatureFlagAssignmentSupplier assignmentSupplier) {
        this.featureFlagSupplier = featureFlagSupplier;
        this.userContextProvider = userContextProvider;
        this.assignmentSupplier = assignmentSupplier;
    }

    public Stream<FeatureFlagName> provide() {
        var user = maybeUserContextProvider()
                .flatMap(FeatureFlagUserContextProvider::provide)
                .orElse(null);

        var userFlagNames = (user != null && assignmentSupplier != null)
                ? assignmentSupplier.findFlagNamesByUser(user)
                : FeatureFlags.empty();

        return featureFlagSupplier.findAllEnabledFeatureFlags()
                .filter(FeatureFlagDefinition::isEnable)
                .filter(flag -> isVisibleForUser(flag, user, userFlagNames))
                .map(FeatureFlagDefinition::name);
    }

    private boolean isVisibleForUser(
            final FeatureFlagDefinition flag,
            final FeatureFlagUser user,
            final FeatureFlags userFlagNames
    ) {
        if (flag.isEnableForALlUser()) {
            return true;
        }

        return flag.isRestricted()
                && user != null
                && userFlagNames.contains(flag.name());
    }

    private Optional<FeatureFlagUserContextProvider> maybeUserContextProvider() {
        return Optional.ofNullable(userContextProvider);
    }
}
