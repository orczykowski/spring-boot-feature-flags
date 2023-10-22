package io.github.orczykowski.springbootfeatureflags;

import java.util.Optional;
import java.util.stream.Stream;


public interface FeatureFlagRepository {

    Stream<FeatureFlagDefinition> findAllEnabledFeatureFlags();

    Optional<FeatureFlagDefinition> findDefinition(FeatureFlagName featureFlagName);
}
