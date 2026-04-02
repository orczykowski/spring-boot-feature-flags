package io.github.orczykowski.springbootfeatureflags.integration

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(
        classes = TestApplication,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("property-it")
class PropertyStorageIntegrationSpec extends AbstractFeatureFlagIntegrationSpec {
}
