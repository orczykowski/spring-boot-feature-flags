package io.github.orczykowski.springbootfeatureflags.infrastructure;

import tools.jackson.databind.json.JsonMapper;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagAssignmentRepository;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagRepository;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagsPropertySource;
import io.github.orczykowski.springbootfeatureflags.adapters.jpa.JpaFeatureFlagAssignmentRepositoryAdapter;
import io.github.orczykowski.springbootfeatureflags.adapters.jpa.JpaFeatureFlagRepositoryAdapter;
import io.github.orczykowski.springbootfeatureflags.adapters.jpa.SpringDataJpaFeatureFlagAssignmentRepository;
import io.github.orczykowski.springbootfeatureflags.adapters.jpa.SpringDataJpaFeatureFlagRepository;
import io.github.orczykowski.springbootfeatureflags.adapters.mongo.MongoFeatureFlagAssignmentRepositoryAdapter;
import io.github.orczykowski.springbootfeatureflags.adapters.mongo.MongoFeatureFlagRepositoryAdapter;
import io.github.orczykowski.springbootfeatureflags.adapters.property.InMemoryFeatureFlagAssignmentRepository;
import io.github.orczykowski.springbootfeatureflags.adapters.property.PropertyFeatureFlagRepositoryAdapter;
import io.github.orczykowski.springbootfeatureflags.adapters.redis.RedisFeatureFlagAssignmentRepositoryAdapter;
import io.github.orczykowski.springbootfeatureflags.adapters.redis.RedisFeatureFlagRepositoryAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ConditionalOnProperty(value = "feature-flags.enabled", havingValue = "true")
@EnableConfigurationProperties({FeatureFlagsPropertySource.class, FeatureFlagStorageProperties.class})
public class FeatureFlagStorageConfiguration {

    @Configuration
    @ConditionalOnProperty(value = "feature-flags.enabled", havingValue = "true")
    @ConditionalOnProperty(name = "feature-flags.storage.type", havingValue = "property", matchIfMissing = true)
    static class PropertyStorageConfiguration {

        @Bean
        InMemoryFeatureFlagAssignmentRepository featureFlagAssignmentRepository() {
            return new InMemoryFeatureFlagAssignmentRepository();
        }

        @Bean
        FeatureFlagRepository featureFlagRepository(FeatureFlagsPropertySource propertySource,
                                                     InMemoryFeatureFlagAssignmentRepository assignmentRepository) {
            return new PropertyFeatureFlagRepositoryAdapter(propertySource, assignmentRepository);
        }
    }

    @Configuration
    @ConditionalOnProperty(value = "feature-flags.enabled", havingValue = "true")
    @ConditionalOnProperty(name = "feature-flags.storage.type", havingValue = "jpa")
    @ConditionalOnClass(name = "jakarta.persistence.EntityManager")
    @EnableJpaRepositories(basePackageClasses = SpringDataJpaFeatureFlagRepository.class)
    static class JpaStorageConfiguration {

        @Bean
        FeatureFlagRepository featureFlagRepository(SpringDataJpaFeatureFlagRepository jpaRepository) {
            return new JpaFeatureFlagRepositoryAdapter(jpaRepository);
        }

        @Bean
        FeatureFlagAssignmentRepository featureFlagAssignmentRepository(
                SpringDataJpaFeatureFlagAssignmentRepository jpaAssignmentRepository) {
            return new JpaFeatureFlagAssignmentRepositoryAdapter(jpaAssignmentRepository);
        }
    }

    @Configuration
    @ConditionalOnProperty(value = "feature-flags.enabled", havingValue = "true")
    @ConditionalOnProperty(name = "feature-flags.storage.type", havingValue = "redis")
    @ConditionalOnClass(name = "org.springframework.data.redis.core.RedisTemplate")
    static class RedisStorageConfiguration {

        @Bean
        FeatureFlagRepository featureFlagRepository(
                org.springframework.data.redis.core.RedisTemplate<String, String> redisTemplate,
                JsonMapper objectMapper,
                FeatureFlagStorageProperties properties) {
            return new RedisFeatureFlagRepositoryAdapter(redisTemplate, objectMapper, properties.collectionName());
        }

        @Bean
        FeatureFlagAssignmentRepository featureFlagAssignmentRepository(
                org.springframework.data.redis.core.RedisTemplate<String, String> redisTemplate,
                JsonMapper objectMapper,
                FeatureFlagStorageProperties properties) {
            return new RedisFeatureFlagAssignmentRepositoryAdapter(redisTemplate, objectMapper, properties.assignmentCollectionName());
        }
    }

    @Configuration
    @ConditionalOnProperty(value = "feature-flags.enabled", havingValue = "true")
    @ConditionalOnProperty(name = "feature-flags.storage.type", havingValue = "mongodb")
    @ConditionalOnClass(name = "org.springframework.data.mongodb.core.MongoTemplate")
    static class MongoStorageConfiguration {

        @Bean
        FeatureFlagRepository featureFlagRepository(
                org.springframework.data.mongodb.core.MongoTemplate mongoTemplate,
                FeatureFlagStorageProperties properties) {
            return new MongoFeatureFlagRepositoryAdapter(mongoTemplate, properties.collectionName());
        }

        @Bean
        FeatureFlagAssignmentRepository featureFlagAssignmentRepository(
                org.springframework.data.mongodb.core.MongoTemplate mongoTemplate,
                FeatureFlagStorageProperties properties) {
            return new MongoFeatureFlagAssignmentRepositoryAdapter(mongoTemplate, properties.assignmentCollectionName());
        }
    }
}
