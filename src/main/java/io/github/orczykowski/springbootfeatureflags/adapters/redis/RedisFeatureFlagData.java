package io.github.orczykowski.springbootfeatureflags.adapters.redis;

import io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinition;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagName;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagState;

public record RedisFeatureFlagData(String name, String enabled) {

    public static RedisFeatureFlagData fromDomain(FeatureFlagDefinition definition) {
        return new RedisFeatureFlagData(
                definition.name().value(),
                definition.enabled().name()
        );
    }

    public FeatureFlagDefinition toDomain() {
        return new FeatureFlagDefinition(
                new FeatureFlagName(name),
                FeatureFlagState.valueOf(enabled)
        );
    }
}
