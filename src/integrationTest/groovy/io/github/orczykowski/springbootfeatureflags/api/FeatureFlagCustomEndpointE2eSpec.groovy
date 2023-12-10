package io.github.orczykowski.springbootfeatureflags.api

import io.github.orczykowski.springbootfeatureflags.BaseIntegrationSpec
import org.springframework.http.HttpStatus
import org.springframework.test.context.TestPropertySource

import static io.restassured.RestAssured.given

@TestPropertySource(properties = ['feature-flags.api.expose.path=my-feature-flags'])
class FeatureFlagCustomEndpointE2eSpec extends BaseIntegrationSpec {

    def "should present ony enabled feature flags names on api"() {
        given:
          def request = given(requestSpec())

        when:
          def response = request.get("my-feature-flags")

        then:
          response.then()
                  .statusCode(HttpStatus.OK.value())
    }
}
