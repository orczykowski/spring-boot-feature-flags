package com.github.orczykowski.springbootfeatureflags;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

@ConfigurationProperties(prefix = "feature-flags")
public record FeatureFlagsDefinitionsConfiguration(Set<FeatureFlagDefinition> definitions) {

}
