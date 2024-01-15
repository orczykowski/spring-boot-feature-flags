package io.github.orczykowski.springbootfeatureflags;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@ConfigurationProperties(prefix = "feature-flags")
record FeatureFlagsPropertySource(List<FeatureFlagDefinitionDto> definitions) {
    private static final Logger log = LoggerFactory.getLogger(FeatureFlagsPropertySource.class);

    public FeatureFlagsPropertySource {
        definitions = Objects.requireNonNullElse(definitions, List.of());
        log.debug("all feature flags definitions: {}", definitions);
    }

    record FeatureFlagDefinitionDto(String name, FeatureFlagState enabled, Set<String> entitledUsers) {
        @ConstructorBinding
        public FeatureFlagDefinitionDto {
        }

        FeatureFlagDefinition asDefinition() {
            var users = Objects.requireNonNullElse(entitledUsers, new HashSet<String>()).stream()
                    .map(User::new)
                    .collect(Collectors.toSet());

            return new FeatureFlagDefinition(new FeatureFlagName(name), enabled, users);
        }
    }


}
