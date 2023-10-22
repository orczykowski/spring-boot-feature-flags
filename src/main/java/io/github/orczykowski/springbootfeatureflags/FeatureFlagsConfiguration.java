package io.github.orczykowski.springbootfeatureflags;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(name = {"feature-flags.enabled"}, havingValue = "true")
@Configuration
@EnableConfigurationProperties(ConfigurationPropertiesFeatureFlagsRepositoryFacade.ConfigurationPropertiesFeatureFlagsRepository.class)
class FeatureFlagsConfiguration {

    @Bean
    FeatureFlagRepository featureFlagRepository(final ConfigurationPropertiesFeatureFlagsRepositoryFacade.ConfigurationPropertiesFeatureFlagsRepository definitionsRepository) {
        return new ConfigurationPropertiesFeatureFlagsRepositoryFacade(definitionsRepository);
    }

    @Bean
    FeatureFlagProvider featureFlagProvider(
            final FeatureFlagRepository featureFlagRepository,
            @Autowired(required = false) final UserContextProvider userContextProvider) {
        return new FeatureFlagProvider(featureFlagRepository, userContextProvider);
    }

    @Bean
    FeatureFlagVerifier featureFlagVerifier(final FeatureFlagRepository featureFlagRepository,
                                            @Autowired(required = false) final UserContextProvider userContextProvider) {
        return new FeatureFlagVerifier(featureFlagRepository, userContextProvider);
    }
}
