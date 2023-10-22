package io.github.orczykowski.springbootfeatureflags;

import java.util.Objects;

import static java.util.Optional.ofNullable;

public record User(String id) {

    public User {
        if (Objects.isNull(id) || id.isBlank()) {
            throw invalidIdException();
        }
    }

    public User(final Number id) {
        this(ofNullable(id).map(Object::toString).orElseThrow(User::invalidIdException));
    }

    private static ConfigurationFeatureFlagsException invalidIdException() {
        return new ConfigurationFeatureFlagsException("User identifier cannot be null or blank.");
    }

    @Override
    public String toString() {
        return id;
    }
}
