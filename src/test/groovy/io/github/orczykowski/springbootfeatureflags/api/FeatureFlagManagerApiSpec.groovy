package io.github.orczykowski.springbootfeatureflags.api

import io.github.orczykowski.springbootfeatureflags.FeatureFlagAssignmentSupplier
import io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinition
import io.github.orczykowski.springbootfeatureflags.FeatureFlagEntitledUsers
import io.github.orczykowski.springbootfeatureflags.FeatureFlagManager
import io.github.orczykowski.springbootfeatureflags.FeatureFlagName
import io.github.orczykowski.springbootfeatureflags.FeatureFlagRepository
import io.github.orczykowski.springbootfeatureflags.FeatureFlagState
import io.github.orczykowski.springbootfeatureflags.FeatureFlagUser
import io.github.orczykowski.springbootfeatureflags.exceptions.FeatureFlagsNotFoundException
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification
import spock.lang.Subject

import java.util.stream.Stream

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class FeatureFlagManagerApiSpec extends Specification {

    static final String BASE_PATH = "/manage/feature-flags"
    static final FeatureFlagName FLAG_NAME = new FeatureFlagName("test-flag")
    static final FeatureFlagUser USER = new FeatureFlagUser("user-1")

    FeatureFlagManager featureFlagManager = Mock()
    FeatureFlagRepository featureFlagRepository = Mock()
    FeatureFlagAssignmentSupplier assignmentSupplier = Mock()

    @Subject
    FeatureFlagManagerApi api = new FeatureFlagManagerApi(featureFlagManager, featureFlagRepository, assignmentSupplier)

    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(api)
            .setControllerAdvice(new FeatureFlagExceptionHandler())
            .build()

    // ==================== CREATE ====================

    def "should create feature flag and return 201"() {
        given:
          def definition = new FeatureFlagDefinition(FLAG_NAME, FeatureFlagState.ANYBODY)
          featureFlagManager.create(_ as FeatureFlagManager.FeatureFlagCommand.CreateFeatureFlag) >> definition

        when:
          def result = mockMvc.perform(post(BASE_PATH)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content('{"name":"test-flag","enabled":"ANYBODY","entitledUsers":[]}'))

        then:
          result.andExpect(status().isCreated())
                .andExpect(jsonPath('$.name').value("test-flag"))
                .andExpect(jsonPath('$.enabled').value("ANYBODY"))
    }

    def "should create restricted flag with entitled users and return 201"() {
        given:
          def definition = new FeatureFlagDefinition(FLAG_NAME, FeatureFlagState.RESTRICTED)
          featureFlagManager.create(_ as FeatureFlagManager.FeatureFlagCommand.CreateFeatureFlag) >> definition

        when:
          def result = mockMvc.perform(post(BASE_PATH)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content('{"name":"test-flag","enabled":"RESTRICTED","entitledUsers":["user-1"]}'))

        then:
          result.andExpect(status().isCreated())
                .andExpect(jsonPath('$.name').value("test-flag"))
                .andExpect(jsonPath('$.enabled').value("RESTRICTED"))
    }

    // ==================== ENABLE ====================

    def "should enable feature flag and return 200"() {
        when:
          def result = mockMvc.perform(put("${BASE_PATH}/test-flag/enable"))

        then:
          result.andExpect(status().isOk())
          1 * featureFlagManager.execute({ it instanceof FeatureFlagManager.FeatureFlagCommand.Enable && it.flagName() == FLAG_NAME })
    }

    def "should return 404 when enabling non-existent flag"() {
        given:
          featureFlagManager.execute(_ as FeatureFlagManager.FeatureFlagCommand.Enable) >> {
              throw new FeatureFlagsNotFoundException(FLAG_NAME)
          }

        when:
          def result = mockMvc.perform(put("${BASE_PATH}/test-flag/enable"))

        then:
          result.andExpect(status().isNotFound())
    }

    // ==================== DISABLE ====================

    def "should disable feature flag and return 200"() {
        when:
          def result = mockMvc.perform(put("${BASE_PATH}/test-flag/disable"))

        then:
          result.andExpect(status().isOk())
          1 * featureFlagManager.execute({ it instanceof FeatureFlagManager.FeatureFlagCommand.Disable && it.flagName() == FLAG_NAME })
    }

    def "should return 404 when disabling non-existent flag"() {
        given:
          featureFlagManager.execute(_ as FeatureFlagManager.FeatureFlagCommand.Disable) >> {
              throw new FeatureFlagsNotFoundException(FLAG_NAME)
          }

        when:
          def result = mockMvc.perform(put("${BASE_PATH}/test-flag/disable"))

        then:
          result.andExpect(status().isNotFound())
    }

    // ==================== RESTRICT ====================

    def "should restrict feature flag and return 200"() {
        when:
          def result = mockMvc.perform(put("${BASE_PATH}/test-flag/restrict"))

        then:
          result.andExpect(status().isOk())
          1 * featureFlagManager.execute({ it instanceof FeatureFlagManager.FeatureFlagCommand.Restrict && it.flagName() == FLAG_NAME })
    }

    def "should return 404 when restricting non-existent flag"() {
        given:
          featureFlagManager.execute(_ as FeatureFlagManager.FeatureFlagCommand.Restrict) >> {
              throw new FeatureFlagsNotFoundException(FLAG_NAME)
          }

        when:
          def result = mockMvc.perform(put("${BASE_PATH}/test-flag/restrict"))

        then:
          result.andExpect(status().isNotFound())
    }

    // ==================== ADD USER ====================

    def "should add restricted user and return 201"() {
        when:
          def result = mockMvc.perform(post("${BASE_PATH}/test-flag/users/user-1"))

        then:
          result.andExpect(status().isCreated())
          1 * featureFlagManager.execute({ it instanceof FeatureFlagManager.FeatureFlagCommand.AddRestrictedUser
                  && it.flagName() == FLAG_NAME && it.user() == USER })
    }

    // ==================== REMOVE USER ====================

    def "should remove restricted user and return 204"() {
        when:
          def result = mockMvc.perform(delete("${BASE_PATH}/test-flag/users/user-1"))

        then:
          result.andExpect(status().isNoContent())
          1 * featureFlagManager.execute({ it instanceof FeatureFlagManager.FeatureFlagCommand.RemoveRestrictedUser
                  && it.flagName() == FLAG_NAME && it.user() == USER })
    }

    // ==================== DELETE ====================

    def "should delete feature flag and return 204"() {
        when:
          def result = mockMvc.perform(delete("${BASE_PATH}/test-flag"))

        then:
          result.andExpect(status().isNoContent())
          1 * featureFlagManager.execute({ it instanceof FeatureFlagManager.FeatureFlagCommand.DeleteFeatureFlag && it.flagName() == FLAG_NAME })
    }

    // ==================== LIST ALL ====================

    def "should list all feature flags"() {
        given:
          def definition = new FeatureFlagDefinition(FLAG_NAME, FeatureFlagState.ANYBODY)
          featureFlagRepository.findAll() >> Stream.of(definition)
          assignmentSupplier.findUsersByFlagName(FLAG_NAME) >> FeatureFlagEntitledUsers.empty()

        when:
          def result = mockMvc.perform(get(BASE_PATH).accept(MediaType.APPLICATION_JSON))

        then:
          result.andExpect(status().isOk())
                .andExpect(jsonPath('$.definitions[0].name').value("test-flag"))
                .andExpect(jsonPath('$.definitions[0].enabled').value("ANYBODY"))
    }

    def "should return empty list when no flags exist"() {
        given:
          featureFlagRepository.findAll() >> Stream.empty()

        when:
          def result = mockMvc.perform(get(BASE_PATH).accept(MediaType.APPLICATION_JSON))

        then:
          result.andExpect(status().isOk())
                .andExpect(jsonPath('$.definitions').isEmpty())
    }

    // ==================== STATE TRANSITIONS ====================

    def "should transition from ANYBODY to RESTRICTED via restrict endpoint"() {
        when:
          def result = mockMvc.perform(put("${BASE_PATH}/test-flag/restrict"))

        then:
          result.andExpect(status().isOk())
          1 * featureFlagManager.execute({ it instanceof FeatureFlagManager.FeatureFlagCommand.Restrict })
    }

    def "should transition from ANYBODY to NOBODY via disable endpoint"() {
        when:
          def result = mockMvc.perform(put("${BASE_PATH}/test-flag/disable"))

        then:
          result.andExpect(status().isOk())
          1 * featureFlagManager.execute({ it instanceof FeatureFlagManager.FeatureFlagCommand.Disable })
    }

    def "should transition from NOBODY to ANYBODY via enable endpoint"() {
        when:
          def result = mockMvc.perform(put("${BASE_PATH}/test-flag/enable"))

        then:
          result.andExpect(status().isOk())
          1 * featureFlagManager.execute({ it instanceof FeatureFlagManager.FeatureFlagCommand.Enable })
    }

    def "should transition from NOBODY to RESTRICTED via restrict endpoint"() {
        when:
          def result = mockMvc.perform(put("${BASE_PATH}/test-flag/restrict"))

        then:
          result.andExpect(status().isOk())
          1 * featureFlagManager.execute({ it instanceof FeatureFlagManager.FeatureFlagCommand.Restrict })
    }

    def "should transition from RESTRICTED to ANYBODY via enable endpoint"() {
        when:
          def result = mockMvc.perform(put("${BASE_PATH}/test-flag/enable"))

        then:
          result.andExpect(status().isOk())
          1 * featureFlagManager.execute({ it instanceof FeatureFlagManager.FeatureFlagCommand.Enable })
    }

    def "should transition from RESTRICTED to NOBODY via disable endpoint"() {
        when:
          def result = mockMvc.perform(put("${BASE_PATH}/test-flag/disable"))

        then:
          result.andExpect(status().isOk())
          1 * featureFlagManager.execute({ it instanceof FeatureFlagManager.FeatureFlagCommand.Disable })
    }
}
