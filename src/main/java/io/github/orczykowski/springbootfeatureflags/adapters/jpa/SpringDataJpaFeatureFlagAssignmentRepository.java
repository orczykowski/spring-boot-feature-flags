package io.github.orczykowski.springbootfeatureflags.adapters.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Spring Data JPA repository for feature flag user assignment entities.
 */
public interface SpringDataJpaFeatureFlagAssignmentRepository
        extends JpaRepository<JpaFeatureFlagAssignmentEntity, JpaFeatureFlagAssignmentId> {

    /**
     * Finds all assignments for the given feature flag name.
     *
     * @param flagName the feature flag name
     * @return list of assignment entities
     */
    List<JpaFeatureFlagAssignmentEntity> findByFlagName(final String flagName);

    /**
     * Finds all assignments for the given user ID.
     *
     * @param userId the user ID
     * @return list of assignment entities
     */
    List<JpaFeatureFlagAssignmentEntity> findByUserId(final String userId);

    /**
     * Deletes all assignments for the given feature flag name.
     *
     * @param flagName the feature flag name
     */
    @Transactional
    void deleteByFlagName(final String flagName);

    /**
     * Deletes the assignment for a specific user and feature flag.
     *
     * @param flagName the feature flag name
     * @param userId the user ID
     */
    @Transactional
    void deleteByFlagNameAndUserId(final String flagName, final String userId);

    /**
     * Checks whether an assignment exists for the given feature flag and user.
     *
     * @param flagName the feature flag name
     * @param userId the user ID
     * @return {@code true} if the assignment exists, {@code false} otherwise
     */
    boolean existsByFlagNameAndUserId(final String flagName, final String userId);
}
