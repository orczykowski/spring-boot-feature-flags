package io.github.orczykowski.springbootfeatureflags;

import java.util.Objects;
import java.util.Set;

sealed interface FeatureFlagCommand permits FeatureFlagCommand.UpsertFeatureFlagCommand, FeatureFlagCommand.DeleteFeatureFlagCommand {
    record DeleteFeatureFlagCommand(FeatureFlagDefinition.FeatureFlagName flagName) implements FeatureFlagCommand {
        public DeleteFeatureFlagCommand {
            if (Objects.isNull(flagName)) {
                throw new InvalidCommandException("Cannot delete feature flag. Feature flag name cannot be null");
            }
        }
    }

    record UpsertFeatureFlagCommand(FeatureFlagDefinition.FeatureFlagName flagName,
                                    FeatureFlagDefinition.FeatureFlagState state,
                                    Set<FeatureFlagDefinition.User> users) implements FeatureFlagCommand {

        public UpsertFeatureFlagCommand {
            if (Objects.isNull(flagName)) {
                throw new InvalidCommandException("Cannot create upsert feature flag command with null flag name");
            }
        }
    }
}
