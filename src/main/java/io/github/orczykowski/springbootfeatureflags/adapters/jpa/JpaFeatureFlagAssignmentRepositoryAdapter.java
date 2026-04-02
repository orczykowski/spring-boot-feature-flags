package io.github.orczykowski.springbootfeatureflags.adapters.jpa;

import io.github.orczykowski.springbootfeatureflags.FeatureFlagEntitledUsers;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagAssignmentRepository;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagName;
import io.github.orczykowski.springbootfeatureflags.FeatureFlags;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagUser;

import java.util.Set;
import java.util.stream.Collectors;

public class JpaFeatureFlagAssignmentRepositoryAdapter implements FeatureFlagAssignmentRepository {

    private final SpringDataJpaFeatureFlagAssignmentRepository jpaRepository;

    public JpaFeatureFlagAssignmentRepositoryAdapter(final SpringDataJpaFeatureFlagAssignmentRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public FeatureFlagEntitledUsers findUsersByFlagName(final FeatureFlagName flagName) {
        Set<FeatureFlagUser> users = jpaRepository.findByFlagName(flagName.value()).stream()
                .map(entity -> new FeatureFlagUser(entity.getUserId()))
                .collect(Collectors.toUnmodifiableSet());
        return FeatureFlagEntitledUsers.of(users);
    }

    @Override
    public FeatureFlags findFlagNamesByUser(final FeatureFlagUser user) {
        Set<FeatureFlagName> flags = jpaRepository.findByUserId(user.id()).stream()
                .map(entity -> new FeatureFlagName(entity.getFlagName()))
                .collect(Collectors.toUnmodifiableSet());
        return FeatureFlags.of(flags);
    }

    @Override
    public boolean isUserAssigned(final FeatureFlagName flagName, final FeatureFlagUser user) {
        return jpaRepository.existsByFlagNameAndUserId(flagName.value(), user.id());
    }

    @Override
    public void saveAssignments(final FeatureFlagName flagName, final FeatureFlagEntitledUsers users) {
        jpaRepository.deleteByFlagName(flagName.value());
        if (!users.isEmpty()) {
            var entities = users.stream()
                    .map(user -> new JpaFeatureFlagAssignmentEntity(flagName.value(), user.id()))
                    .toList();
            jpaRepository.saveAll(entities);
        }
    }

    @Override
    public void addUser(final FeatureFlagName flagName, final FeatureFlagUser user) {
        if (!isUserAssigned(flagName, user)) {
            jpaRepository.save(new JpaFeatureFlagAssignmentEntity(flagName.value(), user.id()));
        }
    }

    @Override
    public void removeUser(final FeatureFlagName flagName, final FeatureFlagUser user) {
        jpaRepository.deleteByFlagNameAndUserId(flagName.value(), user.id());
    }

    @Override
    public void removeAllByFlagName(final FeatureFlagName flagName) {
        jpaRepository.deleteByFlagName(flagName.value());
    }
}
