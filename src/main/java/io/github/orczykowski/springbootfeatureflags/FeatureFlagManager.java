package io.github.orczykowski.springbootfeatureflags;

import java.util.Objects;

class FeatureFlagManager {
    private final FeatureFlagRepository repository;

    FeatureFlagManager(final FeatureFlagRepository repository) {
        this.repository = repository;
    }

    FeatureFlagDefinition create(final FeatureFlagCommand.UpsertFeatureFlagCommand command) {
        validateCreateCommand(command);
        final var featurelessDefinition = new FeatureFlagDefinition(command.flagName(), command.state(), command.users());
        repository.save(featurelessDefinition);
        return featurelessDefinition;
    }

    void update(final FeatureFlagCommand.UpsertFeatureFlagCommand command) {
        checkIsCommandPresent(command);
        repository.findByName(command.flagName())
                .map(definition -> definition.update(command.state(), command.users()))
                .ifPresentOrElse(repository::save, () -> {
                    throw new FeatureFlagsNotFoundException(command.flagName());
                });
    }

    void delete(final FeatureFlagCommand.DeleteFeatureFlagCommand command) {
        checkIsCommandPresent(command);
        repository.removeByName(command.flagName());
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
