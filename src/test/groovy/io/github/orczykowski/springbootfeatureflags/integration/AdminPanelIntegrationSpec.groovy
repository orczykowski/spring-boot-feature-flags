package io.github.orczykowski.springbootfeatureflags.integration

import io.restassured.RestAssured
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.ApplicationContext
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification

class AdminPanelIntegrationSpec {

    @SpringBootTest(
            classes = TestApplication,
            webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
    )
    @TestPropertySource(properties = [
            "feature-flags.enabled=true",
            "feature-flags.api.manage.enabled=true",
            "feature-flags.admin-panel.enabled=true",
            "spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration"
    ])
    static class WhenAdminPanelEnabledSpec extends Specification {

        @LocalServerPort
        int port

        def setup() {
            RestAssured.port = port
        }

        def "should serve admin panel HTML at default path"() {
            when: "the admin panel endpoint is called"
                def response = RestAssured.given()
                        .accept("text/html")
                        .get("/feature-flags-admin")

            then: "it returns 200 with HTML content"
                response.statusCode() == 200
                response.contentType().contains("text/html")
                response.body().asString().contains("<title>Feature Flags Admin</title>")
        }

        def "should inject the correct manage API base path into the HTML"() {
            when: "the admin panel endpoint is called"
                def response = RestAssured.given()
                        .get("/feature-flags-admin")

            then: "the HTML contains the manage API path, not the placeholder"
                def body = response.body().asString()
                body.contains("/manage/feature-flags")
                !body.contains("{{MANAGE_API_BASE_PATH}}")
        }
    }

    @SpringBootTest(
            classes = TestApplication,
            webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
    )
    @TestPropertySource(properties = [
            "feature-flags.enabled=true",
            "feature-flags.api.manage.enabled=true",
            "feature-flags.admin-panel.enabled=true",
            "feature-flags.admin-panel.path=/custom-admin",
            "spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration"
    ])
    static class WhenCustomPathConfiguredSpec extends Specification {

        @LocalServerPort
        int port

        def setup() {
            RestAssured.port = port
        }

        def "should serve admin panel at custom configured path"() {
            when: "the custom admin panel path is called"
                def response = RestAssured.given()
                        .get("/custom-admin")

            then: "it returns 200 with HTML content"
                response.statusCode() == 200
                response.contentType().contains("text/html")
                response.body().asString().contains("<title>Feature Flags Admin</title>")
        }
    }

    @SpringBootTest(classes = TestApplication)
    @TestPropertySource(properties = [
            "feature-flags.enabled=true",
            "feature-flags.api.manage.enabled=true",
            "feature-flags.admin-panel.enabled=false",
            "spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration"
    ])
    static class WhenAdminPanelDisabledSpec extends Specification {

        @Autowired
        ApplicationContext context

        def "should not register admin panel controller when disabled"() {
            expect: "no admin panel controller bean exists"
                !context.containsBean("featureFlagAdminPanelController")
        }
    }
}
