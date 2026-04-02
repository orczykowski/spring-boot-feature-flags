package io.github.orczykowski.springbootfeatureflags.infrastructure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "feature-flags.storage")
public record FeatureFlagStorageProperties(StorageType type,
                                           String tableName,
                                           String collectionName,
                                           String assignmentTableName,
                                           String assignmentCollectionName) {

    private static final String DEFAULT_NAME = "feature_flags";
    private static final String DEFAULT_ASSIGNMENT_NAME = "feature_flag_assignments";

    public FeatureFlagStorageProperties {
        type = (type == null) ? StorageType.PROPERTY : type;
        tableName = (tableName == null || tableName.isBlank()) ? DEFAULT_NAME : tableName;
        collectionName = (collectionName == null || collectionName.isBlank()) ? DEFAULT_NAME : collectionName;
        assignmentTableName = (assignmentTableName == null || assignmentTableName.isBlank()) ? DEFAULT_ASSIGNMENT_NAME : assignmentTableName;
        assignmentCollectionName = (assignmentCollectionName == null || assignmentCollectionName.isBlank()) ? DEFAULT_ASSIGNMENT_NAME : assignmentCollectionName;
    }

    public enum StorageType {
        PROPERTY, JPA, REDIS, MONGODB
    }
}
