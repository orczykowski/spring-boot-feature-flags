package io.github.orczykowski.springbootfeatureflags


import io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinition.FeatureFlagName
import io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinition.FeatureFlagState
import io.restassured.builder.RequestSpecBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
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
class BaseIntegrationSpec extends Specification {
    @LocalServerPort
    int port

    @Autowired
    FeatureFlagRepository repository

    def setup() {
        repository.findAll()
                .forEach(it -> repository.removeByName(it.name()))
    }

    def requestSpec() {
        new RequestSpecBuilder()
                .addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBaseUri("http://localhost:$port")
                .build()
    }

    def assertFeatureFlag(String name, FeatureFlagState enabled, Set<String> users = Set.of()) {
        assertFeatureFlag(new FeatureFlagName(name), enabled, users.collect { new FeatureFlagDefinition.User(it) } as Set<FeatureFlagDefinition.User>)
    }

    def assertFeatureFlag(FeatureFlagName name, FeatureFlagState enabled, Set<FeatureFlagDefinition.User> entitledUsers = Set.of()) {
        def maybeFlag = repository.findByName(name)
        assert maybeFlag.isPresent()
        def flag = maybeFlag.get()
        assert flag.enabled() == enabled
        assert flag.entitledUsers() == entitledUsers
        true
    }

    def createFeatureFlag(String name, String enabled, Set<String> users = Set.of()) {
        repository.save(new FeatureFlagDefinition(
                new FeatureFlagName(name),
                FeatureFlagState.valueOf(enabled),
                users.collect { new FeatureFlagDefinition.User(it) } as Set<FeatureFlagDefinition.User>))
    }
}
