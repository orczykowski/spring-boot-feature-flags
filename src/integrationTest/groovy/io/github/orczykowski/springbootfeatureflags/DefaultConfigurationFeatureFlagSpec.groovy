package io.github.orczykowski.springbootfeatureflags


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource

//Default configuration
@TestPropertySource(properties = [
        'feature-flags.api.expose.enabled=false',
        'feature-flags.api.manage.enabled=false',
        'feature-flags.metrics.enabled=true',])
class DefaultConfigurationFeatureFlagSpec extends BaseIntegrationSpec {

    @Autowired(required = false)
    EnabledFeatureFlagNameProvider featureFlagProvider

    @Autowired(required = false)
    FeatureFlagPresenterApi featureFlagApi

    @Autowired(required = false)
    FeatureFlagSupplier featureFlagRepository

    @Autowired(required = false)
    FeatureFlagVerifier featureFlagVerifier

    @Autowired(required = false)
    MetricsPublisher metricsPublisher

    @Autowired(required = false)
    FeatureFlagExceptionHandler exceptionHandler

    @Autowired(required = false)
    FeatureFlagManagerApi managerApi

    @Autowired(required = false)
    FeatureFlagManager manager


    def "should create default bean with feature flag provider"() {
        expect:
          featureFlagProvider != null
    }

    def "should not create api controller for default configuration"() {
        expect:
          featureFlagApi == null
    }

    def "should not create manager api controller for default configuration"() {
        expect:
          managerApi == null
    }

    def "should not create manager service for default configuration"() {
        expect:
          manager == null
    }

    def "should not create exception handler for default configuration"() {
        expect:
          exceptionHandler == null
    }

    def "should create default bean with feature flag repository"() {
        expect:
          featureFlagRepository != null
    }

    def "should create default bean with feature flag verifier"() {
        expect:
          featureFlagVerifier != null
    }

    def "should create bean with metrics publisher"() {
        expect:
          metricsPublisher != null
    }
}
