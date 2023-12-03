package io.github.orczykowski.springbootfeatureflags

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import org.slf4j.LoggerFactory
import spock.lang.Specification

import static FeatureFlagDefinitionTestFactory.disableForAll
import static FeatureFlagDefinitionTestFactory.disableForUser
import static FeatureFlagDefinitionTestFactory.enableForAll
import static FeatureFlagDefinitionTestFactory.enableForUser

class FeatureFlagVerifierSpec extends Specification {

    private static final FeatureFlagName FLAG_NAME = new FeatureFlagName("FLAG_1")
    private static final User USER = new User("USER_1")

    private InMemoryFakeLogger fakeLogger = new InMemoryFakeLogger()
    private FeatureFlagRepository flagRepository = Stub(FeatureFlagRepository)
    private UserContextProvider userContextProvider = Stub(UserContextProvider)
    private MetricsPublisher metricsPublisher = Mock(MetricsPublisher)

    def setup() {
        Logger logger = LoggerFactory.getLogger(FeatureFlagVerifier.class)
        fakeLogger.setContext((LoggerContext) LoggerFactory.getILoggerFactory())
        logger.setLevel(Level.DEBUG)
        logger.addAppender(fakeLogger)
        fakeLogger.start()
        flagRepository = Stub(FeatureFlagRepository)
        userContextProvider = Stub(UserContextProvider)
        metricsPublisher = Mock(MetricsPublisher)
    }

    def "should return true when flag is defined and enabled for all users"() {
        given:
          flagRepository.findDefinition(FLAG_NAME) >> Optional.of(enableForAll(FLAG_NAME))

        and:
          def verifier = new FeatureFlagVerifier(flagRepository, userContextProvider, metricsPublisher)

        expect:
          verifier.verify(FLAG_NAME)
    }

    def "should return false for null flag name"() {
        given:
          flagRepository.findDefinition(FLAG_NAME) >> Optional.of(enableForAll(FLAG_NAME))

        and:
          def verifier = new FeatureFlagVerifier(flagRepository, userContextProvider, metricsPublisher)

        expect:
          !verifier.verify(null)
    }

    def "should return true when flag is defined and enabled for specific user"() {
        given:
          userContextProvider.provide() >> Optional.of(USER)
          flagRepository.findDefinition(FLAG_NAME) >> Optional.of(enableForUser(FLAG_NAME, USER))

        and:
          def verifier = new FeatureFlagVerifier(flagRepository, userContextProvider, metricsPublisher)

        expect:
          verifier.verify(FLAG_NAME)
    }

    def "should return false when flag is defined and not enabled for specific user"() {
        given:
          flagRepository.findDefinition(FLAG_NAME) >> Optional.of(disableForAll(FLAG_NAME))

        and:
          def verifier = new FeatureFlagVerifier(flagRepository, userContextProvider, metricsPublisher)

        expect:
          !verifier.verify(FLAG_NAME)
    }

    def "should return false when flag is turn off for all users"() {
        given:
          userContextProvider.provide() >> Optional.of(USER)
          flagRepository.findDefinition(FLAG_NAME) >> Optional.of(disableForUser(FLAG_NAME, USER))

        and:
          def verifier = new FeatureFlagVerifier(flagRepository, userContextProvider, metricsPublisher)

        expect:
          !verifier.verify(FLAG_NAME)
    }

    def "should return false when flag is defined for user but user context is empty"() {
        given:
          userContextProvider.provide() >> Optional.empty()
          flagRepository.findDefinition(FLAG_NAME) >> Optional.of(enableForUser(FLAG_NAME, USER))

        and:
          def verifier = new FeatureFlagVerifier(flagRepository, userContextProvider, metricsPublisher)

        expect:
          !verifier.verify(FLAG_NAME)
    }

    def "should return true when flag is enabled even user context provider is null"() {
        given:
          flagRepository.findDefinition(FLAG_NAME) >> Optional.of(enableForAll(FLAG_NAME))

        and:
          def verifier = new FeatureFlagVerifier(flagRepository, null, metricsPublisher)

        expect:
          verifier.verify(FLAG_NAME)
    }

    def "should verify flags when metrics publisher does not exist"() {
        given:
          flagRepository.findDefinition(FLAG_NAME) >> Optional.of(enableForAll(FLAG_NAME))

        and:
          def verifier = new FeatureFlagVerifier(flagRepository, userContextProvider, null)

        expect:
          verifier.verify(FLAG_NAME)
    }

    def "should return false when flag does not exists"() {
        given:
          flagRepository.findDefinition(FLAG_NAME) >> Optional.of(enableForAll(FLAG_NAME))

        and:
          def verifier = new FeatureFlagVerifier(flagRepository, null, metricsPublisher)

        expect:
          !verifier.verify(new FeatureFlagName("GUSTAW"))
    }

    def "should publish information about verification result for user"() {
        given:
          userContextProvider.provide() >> Optional.of(USER)
          flagRepository.findDefinition(FLAG_NAME) >> Optional.of(enableForUser(FLAG_NAME, USER))
          def verifier = new FeatureFlagVerifier(flagRepository, userContextProvider, metricsPublisher)
        when:
          verifier.verify(FLAG_NAME)
        then:
          1 * metricsPublisher.reportVerification(FLAG_NAME, USER, true)
    }

    def "should publish information about verification result"() {
        given:
          flagRepository.findDefinition(FLAG_NAME) >> Optional.of(enableForUser(FLAG_NAME, USER))
          def verifier = new FeatureFlagVerifier(flagRepository, null, metricsPublisher)
        when:
          verifier.verify(FLAG_NAME)
        then:
          1 * metricsPublisher.reportVerification(FLAG_NAME, false)
    }

    def "information about a non-existent flag should be reported"() {
        given:
          flagRepository.findDefinition(FLAG_NAME) >> Optional.empty()
          def verifier = new FeatureFlagVerifier(flagRepository, null, metricsPublisher)
        when:
          verifier.verify(FLAG_NAME)
        then:
          1 * metricsPublisher.reportFlagNotFound()
          fakeLogger.hasLog("Feature Flag definition with name FLAG_1 does not exist.", Level.WARN)
    }
}
