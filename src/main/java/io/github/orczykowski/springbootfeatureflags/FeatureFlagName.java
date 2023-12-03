package io.github.orczykowski.springbootfeatureflags;

import java.util.Objects;

public record FeatureFlagName(String value) {
    private static final int MAX_LENGTH = 120;

    public FeatureFlagName(final String value) {
        validate(value);
        this.value = value.trim();
    }

    private static void validate(final String value) {
        if (Objects.isNull(value) || value.isBlank() || value.length() > MAX_LENGTH) {
            throw new ConfigurationFeatureFlagsException("Feature flag name can not be null or blank");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
