package io.github.orczykowski.springbootfeatureflags

import static FeatureFlagState.ANYBODY
import static FeatureFlagState.NOBODY
import static FeatureFlagState.RESTRICTED
import static io.github.orczykowski.springbootfeatureflags.FeatureFlagsPropertySource.FeatureFlagDefinitionDto

class FeatureFlagDefinitionTestFactory {

    static def enableForAll(FeatureFlagName name) {
        new FeatureFlagDefinition(name, ANYBODY, [] as Set)
    }

    static def enableForUser(FeatureFlagName name, User userId) {
        new FeatureFlagDefinition(name, RESTRICTED, [userId] as Set)
    }

    static def disableForAll(FeatureFlagName name) {
        new FeatureFlagDefinition(name, NOBODY, [] as Set)
    }

    static def disableForUser(FeatureFlagName name, User userId) {
        new FeatureFlagDefinition(name, NOBODY, [userId] as Set)
    }

    static def enableForAll(String name) {
        enableForAll(new FeatureFlagName(name))
    }

    static def disableForAll(String name) {
        new FeatureFlagDefinition(new FeatureFlagName(name), NOBODY, [] as Set)
    }

    static def enableForUser(String name, String userId) {
        new FeatureFlagDefinition(new FeatureFlagName(name), RESTRICTED, [new User(userId)] as Set)
    }

    static def disableForUser(String name, String userId) {
        new FeatureFlagDefinition(new FeatureFlagName(name), NOBODY, [new User(userId)] as Set)
    }

    static def enableForUserDto(String name, String user) {
        new FeatureFlagDefinitionDto(name, RESTRICTED, [user] as Set)
    }

    static def enableForAllDto(String name) {
        new FeatureFlagDefinitionDto(name, ANYBODY, [] as Set)
    }

    static def disableForAllDto(String name) {
        new FeatureFlagDefinitionDto(name, NOBODY, [] as Set)
    }

    static def disableForUserDto(String name, String user) {
        new FeatureFlagDefinitionDto(name, NOBODY, [user] as Set)
    }


}
