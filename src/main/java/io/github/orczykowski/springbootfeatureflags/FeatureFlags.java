package io.github.orczykowski.springbootfeatureflags;

import java.util.Objects;
import java.util.Set;

public record FeatureFlags(Set<FeatureFlagName> flags) {

    private static final FeatureFlags EMPTY = new FeatureFlags(Set.of());

    public FeatureFlags {
        flags = Objects.requireNonNullElse(flags, Set.of());
    }

    public static FeatureFlags empty() {
        return EMPTY;
    }

    public static FeatureFlags of(final Set<FeatureFlagName> flags) {
        if (flags == null || flags.isEmpty()) {
            return EMPTY;
        }
        return new FeatureFlags(Set.copyOf(flags));
    }

    public boolean contains(final FeatureFlagName flagName) {
        return flags.contains(flagName);
    }

    public boolean isEmpty() {
        return flags.isEmpty();
    }

    public int size() {
        return flags.size();
    }
}
