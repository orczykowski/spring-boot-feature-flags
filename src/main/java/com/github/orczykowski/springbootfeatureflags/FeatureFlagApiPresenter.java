package com.github.orczykowski.springbootfeatureflags;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toUnmodifiableSet;

@RestController
@RequestMapping("${feature-flags.api.expose.endpoint}")
@ConditionalOnExpression("${feature-flags.enabled}  and ${feature-flags.api.expose.enabled}")
public class FeatureFlagApiPresenter {

    private final FeatureFlagProvider featureFlagProvider;

    FeatureFlagApiPresenter(final FeatureFlagProvider featureFlagProvider) {
        this.featureFlagProvider = featureFlagProvider;
    }

    @GetMapping()
    public FeatureFlagsResponse getFeatureFlags() {
        return featureFlagProvider
                .provide()
                .map(FeatureFlagName::toString)
                .collect(collectingAndThen(toUnmodifiableSet(), FeatureFlagsResponse::new));
    }


    record FeatureFlagsResponse(Set<String> featureFlags) {
    }
}
