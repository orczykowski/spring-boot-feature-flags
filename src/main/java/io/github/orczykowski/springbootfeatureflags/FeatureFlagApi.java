package io.github.orczykowski.springbootfeatureflags;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toUnmodifiableSet;

@RestController
//@RequestMapping("${feature-flags.api.expose.endpoint}")
@RequestMapping("/feature-flags")
@ConditionalOnExpression("${feature-flags.enabled} and ${feature-flags.api.expose.enabled}")
public class FeatureFlagApi {
    private final FeatureFlagProvider featureFlagProvider;

    FeatureFlagApi(final FeatureFlagProvider featureFlagProvider) {
        this.featureFlagProvider = featureFlagProvider;
    }

    @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public FeatureFlagsResponse getFeatureFlags() {
        return featureFlagProvider
                .provide()
                .map(FeatureFlagName::toString)
                .collect(collectingAndThen(toUnmodifiableSet(), FeatureFlagsResponse::new));
    }


    record FeatureFlagsResponse(Set<String> featureFlags) {
    }
}
