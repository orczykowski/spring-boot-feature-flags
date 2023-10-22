package io.github.orczykowski.springbootfeatureflags


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration
@SpringBootTest(classes = [TestSpringBootApplication,
        FeatureFlagsConfiguration])
class DefaultConfigurationFeatureFlagSpec extends Specification {

    @Autowired
    FeatureFlagProvider featureFlagProvider

    @Autowired(required = false)
    FeatureFlagApi featureFlagApi

    @Autowired
    FeatureFlagRepository featureFlagRepository

    @Autowired
    FeatureFlagVerifier featureFlagVerifier


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
}
