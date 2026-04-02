package io.github.orczykowski.springbootfeatureflags.adapters.property;

import io.github.orczykowski.springbootfeatureflags.FeatureFlagEntitledUsers;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagAssignmentRepository;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagName;
import io.github.orczykowski.springbootfeatureflags.FeatureFlags;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagUser;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryFeatureFlagAssignmentRepository implements FeatureFlagAssignmentRepository {

    private final Map<FeatureFlagName, Set<FeatureFlagUser>> assignments = new ConcurrentHashMap<>();

    @Override
    public FeatureFlagEntitledUsers findUsersByFlagName(final FeatureFlagName flagName) {
        return FeatureFlagEntitledUsers.of(assignments.get(flagName));
    }

    @Override
    public FeatureFlags findFlagNamesByUser(final FeatureFlagUser user) {
        var flags = assignments.entrySet().stream()
                .filter(entry -> entry.getValue().contains(user))
                .map(Map.Entry::getKey)
                .collect(Collectors.toUnmodifiableSet());
        return FeatureFlags.of(flags);
    }

    @Override
    public boolean isUserAssigned(final FeatureFlagName flagName, final FeatureFlagUser user) {
        return assignments.getOrDefault(flagName, Set.of()).contains(user);
    }

    @Override
    public void saveAssignments(final FeatureFlagName flagName, final FeatureFlagEntitledUsers users) {
        if (users == null || users.isEmpty()) {
            assignments.remove(flagName);
        } else {
            assignments.put(flagName, Set.copyOf(users.users()));
        }
    }

    @Override
    public void addUser(final FeatureFlagName flagName, final FeatureFlagUser user) {
        assignments.compute(flagName, (key, existing) -> {
            var users = existing == null ? new HashSet<FeatureFlagUser>() : new HashSet<>(existing);
            users.add(user);
            return Set.copyOf(users);
        });
    }

    @Override
    public void removeUser(final FeatureFlagName flagName, final FeatureFlagUser user) {
        assignments.computeIfPresent(flagName, (key, existing) -> {
            var users = new HashSet<>(existing);
            users.remove(user);
            return users.isEmpty() ? null : Set.copyOf(users);
        });
    }

    @Override
    public void removeAllByFlagName(final FeatureFlagName flagName) {
        assignments.remove(flagName);
    }
}
