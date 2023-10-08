package com.github.orczykowski.springbootfeatureflags;

import java.util.Objects;

public record User(String id) {

    public User {
        if (Objects.isNull(id) || id.isBlank()) {
            throw new ConfigurationFeatureFlagsException("Cannot create user from null or empty string");
        }
    }

    @Override
    public String toString() {
        return id;
    }
}
