package com.github.orczykowski.springbootfeatureflags;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class FeatureFlagVerifier {

  private static final Logger log = LoggerFactory.getLogger(FeatureFlagVerifier.class);

  private final FeatureFlagProvider featureFlagProvider;

  public FeatureFlagVerifier(final FeatureFlagProvider featureFlagProvider) {
    this.featureFlagProvider = featureFlagProvider;
  }

  public boolean verifyWithContextAwareness(final FeatureFlagName featureFlagName) {
    if (Objects.isNull(featureFlagName)) {
      log.debug("feature flag name is null");
      return false;
    }
    return featureFlagProvider.provide().anyMatch(flag -> flag.equals(featureFlagName));
  }
}
