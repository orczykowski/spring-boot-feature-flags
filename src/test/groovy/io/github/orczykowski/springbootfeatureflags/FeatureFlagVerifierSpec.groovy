package io.github.orczykowski.springbootfeatureflags

import spock.lang.Specification

import static FeatureFlagDefinitionTestFactory.disableForAll
import static FeatureFlagDefinitionTestFactory.disableForUser
import static FeatureFlagDefinitionTestFactory.enableForAll
import static FeatureFlagDefinitionTestFactory.enableForUser

class FeatureFlagVerifierSpec extends Specification {

    private static final FeatureFlagName FLAG_NAME = new FeatureFlagName("FLAG_1")
    private static final User USER = new User("USER_1")

    private FeatureFlagRepository flagRepository = Stub(FeatureFlagRepository)
    private UserContextProvider userContextProvider = Stub(UserContextProvider)


    def "should return true when flag is defined and enabled for all users"() {
        given:
          flagRepository.findDefinition(FLAG_NAME) >> Optional.of(enableForAll(FLAG_NAME))

        and:
          def verifier = new FeatureFlagVerifier(flagRepository, userContextProvider)

        expect:
          verifier.verify(FLAG_NAME)
    }

    def "should return false for null flag name"() {
        given:
          flagRepository.findDefinition(FLAG_NAME) >> Optional.of(enableForAll(FLAG_NAME))

        and:
          def verifier = new FeatureFlagVerifier(flagRepository, userContextProvider)

        expect:
          !verifier.verify(null)
    }

    def "should return true when flag is defined and enabled for specific user"() {
        given:
          userContextProvider.provide() >> Optional.of(USER)
          flagRepository.findDefinition(FLAG_NAME) >> Optional.of(enableForUser(FLAG_NAME, USER))

        and:
          def verifier = new FeatureFlagVerifier(flagRepository, userContextProvider)

        expect:
          verifier.verify(FLAG_NAME)
    }

    def "should return false when flag is defined and not enabled for specific user"() {
        given:
          flagRepository.findDefinition(FLAG_NAME) >> Optional.of(disableForAll(FLAG_NAME))

        and:
          def verifier = new FeatureFlagVerifier(flagRepository, userContextProvider)

        expect:
          !verifier.verify(FLAG_NAME)
    }

    def "should return false when flag is turn off for all users"() {
        given:
          userContextProvider.provide() >> Optional.of(USER)
          flagRepository.findDefinition(FLAG_NAME) >> Optional.of(disableForUser(FLAG_NAME, USER))

        and:
          def verifier = new FeatureFlagVerifier(flagRepository, userContextProvider)

        expect:
          !verifier.verify(FLAG_NAME)
    }

    def "should return false when flag is defined for user but user context is empty"() {
        given:
          userContextProvider.provide() >> Optional.empty()
          flagRepository.findDefinition(FLAG_NAME) >> Optional.of(enableForUser(FLAG_NAME, USER))

        and:
          def verifier = new FeatureFlagVerifier(flagRepository, userContextProvider)

        expect:
          !verifier.verify(FLAG_NAME)
    }

    def "should return true when flag is enabled even user context provider is null"() {
        given:
          flagRepository.findDefinition(FLAG_NAME) >> Optional.of(enableForAll(FLAG_NAME))

        and:
          def verifier = new FeatureFlagVerifier(flagRepository, null)

        expect:
          verifier.verify(FLAG_NAME)
    }

    def "should return false when flag does not exists"() {
        given:
          flagRepository.findDefinition(FLAG_NAME) >> Optional.of(enableForAll(FLAG_NAME))

        and:
          def verifier = new FeatureFlagVerifier(flagRepository, null)

        expect:
          !verifier.verify(new FeatureFlagName("GUSTAW"))
    }
}
