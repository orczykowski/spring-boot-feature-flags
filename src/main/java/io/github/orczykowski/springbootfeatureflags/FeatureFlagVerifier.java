package io.github.orczykowski.springbootfeatureflags;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;

public class FeatureFlagVerifier {

    private static final Logger log = LoggerFactory.getLogger(FeatureFlagVerifier.class);

    private final UserContextProvider userContextProvider;
    private final FeatureFlagSupplier featureFlagSupplier;
    private final MetricsPublisher metricsPublisher;

    public FeatureFlagVerifier(final FeatureFlagSupplier featureFlagSupplier,
                               final UserContextProvider userContextProvider,
                               final MetricsPublisher metricsPublisher) {
        this.featureFlagSupplier = featureFlagSupplier;
        this.userContextProvider = userContextProvider;
        this.metricsPublisher = metricsPublisher;
    }

    public boolean verify(final FeatureFlagDefinition.FeatureFlagName featureFlagName) {
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
                .flatMap(UserContextProvider::provide)
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

    private boolean isEnableForUser(final FeatureFlagDefinition flag, final FeatureFlagDefinition.User user) {
        final boolean enableForUser = flag.isEnableForUser(user);
        if (Objects.nonNull(metricsPublisher)) {
            metricsPublisher.reportVerification(flag.name(), user, enableForUser);
        }
        return enableForUser;
    }

    private Optional<UserContextProvider> maybeUserContextProvider() {
        return Optional.ofNullable(userContextProvider);
    }

    private boolean noFlagFound(FeatureFlagDefinition.FeatureFlagName flagName) {
        log.warn("Feature Flag definition with name {} does not exist.", flagName);
        if (Objects.nonNull(metricsPublisher)) {
            metricsPublisher.reportFlagNotFound();
        }
        return false;
    }
}
