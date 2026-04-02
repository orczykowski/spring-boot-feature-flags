package io.github.orczykowski.springbootfeatureflags.adapters.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "feature_flag_assignments")
@IdClass(JpaFeatureFlagAssignmentId.class)
public class JpaFeatureFlagAssignmentEntity {

    @Id
    @Column(name = "flag_name", length = 120, nullable = false)
    private String flagName;

    @Id
    @Column(name = "user_id", nullable = false)
    private String userId;

    protected JpaFeatureFlagAssignmentEntity() {
    }

    public JpaFeatureFlagAssignmentEntity(final String flagName, final String userId) {
        this.flagName = flagName;
        this.userId = userId;
    }

    public String getFlagName() {
        return flagName;
    }

    public String getUserId() {
        return userId;
    }
}
