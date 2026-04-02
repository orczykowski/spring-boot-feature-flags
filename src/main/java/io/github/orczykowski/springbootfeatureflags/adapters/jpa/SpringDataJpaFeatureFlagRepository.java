package io.github.orczykowski.springbootfeatureflags.adapters.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for feature flag entities.
 */
public interface SpringDataJpaFeatureFlagRepository extends JpaRepository<JpaFeatureFlagEntity, String> {
}
