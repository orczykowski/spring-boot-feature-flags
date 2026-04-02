package io.github.orczykowski.springbootfeatureflags

import io.github.orczykowski.springbootfeatureflags.infrastructure.FeatureFlagStorageProperties
import spock.lang.Specification

class FeatureFlagStoragePropertiesSpec extends Specification {

    def "should default to PROPERTY storage type when null"() {
        when:
          def properties = new FeatureFlagStorageProperties(null, null, null, null, null)

        then:
          properties.type() == FeatureFlagStorageProperties.StorageType.PROPERTY
    }

    def "should default table name to feature_flags when null"() {
        when:
          def properties = new FeatureFlagStorageProperties(null, null, null, null, null)

        then:
          properties.tableName() == "feature_flags"
    }

    def "should default collection name to feature_flags when null"() {
        when:
          def properties = new FeatureFlagStorageProperties(null, null, null, null, null)

        then:
          properties.collectionName() == "feature_flags"
    }

    def "should default assignment table name to feature_flag_assignments when null"() {
        when:
          def properties = new FeatureFlagStorageProperties(null, null, null, null, null)

        then:
          properties.assignmentTableName() == "feature_flag_assignments"
    }

    def "should default assignment collection name to feature_flag_assignments when null"() {
        when:
          def properties = new FeatureFlagStorageProperties(null, null, null, null, null)

        then:
          properties.assignmentCollectionName() == "feature_flag_assignments"
    }

    def "should use provided values"() {
        when:
          def properties = new FeatureFlagStorageProperties(
                  FeatureFlagStorageProperties.StorageType.JPA,
                  "custom_table",
                  "custom_collection",
                  "custom_assignments",
                  "custom_assignments_col"
          )

        then:
          properties.type() == FeatureFlagStorageProperties.StorageType.JPA
          properties.tableName() == "custom_table"
          properties.collectionName() == "custom_collection"
          properties.assignmentTableName() == "custom_assignments"
          properties.assignmentCollectionName() == "custom_assignments_col"
    }

    def "should default blank table name to feature_flags"() {
        when:
          def properties = new FeatureFlagStorageProperties(null, "  ", null, null, null)

        then:
          properties.tableName() == "feature_flags"
    }
}
