package io.github.orczykowski.springbootfeatureflags.api

import io.github.orczykowski.springbootfeatureflags.BaseIntegrationSpec
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource

import static org.springframework.http.RequestEntity.get

@TestPropertySource(properties = ['feature-flags.api.base-path=my-feature-flags'])
class FeatureFlagCustomEndpointE2eSpec extends BaseIntegrationSpec {

    def "should present ony enabled feature flags names on api"() {
        given:
          def request = get(apiUrl("my-feature-flags"))
                  .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                  .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                  .build()
        when:
          def response = restTemplate.exchange(request, Map)
        then:
          response.statusCode == HttpStatus.OK
          response.body != null
    }
}
