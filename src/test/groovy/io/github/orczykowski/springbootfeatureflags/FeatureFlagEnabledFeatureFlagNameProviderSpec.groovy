package io.github.orczykowski.springbootfeatureflags

import spock.lang.Specification

import java.util.stream.Stream

import static FeatureFlagState.ANYBODY
import static FeatureFlagState.NOBODY
import static FeatureFlagState.RESTRICTED

class FeatureFlagEnabledFeatureFlagNameProviderSpec extends Specification {
    private static final FeatureFlagUser USER_1 = new FeatureFlagUser("123")
    private static final FeatureFlagUser USER_2 = new FeatureFlagUser("567")

    private static final FeatureFlagDefinition ENABLED_FEATURE_FLAG = new FeatureFlagDefinition(new FeatureFlagName("FOR_ALL_1"), ANYBODY)
    private static final FeatureFlagDefinition ENABLED_FEATURE_FLAG_2 = new FeatureFlagDefinition(new FeatureFlagName("FOR_ALL_2"), ANYBODY)
    private static final FeatureFlagDefinition ENABLED_FEATURE_FLAG_FOR_USER_1 = new FeatureFlagDefinition(new FeatureFlagName("FOR_USER_1"), RESTRICTED)
    private static final FeatureFlagDefinition ENABLED_FEATURE_FLAG_FOR_USER_2 = new FeatureFlagDefinition(new FeatureFlagName("FOR_USER_2"), RESTRICTED)
    private static final FeatureFlagDefinition DISABLED_FEATURE_FLAG = new FeatureFlagDefinition(new FeatureFlagName("DISABLED"), NOBODY)

    private FeatureFlagSupplier flagRepository = Mock(FeatureFlagSupplier)
    private FeatureFlagUserContextProvider userContextProvider = Mock(FeatureFlagUserContextProvider)
    private FeatureFlagAssignmentSupplier assignmentSupplier = Mock(FeatureFlagAssignmentSupplier)


    def "should return only enabled for all feature flag names when user context is not defined"() {
        given:
          flagRepository.findAllEnabledFeatureFlags() >> Stream.of(ENABLED_FEATURE_FLAG, ENABLED_FEATURE_FLAG_2,
                  DISABLED_FEATURE_FLAG, ENABLED_FEATURE_FLAG_FOR_USER_1)

          def provider = new FeatureFlagEnabledFeatureFlagNameProvider(flagRepository, null, assignmentSupplier)

        when:
          def flags = provider.provide()

        then:
          def result = flags
                  .sorted((f1, f2) -> f1.value() <=> f2.value())
                  .toList()
          result.size() == 2
          result[0] == ENABLED_FEATURE_FLAG.name()
          result[1] == ENABLED_FEATURE_FLAG_2.name()
    }

    def "should return empty result when user context is not defined and there is no enabled flags for user"() {
        given:
          def provider = new FeatureFlagEnabledFeatureFlagNameProvider(flagRepository, null, assignmentSupplier)
          flagRepository.findAllEnabledFeatureFlags() >> Stream.of(DISABLED_FEATURE_FLAG, ENABLED_FEATURE_FLAG_FOR_USER_1)

        when:
          def flags = provider.provide()

        then:
          def result = flags
                  .sorted((f1, f2) -> f1.value() <=> f2.value())
                  .toList()

        and:
          result.toList().isEmpty()
    }

    def "should return empty result when any flag is defined"() {
        given:
          flagRepository.findAllEnabledFeatureFlags() >> Stream.of()
          def provider = new FeatureFlagEnabledFeatureFlagNameProvider(flagRepository, null, assignmentSupplier)

        when:
          def flags = provider.provide()

        then:
          flags.toList().isEmpty()
    }

    def "should return flags enabled for user"() {
        given:
          flagRepository.findAllEnabledFeatureFlags() >> Stream.of(ENABLED_FEATURE_FLAG_FOR_USER_1, ENABLED_FEATURE_FLAG_FOR_USER_2)
          userContextProvider.provide() >> Optional.of(USER_1)
          assignmentSupplier.findFlagNamesByUser(USER_1) >> FeatureFlags.of([ENABLED_FEATURE_FLAG_FOR_USER_1.name()] as Set)

        and:
          def featureFlagProvider = new FeatureFlagEnabledFeatureFlagNameProvider(flagRepository, userContextProvider, assignmentSupplier)

        when:
          def flags = featureFlagProvider.provide()

        then:
          def result = flags.toList()
          result.size() == 1
          result[0] == ENABLED_FEATURE_FLAG_FOR_USER_1.name()
    }

}
