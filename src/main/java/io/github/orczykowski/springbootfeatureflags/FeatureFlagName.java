package io.github.orczykowski.springbootfeatureflags;

import java.util.Objects;

public record FeatureFlagName(String value) {
    public FeatureFlagName(final String value) {
        if (Objects.isNull(value) || value.isBlank()) {
            throw new ConfigurationFeatureFlagsException("Feature flag name can not be null or blank");
        }
        this.value = value.trim();
    }

    @Override
    public String toString() {
        return value;
    }
}
