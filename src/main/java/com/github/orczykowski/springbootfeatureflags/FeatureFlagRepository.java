package com.github.orczykowski.springbootfeatureflags;

import java.util.stream.Stream;


public interface FeatureFlagRepository {

    Stream<FeatureFlagDefinition> findFeaturesEnabledForAllUsers();

    Stream<FeatureFlagDefinition> findFeaturesEnabledFor(final User user);

}
