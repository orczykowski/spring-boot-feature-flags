package io.github.orczykowski.springbootfeatureflags.adapters.redis

import tools.jackson.databind.json.JsonMapper
import io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinition
import io.github.orczykowski.springbootfeatureflags.FeatureFlagName
import io.github.orczykowski.springbootfeatureflags.FeatureFlagState
import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.RedisTemplate
import spock.lang.Specification
import spock.lang.Subject

class RedisFeatureFlagRepositoryAdapterSpec extends Specification {

    def redisTemplate = Mock(RedisTemplate)
    def hashOps = Mock(HashOperations)
    def objectMapper = new JsonMapper()

    @Subject
    def adapter = new RedisFeatureFlagRepositoryAdapter(redisTemplate, objectMapper, "feature_flags")

    def setup() {
        redisTemplate.opsForHash() >> hashOps
    }

    def "should save and serialize flag to redis hash"() {
        given:
          def definition = new FeatureFlagDefinition(
                  new FeatureFlagName("FLAG"), FeatureFlagState.RESTRICTED)

        when:
          adapter.save(definition)

        then:
          1 * hashOps.put("feature_flags", "FLAG", { String json ->
              def data = objectMapper.readValue(json, RedisFeatureFlagData)
              data.name() == "FLAG" && data.enabled() == "RESTRICTED"
          })
    }

    def "should find by name when exists"() {
        given:
          def data = new RedisFeatureFlagData("FLAG", "ANYBODY")
          def json = objectMapper.writeValueAsString(data)
          hashOps.get("feature_flags", "FLAG") >> json

        when:
          def result = adapter.findByName(new FeatureFlagName("FLAG"))

        then:
          result.isPresent()
          result.get().name().value() == "FLAG"
          result.get().enabled() == FeatureFlagState.ANYBODY
    }

    def "should return empty when flag not found"() {
        given:
          hashOps.get("feature_flags", "MISSING") >> null

        when:
          def result = adapter.findByName(new FeatureFlagName("MISSING"))

        then:
          result.isEmpty()
    }

    def "should remove by name"() {
        when:
          adapter.removeByName(new FeatureFlagName("FLAG"))

        then:
          1 * hashOps.delete("feature_flags", "FLAG")
    }

    def "should find all and filter enabled"() {
        given:
          def enabled = new RedisFeatureFlagData("ENABLED", "ANYBODY")
          def disabled = new RedisFeatureFlagData("DISABLED", "NOBODY")
          hashOps.entries("feature_flags") >> [
                  "ENABLED": objectMapper.writeValueAsString(enabled),
                  "DISABLED": objectMapper.writeValueAsString(disabled)
          ]

        when:
          def result = adapter.findAllEnabledFeatureFlags().toList()

        then:
          result.size() == 1
          result[0].name().value() == "ENABLED"
    }
}
