package io.github.orczykowski.springbootfeatureflags.integration

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

@SpringBootTest(
        classes = TestApplication,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("redis-it")
class RedisStorageIntegrationSpec extends AbstractFeatureFlagIntegrationSpec {

    static GenericContainer redis = new GenericContainer(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379)

    static {
        redis.start()
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost)
        registry.add("spring.data.redis.port", { -> redis.getMappedPort(6379) })
    }
}
