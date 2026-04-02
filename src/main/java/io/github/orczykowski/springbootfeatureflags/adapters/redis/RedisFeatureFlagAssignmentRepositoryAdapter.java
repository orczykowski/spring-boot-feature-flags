package io.github.orczykowski.springbootfeatureflags.adapters.redis;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagEntitledUsers;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagAssignmentRepository;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagName;
import io.github.orczykowski.springbootfeatureflags.FeatureFlags;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagUser;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RedisFeatureFlagAssignmentRepositoryAdapter implements FeatureFlagAssignmentRepository {

    private static final TypeReference<Set<String>> SET_TYPE = new TypeReference<>() {};

    private final String hashKey;
    private final RedisTemplate<String, String> redisTemplate;
    private final JsonMapper objectMapper;

    public RedisFeatureFlagAssignmentRepositoryAdapter(final RedisTemplate<String, String> redisTemplate,
                                                        final JsonMapper objectMapper,
                                                        final String collectionName) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.hashKey = collectionName;
    }

    @Override
    public FeatureFlagEntitledUsers findUsersByFlagName(final FeatureFlagName flagName) {
        var json = (String) redisTemplate.opsForHash().get(hashKey, flagName.value());
        if (json == null) {
            return FeatureFlagEntitledUsers.empty();
        }
        Set<FeatureFlagUser> users = deserializeUserIds(json).stream()
                .map(FeatureFlagUser::new)
                .collect(Collectors.toUnmodifiableSet());
        return FeatureFlagEntitledUsers.of(users);
    }

    @Override
    public FeatureFlags findFlagNamesByUser(final FeatureFlagUser user) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(hashKey);
        Set<FeatureFlagName> flags = entries.entrySet().stream()
                .filter(entry -> deserializeUserIds((String) entry.getValue()).contains(user.id()))
                .map(entry -> new FeatureFlagName((String) entry.getKey()))
                .collect(Collectors.toUnmodifiableSet());
        return FeatureFlags.of(flags);
    }

    @Override
    public boolean isUserAssigned(final FeatureFlagName flagName, final FeatureFlagUser user) {
        var json = (String) redisTemplate.opsForHash().get(hashKey, flagName.value());
        if (json == null) {
            return false;
        }
        return deserializeUserIds(json).contains(user.id());
    }

    @Override
    public void saveAssignments(final FeatureFlagName flagName, final FeatureFlagEntitledUsers users) {
        if (users.isEmpty()) {
            redisTemplate.opsForHash().delete(hashKey, flagName.value());
        } else {
            Set<String> userIds = users.stream()
                    .map(FeatureFlagUser::id)
                    .collect(Collectors.toUnmodifiableSet());
            redisTemplate.opsForHash().put(hashKey, flagName.value(), serializeUserIds(userIds));
        }
    }

    @Override
    public void addUser(final FeatureFlagName flagName, final FeatureFlagUser user) {
        var json = (String) redisTemplate.opsForHash().get(hashKey, flagName.value());
        Set<String> userIds = json == null ? new HashSet<>() : new HashSet<>(deserializeUserIds(json));
        userIds.add(user.id());
        redisTemplate.opsForHash().put(hashKey, flagName.value(), serializeUserIds(userIds));
    }

    @Override
    public void removeUser(final FeatureFlagName flagName, final FeatureFlagUser user) {
        var json = (String) redisTemplate.opsForHash().get(hashKey, flagName.value());
        if (json == null) {
            return;
        }
        Set<String> userIds = new HashSet<>(deserializeUserIds(json));
        userIds.remove(user.id());
        if (userIds.isEmpty()) {
            redisTemplate.opsForHash().delete(hashKey, flagName.value());
        } else {
            redisTemplate.opsForHash().put(hashKey, flagName.value(), serializeUserIds(userIds));
        }
    }

    @Override
    public void removeAllByFlagName(final FeatureFlagName flagName) {
        redisTemplate.opsForHash().delete(hashKey, flagName.value());
    }

    private String serializeUserIds(final Set<String> userIds) {
        try {
            return objectMapper.writeValueAsString(userIds);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize user assignments to Redis", e);
        }
    }

    private Set<String> deserializeUserIds(final String json) {
        try {
            return objectMapper.readValue(json, SET_TYPE);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to deserialize user assignments from Redis", e);
        }
    }
}
