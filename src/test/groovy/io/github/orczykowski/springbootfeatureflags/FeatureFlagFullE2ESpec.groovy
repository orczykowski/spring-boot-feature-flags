package io.github.orczykowski.springbootfeatureflags

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

import static org.springframework.http.RequestEntity.get

class FeatureFlagFullE2ESpec extends BaseE2ESpecification {

    def "should present ony enabled feature flags on api"() {
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
