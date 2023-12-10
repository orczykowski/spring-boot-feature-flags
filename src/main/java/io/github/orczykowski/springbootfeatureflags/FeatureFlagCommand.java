package io.github.orczykowski.springbootfeatureflags;

import java.util.Objects;
import java.util.Set;

/**
 * Base  class for commands
 */
public sealed interface FeatureFlagCommand permits FeatureFlagCommand.UpsertFeatureFlagCommand, FeatureFlagCommand.DeleteFeatureFlagCommand {
    /**
     * Command used to remove feature flag
     *
     * @param flagName - name of the flag which is to be deleted
     */
    record DeleteFeatureFlagCommand(FeatureFlagDefinition.FeatureFlagName flagName) implements FeatureFlagCommand {
        /**
         * Constructor
         *
         * @param flagName name of the feature flag that will be removed
         */
        public DeleteFeatureFlagCommand {
            if (Objects.isNull(flagName)) {
                throw new InvalidCommandException("Cannot delete feature flag. Feature flag name cannot be null");
            }
        }
    }

    /**
     * command used to update and create new feature flags
     *
     * @param flagName - unique name of feature flag, cannot be null
     * @param enabled  - flag status, cannot be null
     * @param users    - list of users for whom the flag will be enabled
     */
    record UpsertFeatureFlagCommand(FeatureFlagDefinition.FeatureFlagName flagName,
                                    FeatureFlagDefinition.FeatureFlagState enabled,
                                    Set<FeatureFlagDefinition.User> users) implements FeatureFlagCommand {
        /**
         * Cnstructor
         *
         * @param flagName - unique name of feature flag, cannot be null
         * @param enabled  - flag status, cannot be null
         * @param users    - list of users for whom the flag will be enabled
         */
        public UpsertFeatureFlagCommand {
            if (Objects.isNull(flagName)) {
                throw new InvalidCommandException("Cannot create upsert feature flag command with null flag name");
            }
        }
    }
}
