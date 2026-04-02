package io.github.orczykowski.springbootfeatureflags.adapters.jpa;

import io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinition;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagName;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagState;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "feature_flags")
public class JpaFeatureFlagEntity {

    @Id
    @Column(name = "name", length = 120, nullable = false)
    private String name;

    @Column(name = "enabled", nullable = false)
    @Enumerated(EnumType.STRING)
    private FeatureFlagState enabled;

    protected JpaFeatureFlagEntity() {
    }

    public static JpaFeatureFlagEntity fromDomain(FeatureFlagDefinition definition) {
        var entity = new JpaFeatureFlagEntity();
        entity.name = definition.name().value();
        entity.enabled = definition.enabled();
        return entity;
    }

    public FeatureFlagDefinition toDomain() {
        return new FeatureFlagDefinition(
                new FeatureFlagName(name),
                enabled
        );
    }
}
