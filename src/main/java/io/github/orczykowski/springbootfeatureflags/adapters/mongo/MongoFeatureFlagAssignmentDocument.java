package io.github.orczykowski.springbootfeatureflags.adapters.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document(collection = "feature_flag_assignments")
public class MongoFeatureFlagAssignmentDocument {

    @Id
    private String flagName;
    private Set<String> userIds;

    protected MongoFeatureFlagAssignmentDocument() {
    }

    public MongoFeatureFlagAssignmentDocument(final String flagName, final Set<String> userIds) {
        this.flagName = flagName;
        this.userIds = userIds;
    }

    public String getFlagName() {
        return flagName;
    }

    public Set<String> getUserIds() {
        return userIds;
    }
}
