package io.github.orczykowski.springbootfeatureflags.adapters.mongo;

import io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinition;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagName;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagState;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "feature_flags")
public class MongoFeatureFlagDocument {

    @Id
    private String name;
    private String enabled;

    protected MongoFeatureFlagDocument() {
    }

    public static MongoFeatureFlagDocument fromDomain(FeatureFlagDefinition definition) {
        var doc = new MongoFeatureFlagDocument();
        doc.name = definition.name().value();
        doc.enabled = definition.enabled().name();
        return doc;
    }

    public FeatureFlagDefinition toDomain() {
        return new FeatureFlagDefinition(
                new FeatureFlagName(name),
                FeatureFlagState.valueOf(enabled)
        );
    }
}
