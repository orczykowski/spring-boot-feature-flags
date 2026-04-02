package io.github.orczykowski.springbootfeatureflags;

import java.util.Optional;

/**
 * Provides the current user context for feature flag verification.
 * Implementations should return the currently authenticated user
 * or empty if no user is available in the current context.
 */
@FunctionalInterface
public interface FeatureFlagUserContextProvider {

    /**
     * Returns the current user, or empty if no user is available in the current context.
     *
     * @return the current user, or empty
     */
    Optional<FeatureFlagUser> provide();
}
