package io.github.orczykowski.springbootfeatureflags.adapters.jpa;

import java.io.Serializable;
import java.util.Objects;

public class JpaFeatureFlagAssignmentId implements Serializable {

    private String flagName;
    private String userId;

    public JpaFeatureFlagAssignmentId() {
    }

    public JpaFeatureFlagAssignmentId(final String flagName, final String userId) {
        this.flagName = flagName;
        this.userId = userId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof JpaFeatureFlagAssignmentId that)) return false;
        return Objects.equals(flagName, that.flagName) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flagName, userId);
    }
}
