package io.github.orczykowski.springbootfeatureflags;

/**
 * List of possible state of feature flag
 */
public enum FeatureFlagState {
    /**
     * flag will be enabled for everyone
     */
    ANYBODY,
    /**
     * flag will be disabled
     */
    NOBODY,
    /**
     * flag will be limited to a specific list of user
     */
    RESTRICTED
}
