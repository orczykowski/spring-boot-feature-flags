package io.github.orczykowski.springbootfeatureflags.adapters.redis;

import tools.jackson.databind.json.JsonMapper;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinition;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagName;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagRepository;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagState;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class RedisFeatureFlagRepositoryAdapter implements FeatureFlagRepository {

    private final String hashKey;
    private final RedisTemplate<String, String> redisTemplate;
    private final JsonMapper objectMapper;

    public RedisFeatureFlagRepositoryAdapter(final RedisTemplate<String, String> redisTemplate,
                                              final JsonMapper objectMapper,
                                              final String collectionName) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.hashKey = collectionName;
    }

    @Override
    public Stream<FeatureFlagDefinition> findAllEnabledFeatureFlags() {
        return findAll().filter(def -> !FeatureFlagState.NOBODY.equals(def.enabled()));
    }

    @Override
    public Optional<FeatureFlagDefinition> findByName(final FeatureFlagName featureFlagName) {
        var json = (String) redisTemplate.opsForHash().get(hashKey, featureFlagName.value());
        return Optional.ofNullable(json).map(this::deserialize).map(RedisFeatureFlagData::toDomain);
    }

    @Override
    public void removeByName(final FeatureFlagName flagName) {
        redisTemplate.opsForHash().delete(hashKey, flagName.value());
    }

    @Override
    public FeatureFlagDefinition save(final FeatureFlagDefinition definition) {
        var data = RedisFeatureFlagData.fromDomain(definition);
        redisTemplate.opsForHash().put(hashKey, definition.name().value(), serialize(data));
        return definition;
    }

    @Override
    public Stream<FeatureFlagDefinition> findAll() {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(hashKey);
        return entries.values().stream()
                .map(value -> deserialize((String) value))
                .map(RedisFeatureFlagData::toDomain);
    }

    private String serialize(final RedisFeatureFlagData data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize feature flag to Redis", e);
        }
    }

    private RedisFeatureFlagData deserialize(final String json) {
        try {
            return objectMapper.readValue(json, RedisFeatureFlagData.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to deserialize feature flag from Redis", e);
        }
    }
}
