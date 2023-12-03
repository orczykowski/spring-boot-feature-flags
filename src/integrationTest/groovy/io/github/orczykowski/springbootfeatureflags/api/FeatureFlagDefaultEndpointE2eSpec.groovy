package io.github.orczykowski.springbootfeatureflags.api

import io.github.orczykowski.springbootfeatureflags.BaseIntegrationSpec
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

import static org.springframework.http.RequestEntity.get

class FeatureFlagDefaultEndpointE2eSpec extends BaseIntegrationSpec {

    def "should present ony enabled feature flags names on api"() {
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
          body["featureFlags"].sort() == ["FLAG_1", "FLAG_2"]
    }
}
