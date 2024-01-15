package io.github.orczykowski.springbootfeatureflags;

import java.util.Objects;

import static java.util.Optional.ofNullable;

/**
 * ValueObject representing a user/user group from an external system. Used when RESTRICTED is used
 *
 * @param id - unique user ID or unique user group ID. Delivered externally (via external application)
 */
public record User(String id) {
    /**
     * Constructor
     *
     * @param id unique user ID or unique user group ID
     */
    public User {
        if (Objects.isNull(id) || id.isBlank()) {
            throw invalidIdException();
        }
    }

    /**
     * Constructor
     *
     * @param id unique user ID or unique user group ID
     */
    public User(final Number id) {
        this(ofNullable(id).map(Object::toString).orElseThrow(User::invalidIdException));
    }

    private static InvalidFeatureFlagsException invalidIdException() {
        return new InvalidFeatureFlagsException("User identifier cannot be null or blank.");
    }

    @Override
    public String toString() {
        return id;
    }
}
