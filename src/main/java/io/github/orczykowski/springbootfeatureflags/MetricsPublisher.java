package io.github.orczykowski.springbootfeatureflags;

/**
 * interface for publishing feture flag usage statistics
 */
public interface MetricsPublisher {
    /**
     * reports feature flag usage statistics
     *
     * @param flag               feature flag name which we want to verify
     * @param verificationResult verification result
     */
    void reportVerification(FeatureFlagName flag, Boolean verificationResult);

    /**
     * reports feature flag usage statistics, taking into account user context
     *
     * @param flagName           feature flag name which we want to verify
     * @param user               user
     * @param verificationResult verification result
     */
    void reportVerification(FeatureFlagName flagName, User user, Boolean verificationResult);

    /**
     * reports the number of attempts to verify non-existent flags
     */
    void reportFlagNotFound();
}
