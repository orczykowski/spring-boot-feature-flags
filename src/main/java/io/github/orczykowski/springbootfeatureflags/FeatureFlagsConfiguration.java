package io.github.orczykowski.springbootfeatureflags;

import io.github.orczykowski.springbootfeatureflags.infrastructure.FeatureFlagMetricsMeterRegistryPublisher;
import io.github.orczykowski.springbootfeatureflags.infrastructure.FeatureFlagMetricsPublisher;
import io.github.orczykowski.springbootfeatureflags.infrastructure.FeatureFlagStorageConfiguration;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnProperty(
        value = "feature-flags.enabled",
        havingValue = "true",
        matchIfMissing = false)
@EnableConfigurationProperties(FeatureFlagsPropertySource.class)
@Import(FeatureFlagStorageConfiguration.class)
class FeatureFlagsConfiguration {

    @Bean
    @ConditionalOnProperty(
            name = {"feature-flags.api.manage.enabled"},
            havingValue = "true",
            matchIfMissing = false)
    FeatureFlagManager featureFlagManager(final FeatureFlagRepository repository,
                                          final FeatureFlagAssignmentRepository assignmentRepository) {
        return new FeatureFlagManager(repository, assignmentRepository);
    }

    @Bean
    FeatureFlagEnabledFeatureFlagNameProvider featureFlagProvider(final FeatureFlagSupplier featureFlagSupplier,
                                                                  @Autowired(required = false) final FeatureFlagUserContextProvider userContextProvider,
                                                                  @Autowired(required = false) final FeatureFlagAssignmentSupplier assignmentSupplier) {
        return new FeatureFlagEnabledFeatureFlagNameProvider(featureFlagSupplier, userContextProvider, assignmentSupplier);
    }

    @Bean
    FeatureFlagVerifier featureFlagVerifier(final FeatureFlagSupplier featureFlagSupplier,
                                            @Autowired(required = false) final FeatureFlagMetricsPublisher metricsPublisher,
                                            @Autowired(required = false) final FeatureFlagUserContextProvider userContextProvider,
                                            @Autowired(required = false) final FeatureFlagAssignmentSupplier assignmentSupplier) {
        return new FeatureFlagVerifier(featureFlagSupplier, userContextProvider, assignmentSupplier, metricsPublisher);
    }

    @Bean
    @ConditionalOnProperty(
            name = {"feature-flags.metrics.enabled"},
            havingValue = "true",
            matchIfMissing = false)
    @ConditionalOnMissingBean(FeatureFlagMetricsPublisher.class)
    @ConditionalOnBean(MeterRegistry.class)
    FeatureFlagMetricsPublisher metricsPublisher(final MeterRegistry meterRegistry) {
        return new FeatureFlagMetricsMeterRegistryPublisher(meterRegistry);
    }
}
