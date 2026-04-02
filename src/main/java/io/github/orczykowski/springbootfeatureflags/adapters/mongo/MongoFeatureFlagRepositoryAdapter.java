package io.github.orczykowski.springbootfeatureflags.adapters.mongo;

import io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinition;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagName;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagRepository;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagState;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Optional;
import java.util.stream.Stream;

public class MongoFeatureFlagRepositoryAdapter implements FeatureFlagRepository {

    private final MongoTemplate mongoTemplate;
    private final String collectionName;

    public MongoFeatureFlagRepositoryAdapter(final MongoTemplate mongoTemplate, final String collectionName) {
        this.mongoTemplate = mongoTemplate;
        this.collectionName = collectionName;
    }

    @Override
    public Stream<FeatureFlagDefinition> findAllEnabledFeatureFlags() {
        var query = Query.query(Criteria.where("enabled").ne(FeatureFlagState.NOBODY.name()));
        return mongoTemplate.find(query, MongoFeatureFlagDocument.class, collectionName).stream()
                .map(MongoFeatureFlagDocument::toDomain);
    }

    @Override
    public Optional<FeatureFlagDefinition> findByName(final FeatureFlagName featureFlagName) {
        var doc = mongoTemplate.findById(featureFlagName.value(), MongoFeatureFlagDocument.class, collectionName);
        return Optional.ofNullable(doc).map(MongoFeatureFlagDocument::toDomain);
    }

    @Override
    public void removeByName(final FeatureFlagName flagName) {
        var query = Query.query(Criteria.where("_id").is(flagName.value()));
        mongoTemplate.remove(query, MongoFeatureFlagDocument.class, collectionName);
    }

    @Override
    public FeatureFlagDefinition save(final FeatureFlagDefinition definition) {
        mongoTemplate.save(MongoFeatureFlagDocument.fromDomain(definition), collectionName);
        return definition;
    }

    @Override
    public Stream<FeatureFlagDefinition> findAll() {
        return mongoTemplate.findAll(MongoFeatureFlagDocument.class, collectionName).stream()
                .map(MongoFeatureFlagDocument::toDomain);
    }
}
