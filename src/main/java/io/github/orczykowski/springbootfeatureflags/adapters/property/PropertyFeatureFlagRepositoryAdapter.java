package io.github.orczykowski.springbootfeatureflags.adapters.property;

import io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinition;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagName;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagRepository;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagSupplier;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagsPropertySource;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PropertyFeatureFlagRepositoryAdapter implements FeatureFlagSupplier, FeatureFlagRepository {

    private final Map<FeatureFlagName, FeatureFlagDefinition> definitions;

    public PropertyFeatureFlagRepositoryAdapter(final FeatureFlagsPropertySource definitionsRepository,
                                                 final InMemoryFeatureFlagAssignmentRepository assignmentRepository) {
        this.definitions = definitionsRepository.definitions()
                .stream()
                .map(FeatureFlagsPropertySource.FeatureFlagDefinitionDto::asDefinition)
                .collect(Collectors.toConcurrentMap(FeatureFlagDefinition::name, Function.identity()));

        definitionsRepository.definitions().forEach(dto -> {
            var users = dto.asUsers();
            if (!users.isEmpty()) {
                assignmentRepository.saveAssignments(new FeatureFlagName(dto.name()), users);
            }
        });
    }

    @Override
    public Stream<FeatureFlagDefinition> findAllEnabledFeatureFlags() {
        return definitions
                .values()
                .stream()
                .filter(FeatureFlagDefinition::isEnable);
    }

    @Override
    public Optional<FeatureFlagDefinition> findByName(final FeatureFlagName featureFlagName) {
        return Optional.ofNullable(definitions.get(featureFlagName));
    }

    @Override
    public void removeByName(final FeatureFlagName flagName) {
        definitions.remove(flagName);
    }

    @Override
    public FeatureFlagDefinition save(final FeatureFlagDefinition definition) {
        return definitions.put(definition.name(), definition);
    }

    @Override
    public Stream<FeatureFlagDefinition> findAll() {
        return definitions.values().stream();
    }
}
