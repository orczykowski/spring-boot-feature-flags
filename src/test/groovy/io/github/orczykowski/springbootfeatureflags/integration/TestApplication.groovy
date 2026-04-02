package io.github.orczykowski.springbootfeatureflags.integration

import org.springframework.boot.autoconfigure.AutoConfigurationPackage
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication(scanBasePackages = "io.github.orczykowski.springbootfeatureflags")
@AutoConfigurationPackage(basePackages = "io.github.orczykowski.springbootfeatureflags")
class TestApplication {
}
