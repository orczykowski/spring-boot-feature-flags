package io.github.orczykowski.springbootfeatureflags.integration

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MongoDBContainer

@SpringBootTest(
        classes = TestApplication,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("mongo-it")
class MongoStorageIntegrationSpec extends AbstractFeatureFlagIntegrationSpec {

    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0")

    static {
        mongo.start()
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.mongodb.uri", mongo::getReplicaSetUrl)
    }
}
