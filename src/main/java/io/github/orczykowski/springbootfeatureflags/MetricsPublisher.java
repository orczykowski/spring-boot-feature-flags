package io.github.orczykowski.springbootfeatureflags;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.Objects;

class MetricsPublisher {
    private static final String VERIFICATION_RESULT_COUNT = "feature_flags_verification_result.count";
    private static final String VERIFICATION_RESULT_COUNT_DESCRIPTION = "[Feature flags] Number of feature flag verification";
    private static final String TRY_VERIFY_NON_EXISTING_FLAG = "feature_flags_not_existing_flag.count";
    private static final String TRY_VERIFY_NON_EXISTING_FLAG_DESCRIPTION = "[Feature flags] Number of attempts to verify a non-existent feature flag";

    private static final String FLAG_NAME_TAG = "flag_name";
    private static final String USER_TAG = "user";
    private static final String RESULT_TAG = "result";
    private final MeterRegistry meterRegistry;

    MetricsPublisher(final MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    void reportVerification(final FeatureFlagDefinition.FeatureFlagName flag, final Boolean verificationResult) {
        reportVerification(flag, null, verificationResult);
    }

    void reportVerification(final FeatureFlagDefinition.FeatureFlagName flagName, final FeatureFlagDefinition.User user, final Boolean verificationResult) {
        final var counter = Counter.builder(VERIFICATION_RESULT_COUNT)
                .description(VERIFICATION_RESULT_COUNT_DESCRIPTION)
                .tags(FLAG_NAME_TAG, flagName.toString());
        if (Objects.nonNull(user)) {
            counter.tags(USER_TAG, user.toString());
        }
        if (Objects.nonNull(verificationResult)) {
            counter.tags(RESULT_TAG, verificationResult.toString());
        }
        counter.register(meterRegistry).increment();
    }

    void reportFlagNotFound() {
        Counter.builder(TRY_VERIFY_NON_EXISTING_FLAG)
                .description(TRY_VERIFY_NON_EXISTING_FLAG_DESCRIPTION)
                .register(meterRegistry).increment();
    }

}
