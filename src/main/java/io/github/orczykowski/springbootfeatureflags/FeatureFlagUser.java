package io.github.orczykowski.springbootfeatureflags;

import io.github.orczykowski.springbootfeatureflags.exceptions.FeatureFlagInvalidFeatureFlagsException;

import java.util.Objects;

import static java.util.Optional.ofNullable;

/**
 * ValueObject representing a user/user group from an external system. Used when RESTRICTED is used
 *
 * @param id - unique user ID or unique user group ID. Delivered externally (via external application)
 */
public record FeatureFlagUser(String id) {
    /**
     * Constructor
     *
     * @param id unique user ID or unique user group ID
     */
    public FeatureFlagUser {
        if (Objects.isNull(id) || id.isBlank()) {
            throw invalidIdException();
        }
    }

    /**
     * Constructor
     *
     * @param id unique user ID or unique user group ID
     */
    public FeatureFlagUser(final Number id) {
        this(ofNullable(id).map(Object::toString).orElseThrow(FeatureFlagUser::invalidIdException));
    }

    private static FeatureFlagInvalidFeatureFlagsException invalidIdException() {
        return new FeatureFlagInvalidFeatureFlagsException("User identifier cannot be null or blank.");
    }

    @Override
    public String toString() {
        return id;
    }
}
