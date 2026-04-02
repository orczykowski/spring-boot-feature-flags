package io.github.orczykowski.springbootfeatureflags.infrastructure;

import io.github.orczykowski.springbootfeatureflags.FeatureFlagName;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagUser;

/**
 * Publisher of feature flag verification metrics.
 * Implementations report verification events for monitoring and observability.
 */
public interface FeatureFlagMetricsPublisher {

    /**
     * Reports a feature flag verification result without user context.
     *
     * @param flag the verified feature flag name
     * @param verificationResult {@code true} if the flag was enabled, {@code false} otherwise
     */
    void reportVerification(final FeatureFlagName flag, final Boolean verificationResult);

    /**
     * Reports a feature flag verification result with user context.
     *
     * @param flagName the verified feature flag name
     * @param user the user for whom the flag was verified
     * @param verificationResult {@code true} if the flag was enabled, {@code false} otherwise
     */
    void reportVerification(final FeatureFlagName flagName, final FeatureFlagUser user, final Boolean verificationResult);

    /**
     * Reports an attempt to verify a non-existent feature flag.
     */
    void reportFlagNotFound();
}
