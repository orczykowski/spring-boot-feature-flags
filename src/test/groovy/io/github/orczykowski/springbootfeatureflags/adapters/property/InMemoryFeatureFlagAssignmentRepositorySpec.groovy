package io.github.orczykowski.springbootfeatureflags.adapters.property

import io.github.orczykowski.springbootfeatureflags.FeatureFlagEntitledUsers
import io.github.orczykowski.springbootfeatureflags.FeatureFlagName
import io.github.orczykowski.springbootfeatureflags.FeatureFlagUser
import spock.lang.Specification
import spock.lang.Subject

class InMemoryFeatureFlagAssignmentRepositorySpec extends Specification {

    static final FLAG = new FeatureFlagName("TEST_FLAG")
    static final OTHER_FLAG = new FeatureFlagName("OTHER_FLAG")
    static final USER_A = new FeatureFlagUser("user-a")
    static final USER_B = new FeatureFlagUser("user-b")

    @Subject
    def repository = new InMemoryFeatureFlagAssignmentRepository()

    def "should return empty users when no assignments exist"() {
        expect:
            repository.findUsersByFlagName(FLAG).isEmpty()
    }

    def "should save and retrieve user assignments for a flag"() {
        given:
            repository.saveAssignments(FLAG, FeatureFlagEntitledUsers.of([USER_A, USER_B] as Set))

        when:
            def result = repository.findUsersByFlagName(FLAG)

        then:
            result.size() == 2
            result.contains(USER_A)
            result.contains(USER_B)
    }

    def "should add a single user to a flag"() {
        when:
            repository.addUser(FLAG, USER_A)

        then:
            repository.findUsersByFlagName(FLAG).contains(USER_A)
    }

    def "should add multiple users incrementally"() {
        when:
            repository.addUser(FLAG, USER_A)
            repository.addUser(FLAG, USER_B)

        then:
            repository.findUsersByFlagName(FLAG).size() == 2
    }

    def "should check if user is assigned to a flag"() {
        given:
            repository.addUser(FLAG, USER_A)

        expect:
            repository.isUserAssigned(FLAG, USER_A)
            !repository.isUserAssigned(FLAG, USER_B)
    }

    def "should remove a single user from a flag"() {
        given:
            repository.addUser(FLAG, USER_A)
            repository.addUser(FLAG, USER_B)

        when:
            repository.removeUser(FLAG, USER_A)

        then:
            !repository.isUserAssigned(FLAG, USER_A)
            repository.isUserAssigned(FLAG, USER_B)
    }

    def "should clean up entry when last user is removed"() {
        given:
            repository.addUser(FLAG, USER_A)

        when:
            repository.removeUser(FLAG, USER_A)

        then:
            repository.findUsersByFlagName(FLAG).isEmpty()
    }

    def "should remove all assignments for a flag"() {
        given:
            repository.addUser(FLAG, USER_A)
            repository.addUser(FLAG, USER_B)

        when:
            repository.removeAllByFlagName(FLAG)

        then:
            repository.findUsersByFlagName(FLAG).isEmpty()
    }

    def "should find all flag names assigned to a user"() {
        given:
            repository.addUser(FLAG, USER_A)
            repository.addUser(OTHER_FLAG, USER_A)
            repository.addUser(OTHER_FLAG, USER_B)

        when:
            def flags = repository.findFlagNamesByUser(USER_A)

        then:
            flags.size() == 2
            flags.contains(FLAG)
            flags.contains(OTHER_FLAG)
    }

    def "should clear assignments when saving empty users"() {
        given:
            repository.addUser(FLAG, USER_A)

        when:
            repository.saveAssignments(FLAG, FeatureFlagEntitledUsers.empty())

        then:
            repository.findUsersByFlagName(FLAG).isEmpty()
    }
}
