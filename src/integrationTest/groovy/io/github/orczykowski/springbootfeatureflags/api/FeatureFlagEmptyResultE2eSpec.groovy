package io.github.orczykowski.springbootfeatureflags.api

import io.github.orczykowski.springbootfeatureflags.BaseIntegrationSpec
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource

import static org.springframework.http.RequestEntity.get

@TestPropertySource(properties = ['feature-flags.definitions='])
class FeatureFlagEmptyResultE2eSpec extends BaseIntegrationSpec {

    def "should respond with empty list when is no defined feature flags"() {
        given:
          def request = get(apiUrl("feature-flags"))
                  .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                  .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                  .build()
        when:
          def response = restTemplate.exchange(request, Map)
        then:
          response.statusCode == HttpStatus.OK
          response.body != null
        and:
          def body = response.body
          body["featureFlags"] != null
          body["featureFlags"] == []
    }
}
