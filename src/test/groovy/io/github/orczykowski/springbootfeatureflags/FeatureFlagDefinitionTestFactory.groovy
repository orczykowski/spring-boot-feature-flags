package io.github.orczykowski.springbootfeatureflags


import static io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinition.FeatureFlagState.OFF
import static io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinition.FeatureFlagState.ON
import static io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinition.FeatureFlagState.RESTRICTED_FOR_USERS

class FeatureFlagDefinitionTestFactory {

    static def enableForAll(FeatureFlagName name) {
        new FeatureFlagDefinition(name, ON, [] as Set)
    }

    static def enableForUser(FeatureFlagName name, User userId) {
        new FeatureFlagDefinition(name, RESTRICTED_FOR_USERS, [userId] as Set)
    }

    static def disableForAll(FeatureFlagName name) {
        new FeatureFlagDefinition(name, OFF, [] as Set)
    }

    static def disableForUser(FeatureFlagName name, User userId) {
        new FeatureFlagDefinition(name, OFF, [userId] as Set)
    }

    static def enableForAll(String name) {
        enableForAll(new FeatureFlagName(name))
    }

    static def disableForAll(String name) {
        new FeatureFlagDefinition(new FeatureFlagName(name), OFF, [] as Set)
    }

    static def enableForUser(String name, String userId) {
        new FeatureFlagDefinition(new FeatureFlagName(name), RESTRICTED_FOR_USERS, [new User(userId)] as Set)
    }

    static def disableForUser(String name, String userId) {
        new FeatureFlagDefinition(new FeatureFlagName(name), OFF, [new User(userId)] as Set)
    }
}
