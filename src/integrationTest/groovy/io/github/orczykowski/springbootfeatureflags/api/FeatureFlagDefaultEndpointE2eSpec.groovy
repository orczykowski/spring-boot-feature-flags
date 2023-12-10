package io.github.orczykowski.springbootfeatureflags.api

import io.github.orczykowski.springbootfeatureflags.BaseIntegrationSpec

import static io.restassured.RestAssured.given
import static org.hamcrest.CoreMatchers.hasItem
import static org.springframework.http.HttpStatus.OK

class FeatureFlagDefaultEndpointE2eSpec extends BaseIntegrationSpec {

    def "should present ony enabled feature flags names on api"() {
        given:
          createFeatureFlag("FLAG_1", "ANYBODY")
          createFeatureFlag("FLAG_2", "ANYBODY")

        and:
          def request = given(requestSpec())

        when:
          def response = request.get("feature-flags")

        then:
          response.then()
                  .statusCode(OK.value())
                  .body("featureFlags", hasItem("FLAG_1"))
                  .body("featureFlags", hasItem("FLAG_2"))
    }
}
