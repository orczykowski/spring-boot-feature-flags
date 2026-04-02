package io.github.orczykowski.springbootfeatureflags.integration

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer

@SpringBootTest(
        classes = TestApplication,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("jpa-it")
class JpaStorageIntegrationSpec extends AbstractFeatureFlagIntegrationSpec {

    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine")
            .withDatabaseName("feature_flags_jpa_test")
            .withUsername("test")
            .withPassword("test")

    static {
        postgres.start()
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl)
        registry.add("spring.datasource.username", postgres::getUsername)
        registry.add("spring.datasource.password", postgres::getPassword)
        registry.add("spring.datasource.driver-class-name", { -> "org.postgresql.Driver" })
    }
}
