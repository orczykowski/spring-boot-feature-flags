package io.github.orczykowski.springbootfeatureflags;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

class FeatureFlagManager {
    private static final Logger log = LoggerFactory.getLogger(FeatureFlagManager.class);

    private final FeatureFlagRepository repository;

    FeatureFlagManager(final FeatureFlagRepository repository) {
        this.repository = repository;
    }

    FeatureFlagDefinition create(final FeatureFlagCommand.UpsertFeatureFlagCommand command) {
        validateCreateCommand(command);
        final var featurelessDefinition = new FeatureFlagDefinition(command.flagName(), command.state(), command.users());
        repository.save(featurelessDefinition);
        log.debug("Feature flag created {}", featurelessDefinition);
        return featurelessDefinition;
    }

    void update(final FeatureFlagCommand.UpsertFeatureFlagCommand command) {
        checkIsCommandPresent(command);
        repository.findByName(command.flagName())
                .map(definition -> update(command, definition))
                .ifPresentOrElse(repository::save, () -> {
                    throw new FeatureFlagsNotFoundException(command.flagName());
                });
    }

    void delete(final FeatureFlagCommand.DeleteFeatureFlagCommand command) {
        checkIsCommandPresent(command);
        repository.removeByName(command.flagName());
        log.debug("Feature flag deleted {}", command);
    }

    private FeatureFlagDefinition update(final FeatureFlagCommand.UpsertFeatureFlagCommand command, final FeatureFlagDefinition definition) {
        final var updated = definition.update(command.state(), command.users());
        log.debug("Feature flag updated {}", command);
        return updated;
    }

    private void validateCreateCommand(final FeatureFlagCommand.UpsertFeatureFlagCommand command) {
        if (Objects.isNull(command)) {
            throw new InvalidCommandException("Command cannot be null");
        }
        if (repository.findByName(command.flagName()).isPresent()) {
            throw new DuplicatedFeatureFlagException(command.flagName());
        }
    }

    private void checkIsCommandPresent(final FeatureFlagCommand command) {
        if (Objects.isNull(command)) {
            throw new InvalidCommandException("Command cannot be null");
        }
    }

}
