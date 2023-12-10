package io.github.orczykowski.springbootfeatureflags;

import io.github.orczykowski.springbootfeatureflags.ConfigurationPropertiesFeatureFlagsRepositoryFacade.ConfigurationPropertiesFeatureFlagsRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@ConditionalOnProperty(name = {"feature-flags.enabled"}, havingValue = "true")
@Configuration
@EnableConfigurationProperties(ConfigurationPropertiesFeatureFlagsRepository.class)
class FeatureFlagsConfiguration {

    @Bean
    @ConditionalOnMissingBean
    FeatureFlagSupplier featureFlagSupplier(final ConfigurationPropertiesFeatureFlagsRepository definitionsRepository) {
        return new ConfigurationPropertiesFeatureFlagsRepositoryFacade(definitionsRepository);
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = {"feature-flags.api.manage.enabled"}, havingValue = "true")
    FeatureFlagRepository featureFlagDefinitionRepository(final ConfigurationPropertiesFeatureFlagsRepository definitionsRepository) {
        return new ConfigurationPropertiesFeatureFlagsRepositoryFacade(definitionsRepository);
    }

    @Bean
    EnabledFeatureFlagNameProvider featureFlagProvider(
            final FeatureFlagSupplier featureFlagSupplier,
            @Autowired(required = false) final UserContextProvider userContextProvider) {
        return new EnabledFeatureFlagNameProvider(featureFlagSupplier, userContextProvider);
    }

    @Bean
    FeatureFlagVerifier featureFlagVerifier(final FeatureFlagSupplier featureFlagSupplier,
                                            @Autowired(required = false) final FeatureFlagMetricsPublisher metricsPublisher,
                                            @Autowired(required = false) final UserContextProvider userContextProvider) {
        return new FeatureFlagVerifier(featureFlagSupplier, userContextProvider, metricsPublisher);
    }

    @Bean
    @ConditionalOnProperty(name = {"feature-flags.metrics.enabled"}, havingValue = "true")
    @ConditionalOnMissingBean
    MetricsPublisher metricsPublisher(final MeterRegistry meterRegistry) {
        return new FeatureFlagMetricsPublisher(meterRegistry);
    }

    @Bean
    @ConditionalOnProperty(name = {"feature-flags.api.manage.enabled"}, havingValue = "true")
    FeatureFlagManager featureFlagManager(final FeatureFlagRepository repository) {
        return new FeatureFlagManager(repository);
    }
}
