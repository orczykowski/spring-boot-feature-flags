package io.github.orczykowski.springbootfeatureflags;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;

public class FeatureFlagVerifier {

    private static final Logger log = LoggerFactory.getLogger(FeatureFlagVerifier.class);

    private final UserContextProvider userContextProvider;
    private final FeatureFlagRepository featureFlagRepository;


    public FeatureFlagVerifier(final FeatureFlagRepository featureFlagRepository,
                               final UserContextProvider userContextProvider) {
        this.featureFlagRepository = featureFlagRepository;
        this.userContextProvider = userContextProvider;
    }

    public boolean verify(final FeatureFlagName featureFlagName) {
        if (Objects.isNull(featureFlagName)) {
            log.warn("Try verify null feature flag name.");
            return false;
        }
        return featureFlagRepository.findDefinition(featureFlagName)
                .map(this::check)
                .orElseGet(() -> noFlagFound(featureFlagName));
    }

    private Boolean check(final FeatureFlagDefinition flag) {
        return maybeUserContextProvider()
                .flatMap(UserContextProvider::provide)
                .map(flag::isEnableForUser)
                .orElseGet(flag::isEnableForALlUser);
    }

    private Optional<UserContextProvider> maybeUserContextProvider() {
        return Optional.ofNullable(userContextProvider);
    }

    private boolean noFlagFound(FeatureFlagName flagName) {
        log.warn("Flag {} definition does not exist.", flagName);
        return false;
    }

}
