package io.github.orczykowski.springbootfeatureflags;

import io.github.orczykowski.springbootfeatureflags.exceptions.FeatureFlagDuplicatedFeatureFlagException;
import io.github.orczykowski.springbootfeatureflags.exceptions.FeatureFlagsNotFoundException;
import io.github.orczykowski.springbootfeatureflags.exceptions.FeatureFlagInvalidCommandException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class FeatureFlagManager {
    private static final Logger log = LoggerFactory.getLogger(FeatureFlagManager.class);

    private final FeatureFlagRepository repository;
    private final FeatureFlagAssignmentRepository assignmentRepository;

    FeatureFlagManager(final FeatureFlagRepository repository,
                       final FeatureFlagAssignmentRepository assignmentRepository) {
        this.repository = repository;
        this.assignmentRepository = assignmentRepository;
    }

    public void execute(final FeatureFlagCommand command) {
        if (Objects.isNull(command)) {
            throw new FeatureFlagInvalidCommandException("Command cannot be null");
        }
        switch (command) {
            case FeatureFlagCommand.CreateFeatureFlag cmd -> create(cmd);
            case FeatureFlagCommand.DeleteFeatureFlag cmd -> delete(cmd);
            case FeatureFlagCommand.Enable cmd -> enable(cmd);
            case FeatureFlagCommand.Disable cmd -> disable(cmd);
            case FeatureFlagCommand.Restrict cmd -> restrict(cmd);
            case FeatureFlagCommand.AddRestrictedUser cmd -> addRestrictedUser(cmd);
            case FeatureFlagCommand.RemoveRestrictedUser cmd -> removeRestrictedUser(cmd);
        }
    }

    public FeatureFlagDefinition create(final FeatureFlagCommand.CreateFeatureFlag command) {
        if (Objects.isNull(command)) {
            throw new FeatureFlagInvalidCommandException("Command cannot be null");
        }
        if (repository.findByName(command.flagName()).isPresent()) {
            throw new FeatureFlagDuplicatedFeatureFlagException(command.flagName());
        }
        final var definition = new FeatureFlagDefinition(command.flagName(), command.enabled());
        repository.save(definition);
        assignmentRepository.saveAssignments(command.flagName(), command.users());
        log.debug("Feature flag created {}", definition);
        return definition;
    }

    private void delete(final FeatureFlagCommand.DeleteFeatureFlag command) {
        assignmentRepository.removeAllByFlagName(command.flagName());
        repository.removeByName(command.flagName());
        log.debug("Feature flag deleted {}", command);
    }

    private void enable(final FeatureFlagCommand.Enable command) {
        updateState(command.flagName(), FeatureFlagState.ANYBODY);
        log.debug("Feature flag enabled {}", command);
    }

    private void disable(final FeatureFlagCommand.Disable command) {
        updateState(command.flagName(), FeatureFlagState.NOBODY);
        log.debug("Feature flag disabled {}", command);
    }

    private void restrict(final FeatureFlagCommand.Restrict command) {
        updateState(command.flagName(), FeatureFlagState.RESTRICTED);
        log.debug("Feature flag restricted {}", command);
    }

    private void addRestrictedUser(final FeatureFlagCommand.AddRestrictedUser command) {
        updateState(command.flagName(), FeatureFlagState.RESTRICTED);
        assignmentRepository.addUser(command.flagName(), command.user());
        log.debug("Restricted user added {}", command);
    }

    private void removeRestrictedUser(final FeatureFlagCommand.RemoveRestrictedUser command) {
        assignmentRepository.removeUser(command.flagName(), command.user());
        log.debug("Restricted user removed {}", command);
    }

    private void updateState(final FeatureFlagName flagName, final FeatureFlagState state) {
        repository.findByName(flagName)
                .map(definition -> definition.update(state))
                .ifPresentOrElse(
                        repository::save,
                        () -> { throw new FeatureFlagsNotFoundException(flagName); }
                );
    }

    public sealed interface FeatureFlagCommand {

        FeatureFlagName flagName();

        record CreateFeatureFlag(FeatureFlagName flagName, FeatureFlagState enabled,
                                 FeatureFlagEntitledUsers users) implements FeatureFlagCommand {
            public CreateFeatureFlag {
                if (Objects.isNull(flagName)) {
                    throw new FeatureFlagInvalidCommandException("Cannot create feature flag. Flag name cannot be null");
                }
                if (Objects.isNull(enabled)) {
                    throw new FeatureFlagInvalidCommandException("Cannot create feature flag. State cannot be null");
                }
                users = Objects.requireNonNullElse(users, FeatureFlagEntitledUsers.empty());
            }
        }

        record DeleteFeatureFlag(FeatureFlagName flagName) implements FeatureFlagCommand {
            public DeleteFeatureFlag {
                if (Objects.isNull(flagName)) {
                    throw new FeatureFlagInvalidCommandException("Cannot delete feature flag. Flag name cannot be null");
                }
            }
        }

        record Enable(FeatureFlagName flagName) implements FeatureFlagCommand {
            public Enable {
                if (Objects.isNull(flagName)) {
                    throw new FeatureFlagInvalidCommandException("Cannot enable feature flag. Flag name cannot be null");
                }
            }
        }

        record Disable(FeatureFlagName flagName) implements FeatureFlagCommand {
            public Disable {
                if (Objects.isNull(flagName)) {
                    throw new FeatureFlagInvalidCommandException("Cannot disable feature flag. Flag name cannot be null");
                }
            }
        }

        record Restrict(FeatureFlagName flagName) implements FeatureFlagCommand {
            public Restrict {
                if (Objects.isNull(flagName)) {
                    throw new FeatureFlagInvalidCommandException("Cannot restrict feature flag. Flag name cannot be null");
                }
            }
        }

        record AddRestrictedUser(FeatureFlagName flagName, FeatureFlagUser user) implements FeatureFlagCommand {
            public AddRestrictedUser {
                if (Objects.isNull(flagName)) {
                    throw new FeatureFlagInvalidCommandException("Cannot add restricted user. Flag name cannot be null");
                }
                if (Objects.isNull(user)) {
                    throw new FeatureFlagInvalidCommandException("Cannot add restricted user. User cannot be null");
                }
            }
        }

        record RemoveRestrictedUser(FeatureFlagName flagName, FeatureFlagUser user) implements FeatureFlagCommand {
            public RemoveRestrictedUser {
                if (Objects.isNull(flagName)) {
                    throw new FeatureFlagInvalidCommandException("Cannot remove restricted user. Flag name cannot be null");
                }
                if (Objects.isNull(user)) {
                    throw new FeatureFlagInvalidCommandException("Cannot remove restricted user. User cannot be null");
                }
            }
        }
    }
}
