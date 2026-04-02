package io.github.orczykowski.springbootfeatureflags.integration

import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.http.HttpStatus
import spock.lang.Specification

@SpringBootTest(
        classes = TestApplication,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("property-it")
class FeatureFlagApiEdgeCasesIntegrationSpec extends Specification {

    static final String MANAGE_PATH = "/manage/feature-flags"

    @LocalServerPort
    int port

    def setup() {
        RestAssured.port = port
    }

    def "should return 422 when creating a flag with blank name"() {
        given: "a request with a blank flag name"
            def requestBody = [name: " ", enabled: "ANYBODY", entitledUsers: []]

        when: "the create endpoint is called"
            def response = RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(requestBody)
                    .post(MANAGE_PATH)

        then: "the server returns 422 unprocessable entity"
            response.statusCode() == HttpStatus.UNPROCESSABLE_ENTITY.value()
    }

    def "should return 422 when creating a flag with name exceeding 120 characters"() {
        given: "a request with a flag name that is too long"
            def longName = "A" * 121
            def requestBody = [name: longName, enabled: "ANYBODY", entitledUsers: []]

        when: "the create endpoint is called"
            def response = RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(requestBody)
                    .post(MANAGE_PATH)

        then: "the server returns 422 unprocessable entity"
            response.statusCode() == HttpStatus.UNPROCESSABLE_ENTITY.value()
    }

    def "should return 422 when creating a flag with invalid JSON body"() {
        given: "a request with malformed JSON"
            def invalidJson = '{"name": "TEST", "enabled": "INVALID_STATE"}'

        when: "the create endpoint is called"
            def response = RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(invalidJson)
                    .post(MANAGE_PATH)

        then: "the server returns 422 unprocessable entity"
            response.statusCode() == HttpStatus.UNPROCESSABLE_ENTITY.value()
    }
}
