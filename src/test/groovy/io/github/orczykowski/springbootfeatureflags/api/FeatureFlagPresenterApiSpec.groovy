package io.github.orczykowski.springbootfeatureflags.api

import io.github.orczykowski.springbootfeatureflags.FeatureFlagEnabledFeatureFlagNameProvider
import io.github.orczykowski.springbootfeatureflags.FeatureFlagName
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification
import spock.lang.Subject

import java.util.stream.Stream

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class FeatureFlagPresenterApiSpec extends Specification {

    static final String PRESENTER_PATH = "/feature-flags"

    FeatureFlagEnabledFeatureFlagNameProvider enabledFeatureFlagNameProvider = Mock()

    @Subject
    FeatureFlagPresenterApi api = new FeatureFlagPresenterApi(enabledFeatureFlagNameProvider)

    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(api).build()

    def "should return enabled feature flags"() {
        given:
          enabledFeatureFlagNameProvider.provide() >> Stream.of(
                  new FeatureFlagName("flag-1"),
                  new FeatureFlagName("flag-2")
          )

        when:
          def result = mockMvc.perform(get(PRESENTER_PATH).accept(MediaType.APPLICATION_JSON))

        then:
          result.andExpect(status().isOk())
                .andExpect(jsonPath('$.featureFlags').isArray())
                .andExpect(jsonPath('$.featureFlags.length()').value(2))
    }

    def "should return empty set when no flags are enabled"() {
        given:
          enabledFeatureFlagNameProvider.provide() >> Stream.empty()

        when:
          def result = mockMvc.perform(get(PRESENTER_PATH).accept(MediaType.APPLICATION_JSON))

        then:
          result.andExpect(status().isOk())
                .andExpect(jsonPath('$.featureFlags').isEmpty())
    }
}
