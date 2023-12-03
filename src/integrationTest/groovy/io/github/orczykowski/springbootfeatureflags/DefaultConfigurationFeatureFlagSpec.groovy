package io.github.orczykowski.springbootfeatureflags


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration
@SpringBootTest(classes = [
        TestSpringBootApplication,
        FeatureFlagsConfiguration])
class DefaultConfigurationFeatureFlagSpec extends Specification {

    @Autowired(required = false)
    FeatureFlagProvider featureFlagProvider

    @Autowired(required = false)
    FeatureFlagApi featureFlagApi

    @Autowired(required = false)
    FeatureFlagRepository featureFlagRepository

    @Autowired(required = false)
    FeatureFlagVerifier featureFlagVerifier

    @Autowired(required = false)
    MetricsPublisher metricsPublisher

    def "should create default bean with feature flag provider"() {
        expect:
          featureFlagProvider != null
    }

    def "should not create api controller for default configuration"() {
        expect:
          featureFlagApi == null
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
