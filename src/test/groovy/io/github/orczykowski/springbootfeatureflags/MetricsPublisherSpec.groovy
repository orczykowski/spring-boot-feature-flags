package io.github.orczykowski.springbootfeatureflags


import io.micrometer.core.instrument.Meter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import spock.lang.Specification
import spock.lang.Subject

class MetricsPublisherSpec extends Specification {
    private static final FeatureFlagName FLAG_NAME = new FeatureFlagName("test")
    private static final User USER_1 = new User("bob")
    private static final double EPSILON = 0.0000000001d

    private MeterRegistry registry = new SimpleMeterRegistry()

    @Subject
    MetricsPublisher publisher = new FeatureFlagMetricsPublisher(registry)

    def "should publish verification metrics for user"() {
        when:
          publisher.reportVerification(FLAG_NAME, USER_1, verificationResult)

        then:
          def metric = findUserMetric(FLAG_NAME, USER_1, verificationResult)
          assertMetric(metric, 1.0)

        where:
          verificationResult << [true, false]
    }

    def "should publish verification metrics"() {
        when:
          publisher.reportVerification(FLAG_NAME, verificationResult)

        then:
          def metric = findVerificationMetric(FLAG_NAME, verificationResult)
          assertMetric(metric, 1.0)

        where:
          verificationResult << [true, false]
    }

    def "should publish aggregated not found metric"() {
        when:
          publisher.reportFlagNotFound()

        then:
          def metric = findNotFoundMetric()
          assertMetric(metric, 1.0)

        where:
          verificationResult << [true, false]
    }

    def "metrics should contain description"() {
        when:
          publisher.reportVerification(FLAG_NAME, true)

        then:
          def metric = findVerificationMetric(FLAG_NAME, true)
          metric.id.description == "[Feature flags] Number of feature flag verification"
    }

    private def findUserMetric(FeatureFlagName flagName, User user, boolean verificationResult) {
        def metric = findVerificationMetric(flagName, verificationResult)
        assert metric != null
        if (!metric.id.tags.contains(Tag.of("user", user.toString()))) {
            assert false
        }
        metric
    }

    private def findVerificationMetric(FeatureFlagName flagName, boolean verificationResult) {
        def metric = registry.meters.find {
            it.id.name == "feature_flags_verification_result.count"
                    && it.id.tags.contains(Tag.of("flag_name", flagName.value()))
                    && it.id.tags.contains(Tag.of("result", verificationResult.toString()))
        }
        assert metric != null
        metric
    }

    private def findNotFoundMetric() {
        def metric = registry.meters.find {
            it.id.name == "feature_flags_not_existing_flag.count"
        }
        assert metric != null
        metric
    }


    private def assertMetric(Meter metric, Double expectedValue) {
        Math.abs(metric.value - expectedValue) < EPSILON
    }


}
