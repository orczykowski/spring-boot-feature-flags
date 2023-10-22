package io.github.orczykowski.springbootfeatureflags

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ActiveProfiles("integration")
@ContextConfiguration
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = [
                TestSpringBootApplication,
                FeatureFlagsConfiguration,
                TestAppConfiguration])
class BaseE2ESpecification extends Specification {
    TestRestTemplate restTemplate = new TestRestTemplate()

    @LocalServerPort
    int port

    def apiUrl(String resource) { "http://localhost:$port/$resource" }
}
