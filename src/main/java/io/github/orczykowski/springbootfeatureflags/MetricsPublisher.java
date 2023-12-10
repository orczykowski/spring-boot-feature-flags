package io.github.orczykowski.springbootfeatureflags;

public interface MetricsPublisher {
    void reportVerification(FeatureFlagDefinition.FeatureFlagName flag, Boolean verificationResult);

    void reportVerification(FeatureFlagDefinition.FeatureFlagName flagName, FeatureFlagDefinition.User user, Boolean verificationResult);

    void reportFlagNotFound();
}
