package io.github.orczykowski.springbootfeatureflags

import static io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinition.FeatureFlagState.ANYBODY
import static io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinition.FeatureFlagState.NOBODY
import static io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinition.FeatureFlagState.RESTRICTED

class FeatureFlagDefinitionTestFactory {

    static def enableForAll(FeatureFlagDefinition.FeatureFlagName name) {
        new FeatureFlagDefinition(name, ANYBODY, [] as Set)
    }

    static def enableForUser(FeatureFlagDefinition.FeatureFlagName name, FeatureFlagDefinition.User userId) {
        new FeatureFlagDefinition(name, RESTRICTED, [userId] as Set)
    }

    static def disableForAll(FeatureFlagDefinition.FeatureFlagName name) {
        new FeatureFlagDefinition(name, NOBODY, [] as Set)
    }

    static def disableForUser(FeatureFlagDefinition.FeatureFlagName name, FeatureFlagDefinition.User userId) {
        new FeatureFlagDefinition(name, NOBODY, [userId] as Set)
    }

    static def enableForAll(String name) {
        enableForAll(new FeatureFlagDefinition.FeatureFlagName(name))
    }

    static def disableForAll(String name) {
        new FeatureFlagDefinition(new FeatureFlagDefinition.FeatureFlagName(name), NOBODY, [] as Set)
    }

    static def enableForUser(String name, String userId) {
        new FeatureFlagDefinition(new FeatureFlagDefinition.FeatureFlagName(name), RESTRICTED, [new FeatureFlagDefinition.User(userId)] as Set)
    }

    static def disableForUser(String name, String userId) {
        new FeatureFlagDefinition(new FeatureFlagDefinition.FeatureFlagName(name), NOBODY, [new FeatureFlagDefinition.User(userId)] as Set)
    }
}
