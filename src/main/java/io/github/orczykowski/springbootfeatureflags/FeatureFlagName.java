package io.github.orczykowski.springbootfeatureflags;

import java.util.Objects;

/**
 * ValueObject representing feature flag name
 *
 * @param value - unique name of feature flag
 */
public record FeatureFlagName(String value) {
    private static final int MAX_LENGTH = 120;

    /**
     * Constructor
     *
     * @param value name of feature flag
     */
    public FeatureFlagName(final String value) {
        validate(value);
        this.value = value.trim();
    }

    private static void validate(final String value) {
        if (Objects.isNull(value) || value.isBlank()) {
            throw new InvalidFeatureFlagsException("Feature flag name can not be null or blank");
        }
        if (value.length() > MAX_LENGTH) {
            throw new InvalidFeatureFlagsException("Feature flag name is too long");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
