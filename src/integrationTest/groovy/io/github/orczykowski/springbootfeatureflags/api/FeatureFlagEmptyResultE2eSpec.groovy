package io.github.orczykowski.springbootfeatureflags.api

import io.github.orczykowski.springbootfeatureflags.BaseIntegrationSpec
import org.hamcrest.CoreMatchers
import org.springframework.http.HttpStatus
import org.springframework.test.context.TestPropertySource

import static io.restassured.RestAssured.given

@TestPropertySource(properties = ['feature-flags.definitions='])
class FeatureFlagEmptyResultE2eSpec extends BaseIntegrationSpec {

    def "should respond with empty list when is no defined feature flags"() {
        given:
          def request = given(requestSpec())

        when:
          def response = request.get("feature-flags")

        then:
          response.then()
                  .statusCode(HttpStatus.OK.value())
                  .body("featureFlags", CoreMatchers.is([]))
    }

}
