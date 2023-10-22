package io.github.orczykowski.springbootfeatureflags

import spock.lang.Specification

import java.util.stream.Stream

import static io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinition.FeatureFlagState.OFF
import static io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinition.FeatureFlagState.ON
import static io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinition.FeatureFlagState.RESTRICTED_FOR_USERS

class FeatureFlagProviderSpec extends Specification {
    private static final User USER_1 = new User("123")
    private static final User USER_2 = new User("567")

    private static final FeatureFlagDefinition ENABLED_FEATURE_FLAG = new FeatureFlagDefinition(new FeatureFlagName("FOR_ALL_1"), ON, null)
    private static final FeatureFlagDefinition ENABLED_FEATURE_FLAG_2 = new FeatureFlagDefinition(new FeatureFlagName("FOR_ALL_2"), ON, null)
    private static final FeatureFlagDefinition ENABLED_FEATURE_FLAG_FOR_USER_1 = new FeatureFlagDefinition(new FeatureFlagName("FOR_USER_1"), RESTRICTED_FOR_USERS, Set.of(USER_1))
    private static final FeatureFlagDefinition ENABLED_FEATURE_FLAG_FOR_USER_2 = new FeatureFlagDefinition(new FeatureFlagName("FOR_USER_2"), RESTRICTED_FOR_USERS, Set.of(USER_2))
    private static final FeatureFlagDefinition DISABLED_FEATURE_FLAG = new FeatureFlagDefinition(new FeatureFlagName("DISABLED"), OFF, Set.of())

    private FeatureFlagRepository flagRepository = Mock(FeatureFlagRepository)
    private UserContextProvider userContextProvider = Mock(UserContextProvider)


    def "should return only enabled for all feature flag names when user context is not defined"() {
        given:
          flagRepository.findAllEnabledFeatureFlags() >> Stream.of(ENABLED_FEATURE_FLAG, ENABLED_FEATURE_FLAG_2,
                  DISABLED_FEATURE_FLAG, ENABLED_FEATURE_FLAG_FOR_USER_1)

          def provider = new FeatureFlagProvider(flagRepository, null)

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
          def provider = new FeatureFlagProvider(flagRepository, null)
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
          def provider = new FeatureFlagProvider(flagRepository, null)

        when:
          def flags = provider.provide()

        then:
          flags.toList().isEmpty()
    }

    def "should return flags enabled for user"() {
        given:
          flagRepository.findAllEnabledFeatureFlags() >> Stream.of(ENABLED_FEATURE_FLAG_FOR_USER_1, ENABLED_FEATURE_FLAG_FOR_USER_2)
          userContextProvider.provide() >> Optional.of(USER_1)

        and:
          def featureFlagProvider = new FeatureFlagProvider(flagRepository, userContextProvider)

        when:
          def flags = featureFlagProvider.provide()

        then:
          def result = flags.toList()
          result.size() == 1
          result[0] == ENABLED_FEATURE_FLAG_FOR_USER_1.name()
    }

}
