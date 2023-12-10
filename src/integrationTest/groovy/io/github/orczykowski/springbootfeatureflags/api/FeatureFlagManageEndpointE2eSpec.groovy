package io.github.orczykowski.springbootfeatureflags.api

import io.github.orczykowski.springbootfeatureflags.BaseIntegrationSpec
import org.springframework.http.HttpStatus

import static io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinition.FeatureFlagState.OFF
import static io.restassured.RestAssured.given

class FeatureFlagManageEndpointE2eSpec extends BaseIntegrationSpec {

    def "should create new feature flag"() {
        given:
          def request = given(requestSpec())
                  .body([name         : "NEW_FEATURE_FLAG",
                         enabled      : "OFF",
                         entitledUsers: ["123"]])
        when:
          def response = request.post("manage/feature-flags")

        then:
          response.then()
                  .statusCode(HttpStatus.CREATED.value())
        and:
          assertFeatureFlag("NEW_FEATURE_FLAG", OFF, ["123"] as Set)
    }

    def "should update existing feature flag"() {
        given:
          createFeatureFlag("FLAG_1", "ON", Set.of())

        and:
          def request = given(requestSpec())
                  .body([enabled      : "OFF",
                         entitledUsers: ["123"]])
        when:
          def response = request.put("manage/feature-flags/FLAG_1")

        then:
          response.then()
                  .statusCode(HttpStatus.OK.value())
        and:
          assertFeatureFlag("FLAG_1", OFF, ["123"] as Set)
    }

    def "should return Not Found when try update non existing feature flag"() {
        given:
          def request = given(requestSpec())
                  .body([enabled      : "OFF",
                         entitledUsers: ["123"]])
        when:
          def response = request.put("manage/feature-flags/FLAG_1")

        then:
          response.then()
                  .statusCode(HttpStatus.NOT_FOUND.value())
    }

    def "should return bad request when try update flag with invalid request"() {
        given:
          createFeatureFlag("FLAG_1", "ON", Set.of())
          def request = given(requestSpec())
                  .body(body)
        when:
          def response = request.put("manage/feature-flags/FLAG_1")

        then:
          response.then()
                  .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())

        where:
          body << [
                  [enabled: "NO", entitledUsers: ["123"]],
                  [enabled: null, entitledUsers: ["123"]],
                  [enabled: "RESTRICTED_FOR_USERS", entitledUsers: []]
          ]
    }

    def "should return conflict when try to create another feature flag with same name"() {
        given:
          createFeatureFlag("ABCDE", "ON")

        and:
          def request = given(requestSpec())
                  .body([name         : "ABCDE",
                         enabled      : "OFF",
                         entitledUsers: ["123"]])
        when:
          def response = request.post("manage/feature-flags")

        then:
          response.then()
                  .statusCode(HttpStatus.CONFLICT.value())
    }

    def "should delete feature flag"() {
        given:
          createFeatureFlag("TO_DELETE", "ON")

        and:
          def request = given(requestSpec())

        when:
          def response = request.delete("manage/feature-flags/TO_DELETE")

        then:
          response.then()
                  .statusCode(HttpStatus.NO_CONTENT.value())

    }

    def "should return all feature flags definitions"() {
        given:
          createFeatureFlag("FLAG_1", "ON")
          createFeatureFlag("FLAG_2", "OFF")
          createFeatureFlag("FLAG_3", "RESTRICTED_FOR_USERS", Set.of("123"))

        and:
          def request = given(requestSpec())

        when:
          def response = request.get("manage/feature-flags")

        then:
          def dto = response.then()
                  .statusCode(HttpStatus.OK.value())
                  .extract()
                  .as(GetResponse.class)

        and:
          dto.definitions != null
          dto.definitions.size() == 3

        and:
          hasItem(dto.definitions, "FLAG_1", "ON", [])
          hasItem(dto.definitions, "FLAG_2", "OFF", [])
          hasItem(dto.definitions, "FLAG_3", "RESTRICTED_FOR_USERS", ["123"])
    }

    def hasItem(List<FeatureFlagDefinition> definitions, String name, String enabled, List<String> users) {
        def flag = definitions.find { it.name == name }
        assert flag != null
        assert flag.enabled == enabled
        assert flag.entitledUsers.containsAll(users)
        true
    }

    static class GetResponse {
        List<FeatureFlagDefinition> definitions
    }

    static class FeatureFlagDefinition {
        String name
        String enabled
        List<String> entitledUsers
    }
}
