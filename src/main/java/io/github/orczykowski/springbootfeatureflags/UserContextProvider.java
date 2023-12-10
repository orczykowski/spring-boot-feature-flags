package io.github.orczykowski.springbootfeatureflags;

import java.util.Optional;

/**
 * Interface provides information about user in business context
 */
@FunctionalInterface
public interface UserContextProvider {

    /**
     * may return username or empty if the user is not available in a given context
     *
     * @return may return information about user
     */
    Optional<FeatureFlagDefinition.User> provide();
}
