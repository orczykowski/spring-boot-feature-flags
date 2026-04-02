package io.github.orczykowski.springbootfeatureflags.integration

import io.github.orczykowski.springbootfeatureflags.FeatureFlagManager
import io.github.orczykowski.springbootfeatureflags.FeatureFlagVerifier
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification

class AutoConfigurationIntegrationSpec {

    @SpringBootTest(classes = TestApplication)
    @TestPropertySource(properties = [
            "feature-flags.enabled=false",
            "spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration"
    ])
    static class WhenFeatureFlagsDisabledSpec extends Specification {

        @Autowired
        ApplicationContext context

        def "should not create FeatureFlagVerifier bean when feature flags are disabled"() {
            expect: "no verifier bean exists in the context"
                !context.containsBean("featureFlagVerifier")
        }
    }

    @SpringBootTest(classes = TestApplication)
    @TestPropertySource(properties = [
            "feature-flags.enabled=true",
            "feature-flags.api.manage.enabled=false",
            "feature-flags.api.expose.enabled=false",
            "spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration"
    ])
    static class WhenManageApiDisabledSpec extends Specification {

        @Autowired
        ApplicationContext context

        def "should create FeatureFlagVerifier bean even without APIs enabled"() {
            expect: "the verifier bean exists"
                context.containsBean("featureFlagVerifier")
        }

        def "should not create FeatureFlagManager bean when manage API is disabled"() {
            expect: "no manager bean exists"
                !context.containsBean("featureFlagManager")
        }
    }
}
