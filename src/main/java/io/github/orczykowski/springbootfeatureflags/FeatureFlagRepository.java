package io.github.orczykowski.springbootfeatureflags;

import java.util.stream.Stream;

public interface FeatureFlagRepository extends FeatureFlagSupplier {
    void removeByName(FeatureFlagDefinition.FeatureFlagName flagName);

    FeatureFlagDefinition save(FeatureFlagDefinition definition);

    Stream<FeatureFlagDefinition> findAll();
}
