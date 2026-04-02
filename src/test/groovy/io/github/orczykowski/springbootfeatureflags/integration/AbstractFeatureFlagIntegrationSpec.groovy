package io.github.orczykowski.springbootfeatureflags.integration

import io.github.orczykowski.springbootfeatureflags.FeatureFlagName
import io.github.orczykowski.springbootfeatureflags.FeatureFlagVerifier
import io.github.orczykowski.springbootfeatureflags.FeatureFlagUser
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import spock.lang.Specification
import spock.lang.Stepwise

@Stepwise
abstract class AbstractFeatureFlagIntegrationSpec extends Specification {

    static final String FLAG_NAME = "INTEGRATION_TEST_FLAG"
    static final String ENTITLED_USER_ID = "user-42"
    static final String OTHER_USER_ID = "user-99"
    static final String MANAGE_PATH = "/manage/feature-flags"
    static final String PRESENTER_PATH = "/feature-flags"

    @LocalServerPort
    int port

    @Autowired
    FeatureFlagVerifier verifier

    @Autowired
    TestFeatureFlagUserContextProvider testUserContextProvider

    def setup() {
        RestAssured.port = port
    }

    // ==================== CREATE ====================

    def "should create a new feature flag enabled for everybody"() {
        given: "a request to create a flag with ANYBODY state"
            def requestBody = [name: FLAG_NAME, enabled: "ANYBODY", entitledUsers: []]

        when: "the create endpoint is called"
            def response = RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(requestBody)
                    .post(MANAGE_PATH)

        then: "the flag is created successfully with status 201"
            response.statusCode() == HttpStatus.CREATED.value()
            response.jsonPath().getString("name") == FLAG_NAME
            response.jsonPath().getString("enabled") == "ANYBODY"
    }

    def "should return 409 conflict when creating a flag with duplicate name"() {
        given: "a request with the same flag name that already exists"
            def requestBody = [name: FLAG_NAME, enabled: "ANYBODY", entitledUsers: []]

        when: "the create endpoint is called again"
            def response = RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(requestBody)
                    .post(MANAGE_PATH)

        then: "the server returns 409 conflict"
            response.statusCode() == HttpStatus.CONFLICT.value()
    }

    // ==================== VERIFY: ANYBODY ====================

    def "should verify that flag enabled for everybody returns true"() {
        given: "no specific user context is set"
            testUserContextProvider.clearUser()

        when: "the flag is verified"
            def result = verifier.verify(new FeatureFlagName(FLAG_NAME))

        then: "verification returns true because the flag is enabled for everybody"
            result == true
    }

    // ==================== PRESENTER API ====================

    def "should return the enabled flag in the presenter API response"() {
        when: "the presenter endpoint is called"
            def response = RestAssured.given()
                    .accept(ContentType.JSON)
                    .get(PRESENTER_PATH)

        then: "the response contains the created flag"
            response.statusCode() == HttpStatus.OK.value()
            response.jsonPath().getList("featureFlags").contains(FLAG_NAME)
    }

    // ==================== DISABLE ====================

    def "should disable a feature flag via management API"() {
        when: "the disable endpoint is called"
            def response = RestAssured.given()
                    .put("${MANAGE_PATH}/${FLAG_NAME}/disable")

        then: "the flag is disabled successfully"
            response.statusCode() == HttpStatus.OK.value()
    }

    def "should verify that disabled flag returns false"() {
        when: "the disabled flag is verified"
            def result = verifier.verify(new FeatureFlagName(FLAG_NAME))

        then: "verification returns false because the flag is disabled"
            result == false
    }

    def "should exclude disabled flag from presenter API response"() {
        when: "the presenter endpoint is called"
            def response = RestAssured.given()
                    .accept(ContentType.JSON)
                    .get(PRESENTER_PATH)

        then: "the response does not contain the disabled flag"
            response.statusCode() == HttpStatus.OK.value()
            !response.jsonPath().getList("featureFlags").contains(FLAG_NAME)
    }

    // ==================== RESTRICTED: ADD USER ====================

    def "should add a restricted user to the feature flag"() {
        when: "a user is added to the flag's restricted list"
            def response = RestAssured.given()
                    .post("${MANAGE_PATH}/${FLAG_NAME}/users/${ENTITLED_USER_ID}")

        then: "the operation succeeds"
            response.statusCode() == HttpStatus.CREATED.value()
    }

    def "should verify restricted flag returns true for the entitled user"() {
        given: "the user context is set to the entitled user"
            testUserContextProvider.setUser(new FeatureFlagUser(ENTITLED_USER_ID))

        when: "the flag is verified"
            def result = verifier.verify(new FeatureFlagName(FLAG_NAME))

        then: "verification returns true because the user is in the restricted list"
            result == true
    }

    def "should verify restricted flag returns false for a different user"() {
        given: "the user context is set to a user who is NOT in the restricted list"
            testUserContextProvider.setUser(new FeatureFlagUser(OTHER_USER_ID))

        when: "the flag is verified"
            def result = verifier.verify(new FeatureFlagName(FLAG_NAME))

        then: "verification returns false because the user is not entitled"
            result == false
    }

    def "should verify restricted flag returns false when no user context is set"() {
        given: "no user context is available"
            testUserContextProvider.clearUser()

        when: "the flag is verified"
            def result = verifier.verify(new FeatureFlagName(FLAG_NAME))

        then: "verification returns false because restricted flags require a user context"
            result == false
    }

    // ==================== RESTRICT ====================

    def "should restrict a feature flag via management API"() {
        when: "the restrict endpoint is called"
            def response = RestAssured.given()
                    .put("${MANAGE_PATH}/${FLAG_NAME}/restrict")

        then: "the flag is restricted successfully"
            response.statusCode() == HttpStatus.OK.value()
    }

    def "should verify that restricted flag returns false without user context"() {
        given: "no user context is available"
            testUserContextProvider.clearUser()

        when: "the restricted flag is verified"
            def result = verifier.verify(new FeatureFlagName(FLAG_NAME))

        then: "verification returns false because restricted flags require a user context"
            result == false
    }

    // ==================== RE-ENABLE ====================

    def "should re-enable a previously restricted feature flag"() {
        when: "the enable endpoint is called"
            def response = RestAssured.given()
                    .put("${MANAGE_PATH}/${FLAG_NAME}/enable")

        then: "the flag is enabled successfully"
            response.statusCode() == HttpStatus.OK.value()
    }

    def "should verify that re-enabled flag returns true"() {
        when: "the re-enabled flag is verified"
            def result = verifier.verify(new FeatureFlagName(FLAG_NAME))

        then: "verification returns true because the flag is enabled for everybody again"
            result == true
    }

    // ==================== LIST ALL ====================

    def "should list all feature flags via management API"() {
        when: "the list endpoint is called"
            def response = RestAssured.given()
                    .accept(ContentType.JSON)
                    .get(MANAGE_PATH)

        then: "the response contains the created flag"
            response.statusCode() == HttpStatus.OK.value()
            response.jsonPath().getList("definitions.name").contains(FLAG_NAME)
    }

    // ==================== REMOVE USER ====================

    def "should remove a restricted user from the feature flag"() {
        when: "the user is removed from the flag's restricted list"
            def response = RestAssured.given()
                    .delete("${MANAGE_PATH}/${FLAG_NAME}/users/${ENTITLED_USER_ID}")

        then: "the operation succeeds"
            response.statusCode() == HttpStatus.NO_CONTENT.value()
    }

    // ==================== DELETE ====================

    def "should delete the feature flag and all its assignments"() {
        when: "the delete endpoint is called"
            def response = RestAssured.given()
                    .delete("${MANAGE_PATH}/${FLAG_NAME}")

        then: "the flag is deleted with status 204"
            response.statusCode() == HttpStatus.NO_CONTENT.value()
    }

    def "should verify that deleted flag returns false"() {
        when: "the deleted flag is verified"
            def result = verifier.verify(new FeatureFlagName(FLAG_NAME))

        then: "verification returns false because the flag no longer exists"
            result == false
    }

    // ==================== ERROR HANDLING ====================

    def "should return 404 when enabling a non-existent flag"() {
        when: "the enable endpoint is called for a flag that does not exist"
            def response = RestAssured.given()
                    .put("${MANAGE_PATH}/NON_EXISTENT_FLAG/enable")

        then: "the server returns 404 not found"
            response.statusCode() == HttpStatus.NOT_FOUND.value()
    }

    def "should return 404 when restricting a non-existent flag"() {
        when: "the restrict endpoint is called for a flag that does not exist"
            def response = RestAssured.given()
                    .put("${MANAGE_PATH}/NON_EXISTENT_FLAG/restrict")

        then: "the server returns 404 not found"
            response.statusCode() == HttpStatus.NOT_FOUND.value()
    }

    def cleanup() {
        testUserContextProvider.clearUser()
    }
}
