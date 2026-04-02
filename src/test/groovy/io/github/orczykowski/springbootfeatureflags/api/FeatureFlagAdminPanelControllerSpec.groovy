package io.github.orczykowski.springbootfeatureflags.api

import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification
import spock.lang.Subject

import static org.hamcrest.Matchers.containsString
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class FeatureFlagAdminPanelControllerSpec extends Specification {

    static final String MANAGE_PATH = "/manage/feature-flags"
    static final String ADMIN_PATH = "/feature-flags-admin"

    @Subject
    FeatureFlagAdminPanelController controller = new FeatureFlagAdminPanelController(MANAGE_PATH)

    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build()

    def "should serve admin panel HTML"() {
        when:
          def result = mockMvc.perform(get(ADMIN_PATH).accept("text/html"))

        then:
          result.andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                .andExpect(content().string(containsString("<title>Feature Flags Admin</title>")))
    }

    def "should inject manage API base path into HTML"() {
        when:
          def result = mockMvc.perform(get(ADMIN_PATH))

        then:
          def body = result.andReturn().response.contentAsString
          body.contains(MANAGE_PATH)
          !body.contains("{{MANAGE_API_BASE_PATH}}")
    }

    def "should inline CSS into HTML"() {
        when:
          def result = mockMvc.perform(get(ADMIN_PATH))

        then:
          def body = result.andReturn().response.contentAsString
          body.contains("--bg-primary")
          !body.contains("{{INLINE_CSS}}")
    }

    def "should inline JS into HTML"() {
        when:
          def result = mockMvc.perform(get(ADMIN_PATH))

        then:
          def body = result.andReturn().response.contentAsString
          body.contains("FF_ADMIN_CONFIG")
          body.contains("handleResponse")
          !body.contains("{{INLINE_JS}}")
    }
}
