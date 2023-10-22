package io.github.orczykowski.springbootfeatureflags

import io.restassured.module.mockmvc.RestAssuredMockMvc
import org.hamcrest.Matchers
import org.spockframework.spring.SpringBean
import org.springframework.http.MediaType
import spock.lang.Specification

import java.util.stream.Stream

import static org.hamcrest.CoreMatchers.hasItem
import static org.hamcrest.CoreMatchers.notNullValue
import static org.springframework.http.HttpStatus.OK

class FeatureFlagApiIntTest extends Specification {

    private static def FLAG_NAME_1 = new FeatureFlagName("flag_01")
    private static def FLAG_NAME_2 = new FeatureFlagName("flag_02")


    @SpringBean
    FeatureFlagProvider featureFlagProvider = Mock()

    def "should return all enabled feature flags names"() {
        given:
          featureFlagProvider.provide() >> Stream.of(FLAG_NAME_1, FLAG_NAME_2)

          def request = RestAssuredMockMvc.given()
                  .standaloneSetup(new FeatureFlagApi(featureFlagProvider))
                  .contentType(MediaType.APPLICATION_JSON_VALUE)

        when:
          def response = request.get("/feature-flags")
        then:
          response.then()
                  .statusCode(OK.value())
                  .contentType(MediaType.APPLICATION_JSON_VALUE)
                  .body("featureFlags", notNullValue())
                  .body("featureFlags", hasItem("flag_01"))
                  .body("featureFlags", hasItem("flag_02"))
    }

    def "should return empty result when has no enabled flags defined"() {
        given:
          featureFlagProvider.provide() >> Stream.of()

          def request = RestAssuredMockMvc.given()
                  .standaloneSetup(new FeatureFlagApi(featureFlagProvider))
                  .contentType(MediaType.APPLICATION_JSON_VALUE)

        when:
          def response = request.get("/feature-flags")
        then:
          response.then()
                  .statusCode(OK.value())
                  .contentType(MediaType.APPLICATION_JSON_VALUE)
                  .body("featureFlags", Matchers.empty())
    }
}
