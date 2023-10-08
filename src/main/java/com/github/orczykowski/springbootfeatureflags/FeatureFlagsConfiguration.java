package com.github.orczykowski.springbootfeatureflags;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(name = {"feature-flags.enabled"}, havingValue = "true")
@Configuration
class FeatureFlagsConfiguration {

    @Bean
    @ConditionalOnMissingBean
    UserContextProvider userContextProvider() {
        return new GuestUserContextProvider();
    }

    @Bean
    @ConditionalOnMissingBean
    FeatureFlagRepository featureFlagRepository(final FeatureFlagsDefinitionsConfiguration config) {
        return new FeatureFlagConfigurationPropertiesRepositoryFacade(config);
    }

    @Bean
    FeatureFlagProvider featureFlagProvider(
            final FeatureFlagRepository featureFlagRepository,
            final UserContextProvider userContextProvider) {
        return new FeatureFlagProvider(featureFlagRepository, userContextProvider);
    }

    @Bean
    FeatureFlagVerifier featureFlagVerifier(final FeatureFlagProvider featureFlagProvider) {
        return new FeatureFlagVerifier(featureFlagProvider);
    }
}
