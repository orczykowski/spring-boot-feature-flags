package io.github.orczykowski.springbootfeatureflags;

import java.util.Optional;
import java.util.stream.Stream;


public interface FeatureFlagSupplier {

    Stream<FeatureFlagDefinition> findAllEnabledFeatureFlags();

    Optional<FeatureFlagDefinition> findByName(FeatureFlagDefinition.FeatureFlagName featureFlagName);

}
