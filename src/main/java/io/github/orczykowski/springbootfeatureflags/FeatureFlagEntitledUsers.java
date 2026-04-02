package io.github.orczykowski.springbootfeatureflags;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public record FeatureFlagEntitledUsers(Set<FeatureFlagUser> users) {

    private static final FeatureFlagEntitledUsers EMPTY = new FeatureFlagEntitledUsers(Set.of());

    public FeatureFlagEntitledUsers {
        users = Objects.requireNonNullElse(users, Set.of());
    }

    public static FeatureFlagEntitledUsers empty() {
        return EMPTY;
    }

    public static FeatureFlagEntitledUsers of(final Set<FeatureFlagUser> users) {
        if (users == null || users.isEmpty()) {
            return EMPTY;
        }
        return new FeatureFlagEntitledUsers(Set.copyOf(users));
    }

    public boolean isEmpty() {
        return users.isEmpty();
    }

    public boolean contains(final FeatureFlagUser user) {
        return users.contains(user);
    }

    public Stream<FeatureFlagUser> stream() {
        return users.stream();
    }

    public int size() {
        return users.size();
    }
}
