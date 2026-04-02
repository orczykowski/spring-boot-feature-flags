package io.github.orczykowski.springbootfeatureflags;

import io.github.orczykowski.springbootfeatureflags.infrastructure.FeatureFlagMetricsPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;

/**
 * Service returns information whether the feature flag is enabled. It takes into account the user's context
 */
public class FeatureFlagVerifier {

    private static final Logger log = LoggerFactory.getLogger(FeatureFlagVerifier.class);

    private final FeatureFlagUserContextProvider userContextProvider;
    private final FeatureFlagSupplier featureFlagSupplier;
    private final FeatureFlagAssignmentSupplier assignmentSupplier;
    private final FeatureFlagMetricsPublisher metricsPublisher;


    FeatureFlagVerifier(final FeatureFlagSupplier featureFlagSupplier,
                        final FeatureFlagUserContextProvider userContextProvider,
                        final FeatureFlagAssignmentSupplier assignmentSupplier,
                        final FeatureFlagMetricsPublisher metricsPublisher) {
        this.featureFlagSupplier = featureFlagSupplier;
        this.userContextProvider = userContextProvider;
        this.assignmentSupplier = assignmentSupplier;
        this.metricsPublisher = metricsPublisher;
    }

    /**
     * Method determines whether the flag is enabled or not, taking into account its definition and the user context, if any.
     *
     * @param featureFlagName - feature flag name which we want to verify
     * @return returns true / false depending on whether the flag is enabled or disabled. Depending on the configuration, it takes into account the user's contex
     */
    public boolean verify(final FeatureFlagName featureFlagName) {
        if (Objects.isNull(featureFlagName)) {
            log.warn("Try verify null feature flag name.");
            return false;
        }
        return featureFlagSupplier.findByName(featureFlagName)
                .map(this::check)
                .orElseGet(() -> noFlagFound(featureFlagName));
    }

    private Boolean check(final FeatureFlagDefinition flag) {
        return maybeUserContextProvider()
                .flatMap(FeatureFlagUserContextProvider::provide)
                .map(user -> isEnableForUser(flag, user))
                .orElseGet(() -> isEnableForALlUser(flag));
    }

    private boolean isEnableForALlUser(final FeatureFlagDefinition flag) {
        final boolean enableForALlUser = flag.isEnableForALlUser();
        if (Objects.nonNull(metricsPublisher)) {
            metricsPublisher.reportVerification(flag.name(), enableForALlUser);
        }
        return enableForALlUser;
    }

    private boolean isEnableForUser(final FeatureFlagDefinition flag, final FeatureFlagUser user) {
        boolean result;
        if (flag.isEnableForALlUser()) {
            result = true;
        } else if (flag.isRestricted() && assignmentSupplier != null) {
            result = assignmentSupplier.isUserAssigned(flag.name(), user);
        } else {
            result = false;
        }
        if (Objects.nonNull(metricsPublisher)) {
            metricsPublisher.reportVerification(flag.name(), user, result);
        }
        return result;
    }

    private Optional<FeatureFlagUserContextProvider> maybeUserContextProvider() {
        return Optional.ofNullable(userContextProvider);
    }

    private boolean noFlagFound(FeatureFlagName flagName) {
        log.warn("Feature Flag definition with name {} does not exist.", flagName);
        if (Objects.nonNull(metricsPublisher)) {
            metricsPublisher.reportFlagNotFound();
        }
        return false;
    }
}
