package io.github.orczykowski.springbootfeatureflags;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toUnmodifiableSet;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@ConditionalOnExpression(value = "${feature-flags.enabled:false} and ${feature-flags.api.expose.enabled:false}")
class FeatureFlagPresenterApi {
    private final EnabledFeatureFlagNameProvider enabledFeatureFlagNameProvider;

    FeatureFlagPresenterApi(final EnabledFeatureFlagNameProvider enabledFeatureFlagNameProvider) {
        this.enabledFeatureFlagNameProvider = enabledFeatureFlagNameProvider;
    }

    @GetMapping(path = "${feature-flags.api.expose.path:/feature-flags}",
            produces = APPLICATION_JSON_VALUE)
    FeatureFlagsResponse getFeatureFlags() {
        return enabledFeatureFlagNameProvider.provide().map(FeatureFlagName::toString).collect(collectingAndThen(toUnmodifiableSet(), FeatureFlagsResponse::new));
    }

    record FeatureFlagsResponse(Set<String> featureFlags) {
    }
}
