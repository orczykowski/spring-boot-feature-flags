package io.github.orczykowski.springbootfeatureflags


import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TestAppConfiguration {
    static final FeatureFlagDefinition.User USER = new FeatureFlagDefinition.User("213")

    @Bean
    UserContextProvider userContextProvider() {
        return new UserContextProvider() {
            @Override
            Optional<FeatureFlagDefinition.User> provide() {
                Optional.ofNullable(USER)
            }
        }
    }
}
