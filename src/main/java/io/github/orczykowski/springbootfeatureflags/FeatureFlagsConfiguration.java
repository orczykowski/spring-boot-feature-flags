package io.github.orczykowski.springbootfeatureflags;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
        value = "feature-flags.enabled",
        havingValue = "true",
        matchIfMissing = false)
@EnableConfigurationProperties(FeatureFlagsPropertySource.class)
class FeatureFlagsConfiguration {
    @Bean
    @ConditionalOnMissingBean({FeatureFlagSupplier.class})
    @ConditionalOnProperty(
            value = "feature-flags.api.manage.enabled",
            havingValue = "false",
            matchIfMissing = false)
    FeatureFlagSupplier featureFlagSupplier(final FeatureFlagsPropertySource definitionsRepository) {
        return new FeatureFlagsRepositoryPropertySourceFacade(definitionsRepository);
    }

    @Bean
    @ConditionalOnProperty(
            name = {"feature-flags.api.manage.enabled"},
            havingValue = "true",
            matchIfMissing = false)
    FeatureFlagRepository featureFlagDefinitionRepository(final FeatureFlagsPropertySource definitionsRepository) {
        return new FeatureFlagsRepositoryPropertySourceFacade(definitionsRepository);
    }

    @Bean
    @ConditionalOnProperty(
            name = {"feature-flags.api.manage.enabled"},
            havingValue = "true",
            matchIfMissing = false)
    FeatureFlagManager featureFlagManager(final FeatureFlagRepository repository) {
        return new FeatureFlagManager(repository);
    }

    @Bean
    EnabledFeatureFlagNameProvider featureFlagProvider(final FeatureFlagSupplier featureFlagSupplier,
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
    @ConditionalOnProperty(
            name = {"feature-flags.metrics.enabled"},
            havingValue = "true",
            matchIfMissing = false)
    @ConditionalOnMissingBean(MetricsPublisher.class)
    @ConditionalOnBean(MeterRegistry.class)
    MetricsPublisher metricsPublisher(final MeterRegistry meterRegistry) {
        return new FeatureFlagMetricsPublisher(meterRegistry);
    }
}
