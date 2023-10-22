package io.github.orczykowski.springbootfeatureflags


import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TestAppConfiguration {
    static final User USER = new User("213")

    @Bean
    UserContextProvider userContextProvider() {
        return new UserContextProvider() {
            @Override
            Optional<User> provide() {
                Optional.ofNullable(USER)
            }
        }
    }
}
