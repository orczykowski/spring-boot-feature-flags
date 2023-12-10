package io.github.orczykowski.springbootfeatureflags.api

import io.github.orczykowski.springbootfeatureflags.BaseIntegrationSpec
import org.springframework.http.HttpStatus

import static io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinition.FeatureFlagState.NOBODY
import static io.restassured.RestAssured.given

class FeatureFlagManageEndpointE2eSpec extends BaseIntegrationSpec {

    def "should create new feature flag"() {
        given:
          def request = given(requestSpec())
                  .body([name         : "NEW_FEATURE_FLAG",
                         enabled      : "NOBODY",
                         entitledUsers: ["123"]])
        when:
          def response = request.post("manage/feature-flags")

        then:
          response.then()
                  .statusCode(HttpStatus.CREATED.value())
        and:
          assertFeatureFlag("NEW_FEATURE_FLAG", NOBODY, ["123"] as Set)
    }

    def "should update existing feature flag"() {
        given:
          createFeatureFlag("FLAG_1", "ANYBODY", Set.of())

        and:
          def request = given(requestSpec())
                  .body([enabled      : "NOBODY",
                         entitledUsers: ["123"]])
        when:
          def response = request.put("manage/feature-flags/FLAG_1")

        then:
          response.then()
                  .statusCode(HttpStatus.OK.value())
        and:
          assertFeatureFlag("FLAG_1", NOBODY, ["123"] as Set)
    }

    def "should return Not Found when try update non existing feature flag"() {
        given:
          def request = given(requestSpec())
                  .body([enabled      : "NOBODY",
                         entitledUsers: ["123"]])
        when:
          def response = request.put("manage/feature-flags/FLAG_1")

        then:
          response.then()
                  .statusCode(HttpStatus.NOT_FOUND.value())
    }

    def "should return bad request when try update flag with invalid request"() {
        given:
          createFeatureFlag("FLAG_1", "ANYBODY", Set.of())
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
                  [enabled: "RESTRICTED", entitledUsers: []]
          ]
    }

    def "should return conflict when try to create another feature flag with same name"() {
        given:
          createFeatureFlag("ABCDE", "ANYBODY")

        and:
          def request = given(requestSpec())
                  .body([name         : "ABCDE",
                         enabled      : "NOBODY",
                         entitledUsers: ["123"]])
        when:
          def response = request.post("manage/feature-flags")

        then:
          response.then()
                  .statusCode(HttpStatus.CONFLICT.value())
    }

    def "should delete feature flag"() {
        given:
          createFeatureFlag("TO_DELETE", "ANYBODY")

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
          createFeatureFlag("FLAG_1", "ANYBODY")
          createFeatureFlag("FLAG_2", "NOBODY")
          createFeatureFlag("FLAG_3", "RESTRICTED", Set.of("123"))

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
          hasItem(dto.definitions, "FLAG_1", "ANYBODY", [])
          hasItem(dto.definitions, "FLAG_2", "NOBODY", [])
          hasItem(dto.definitions, "FLAG_3", "RESTRICTED", ["123"])
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
