package io.github.orczykowski.springbootfeatureflags.adapters.mongo

import io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinition
import io.github.orczykowski.springbootfeatureflags.FeatureFlagName
import io.github.orczykowski.springbootfeatureflags.FeatureFlagState
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import spock.lang.Specification
import spock.lang.Subject

class MongoFeatureFlagRepositoryAdapterSpec extends Specification {

    def mongoTemplate = Mock(MongoTemplate)

    @Subject
    def adapter = new MongoFeatureFlagRepositoryAdapter(mongoTemplate, "feature_flags")

    def "should find all enabled feature flags"() {
        given:
          def doc = MongoFeatureFlagDocument.fromDomain(
                  new FeatureFlagDefinition(new FeatureFlagName("FLAG"), FeatureFlagState.ANYBODY))
          mongoTemplate.find(_ as Query, MongoFeatureFlagDocument, "feature_flags") >> [doc]

        when:
          def result = adapter.findAllEnabledFeatureFlags().toList()

        then:
          result.size() == 1
          result[0].name().value() == "FLAG"
    }

    def "should find by name when exists"() {
        given:
          def doc = MongoFeatureFlagDocument.fromDomain(
                  new FeatureFlagDefinition(new FeatureFlagName("FLAG"), FeatureFlagState.ANYBODY))
          mongoTemplate.findById("FLAG", MongoFeatureFlagDocument, "feature_flags") >> doc

        when:
          def result = adapter.findByName(new FeatureFlagName("FLAG"))

        then:
          result.isPresent()
          result.get().name().value() == "FLAG"
    }

    def "should return empty when not found"() {
        given:
          mongoTemplate.findById("MISSING", MongoFeatureFlagDocument, "feature_flags") >> null

        when:
          def result = adapter.findByName(new FeatureFlagName("MISSING"))

        then:
          result.isEmpty()
    }

    def "should save document"() {
        given:
          def definition = new FeatureFlagDefinition(
                  new FeatureFlagName("FLAG"), FeatureFlagState.RESTRICTED)

        when:
          adapter.save(definition)

        then:
          1 * mongoTemplate.save(_ as MongoFeatureFlagDocument, "feature_flags")
    }

    def "should remove by name"() {
        when:
          adapter.removeByName(new FeatureFlagName("FLAG"))

        then:
          1 * mongoTemplate.remove(_ as Query, MongoFeatureFlagDocument, "feature_flags")
    }

    def "should use custom collection name"() {
        given:
          def customAdapter = new MongoFeatureFlagRepositoryAdapter(mongoTemplate, "custom_flags")
          mongoTemplate.findAll(MongoFeatureFlagDocument, "custom_flags") >> []

        when:
          def result = customAdapter.findAll().toList()

        then:
          result.isEmpty()
    }
}
