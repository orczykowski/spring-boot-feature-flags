package io.github.orczykowski.springbootfeatureflags;

import io.github.orczykowski.springbootfeatureflags.FeatureFlagCommand.UpsertFeatureFlagCommand;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@ConditionalOnExpression(value = "${feature-flags.enabled:false} and ${feature-flags.api.manage.enabled:false}")
@RequestMapping(value = "${feature-flags.manage.path:/manage/feature-flags}", produces = MediaType.APPLICATION_JSON_VALUE)
class FeatureFlagManagerApi {
    private final FeatureFlagManager featureFlagManager;
    private final FeatureFlagRepository featureFlagRepository;

    FeatureFlagManagerApi(final FeatureFlagManager featureFlagManager, final FeatureFlagRepository featureFlagRepository) {
        this.featureFlagManager = featureFlagManager;
        this.featureFlagRepository = featureFlagRepository;
    }

    @PostMapping(
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    FeatureFlagDefinitionDto create(@RequestBody final FeatureFlagDefinitionDto request) {
        var featureFlag = featureFlagManager.create(request.asUpsertCommand());
        return FeatureFlagDefinitionDto.from(featureFlag);
    }

    @PutMapping(path = "/{featureFlagName}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    void update(@PathVariable("featureFlagName") final String featureFlagName,
                @RequestBody final UpdateRequest request) {
        featureFlagManager.update(request.asUpsertCommand(featureFlagName));
    }

    @DeleteMapping("/{featureFlagName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable("featureFlagName") final String featureFlagName) {
        featureFlagManager.delete(new FeatureFlagCommand.DeleteFeatureFlagCommand(new FeatureFlagName(featureFlagName)));
    }

    @GetMapping
    FeatureFlagDefinitionsResponse findAll() {
        return featureFlagRepository.findAll()
                .map(FeatureFlagDefinitionDto::from)
                .collect(Collectors.collectingAndThen(Collectors.toList(), FeatureFlagDefinitionsResponse::new));
    }

    record FeatureFlagDefinitionDto(String name, FeatureFlagState enabled, Set<String> entitledUsers) {
        static FeatureFlagDefinitionDto from(final FeatureFlagDefinition definition) {
            return definition.entitledUsers()
                    .stream()
                    .map(User::toString)
                    .collect(Collectors.collectingAndThen(Collectors.toUnmodifiableSet(),
                            users -> new FeatureFlagDefinitionDto(
                                    definition.name().toString(),
                                    definition.enabled(),
                                    users)));
        }

        UpsertFeatureFlagCommand asUpsertCommand() {
            final var entitledUsers = Objects.requireNonNullElse(this.entitledUsers, new HashSet<String>())
                    .stream()
                    .map(User::new)
                    .collect(Collectors.toSet());
            return new UpsertFeatureFlagCommand(
                    new FeatureFlagName(name),
                    enabled,
                    entitledUsers);
        }
    }

    record FeatureFlagDefinitionsResponse(List<FeatureFlagDefinitionDto> definitions) {
    }

    record UpdateRequest(Set<String> entitledUsers, FeatureFlagState enabled) {
        UpsertFeatureFlagCommand asUpsertCommand(final String name) {
            final var users = Objects.requireNonNullElse(entitledUsers, new HashSet<String>())
                    .stream()
                    .map(User::new)
                    .collect(Collectors.toSet());

            return new UpsertFeatureFlagCommand(new FeatureFlagName(name), enabled, users);
        }
    }
}
