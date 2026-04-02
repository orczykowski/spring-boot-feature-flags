package io.github.orczykowski.springbootfeatureflags.adapters.mongo;

import io.github.orczykowski.springbootfeatureflags.FeatureFlagEntitledUsers;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagAssignmentRepository;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagName;
import io.github.orczykowski.springbootfeatureflags.FeatureFlags;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagUser;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Set;
import java.util.stream.Collectors;

public class MongoFeatureFlagAssignmentRepositoryAdapter implements FeatureFlagAssignmentRepository {

    private final MongoTemplate mongoTemplate;
    private final String collectionName;

    public MongoFeatureFlagAssignmentRepositoryAdapter(final MongoTemplate mongoTemplate, final String collectionName) {
        this.mongoTemplate = mongoTemplate;
        this.collectionName = collectionName;
    }

    @Override
    public FeatureFlagEntitledUsers findUsersByFlagName(final FeatureFlagName flagName) {
        var doc = mongoTemplate.findById(flagName.value(), MongoFeatureFlagAssignmentDocument.class, collectionName);
        if (doc == null || doc.getUserIds() == null) {
            return FeatureFlagEntitledUsers.empty();
        }
        Set<FeatureFlagUser> users = doc.getUserIds().stream()
                .map(FeatureFlagUser::new)
                .collect(Collectors.toUnmodifiableSet());
        return FeatureFlagEntitledUsers.of(users);
    }

    @Override
    public FeatureFlags findFlagNamesByUser(final FeatureFlagUser user) {
        var query = Query.query(Criteria.where("userIds").is(user.id()));
        var docs = mongoTemplate.find(query, MongoFeatureFlagAssignmentDocument.class, collectionName);
        Set<FeatureFlagName> flags = docs.stream()
                .map(doc -> new FeatureFlagName(doc.getFlagName()))
                .collect(Collectors.toUnmodifiableSet());
        return FeatureFlags.of(flags);
    }

    @Override
    public boolean isUserAssigned(final FeatureFlagName flagName, final FeatureFlagUser user) {
        var query = Query.query(
                Criteria.where("_id").is(flagName.value())
                        .and("userIds").is(user.id())
        );
        return mongoTemplate.exists(query, MongoFeatureFlagAssignmentDocument.class, collectionName);
    }

    @Override
    public void saveAssignments(final FeatureFlagName flagName, final FeatureFlagEntitledUsers users) {
        var query = Query.query(Criteria.where("_id").is(flagName.value()));
        mongoTemplate.remove(query, MongoFeatureFlagAssignmentDocument.class, collectionName);
        if (!users.isEmpty()) {
            Set<String> userIds = users.stream()
                    .map(FeatureFlagUser::id)
                    .collect(Collectors.toUnmodifiableSet());
            mongoTemplate.save(new MongoFeatureFlagAssignmentDocument(flagName.value(), userIds), collectionName);
        }
    }

    @Override
    public void addUser(final FeatureFlagName flagName, final FeatureFlagUser user) {
        var query = Query.query(Criteria.where("_id").is(flagName.value()));
        var update = new Update().addToSet("userIds", user.id());
        var result = mongoTemplate.updateFirst(query, update, MongoFeatureFlagAssignmentDocument.class, collectionName);
        if (result.getMatchedCount() == 0) {
            mongoTemplate.save(
                    new MongoFeatureFlagAssignmentDocument(flagName.value(), Set.of(user.id())),
                    collectionName
            );
        }
    }

    @Override
    public void removeUser(final FeatureFlagName flagName, final FeatureFlagUser user) {
        var query = Query.query(Criteria.where("_id").is(flagName.value()));
        var update = new Update().pull("userIds", user.id());
        mongoTemplate.updateFirst(query, update, MongoFeatureFlagAssignmentDocument.class, collectionName);
    }

    @Override
    public void removeAllByFlagName(final FeatureFlagName flagName) {
        var query = Query.query(Criteria.where("_id").is(flagName.value()));
        mongoTemplate.remove(query, MongoFeatureFlagAssignmentDocument.class, collectionName);
    }
}
