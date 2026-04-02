package io.github.orczykowski.springbootfeatureflags.api;

import io.github.orczykowski.springbootfeatureflags.FeatureFlagEntitledUsers;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagAssignmentSupplier;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagDefinition;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagManager;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagName;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagRepository;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagState;
import io.github.orczykowski.springbootfeatureflags.FeatureFlagUser;
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
@RequestMapping(value = "${feature-flags.api.manage.path:/manage/feature-flags}", produces = MediaType.APPLICATION_JSON_VALUE)
public class FeatureFlagManagerApi {
    private final FeatureFlagManager featureFlagManager;
    private final FeatureFlagRepository featureFlagRepository;
    private final FeatureFlagAssignmentSupplier assignmentSupplier;

    public FeatureFlagManagerApi(final FeatureFlagManager featureFlagManager,
                          final FeatureFlagRepository featureFlagRepository,
                          final FeatureFlagAssignmentSupplier assignmentSupplier) {
        this.featureFlagManager = featureFlagManager;
        this.featureFlagRepository = featureFlagRepository;
        this.assignmentSupplier = assignmentSupplier;
    }

    @PostMapping(
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    FeatureFlagDefinitionDto create(@RequestBody final CreateRequest request) {
        var cmd = new FeatureFlagManager.FeatureFlagCommand.CreateFeatureFlag(
                new FeatureFlagName(request.name()),
                request.enabled(),
                request.toEntitledUsers());
        var featureFlag = featureFlagManager.create(cmd);
        return FeatureFlagDefinitionDto.from(featureFlag, cmd.users());
    }

    @PutMapping("/{featureFlagName}/enable")
    void enable(@PathVariable("featureFlagName") final String featureFlagName) {
        featureFlagManager.execute(new FeatureFlagManager.FeatureFlagCommand.Enable(new FeatureFlagName(featureFlagName)));
    }

    @PutMapping("/{featureFlagName}/disable")
    void disable(@PathVariable("featureFlagName") final String featureFlagName) {
        featureFlagManager.execute(new FeatureFlagManager.FeatureFlagCommand.Disable(new FeatureFlagName(featureFlagName)));
    }

    @PutMapping("/{featureFlagName}/restrict")
    void restrict(@PathVariable("featureFlagName") final String featureFlagName) {
        featureFlagManager.execute(new FeatureFlagManager.FeatureFlagCommand.Restrict(new FeatureFlagName(featureFlagName)));
    }

    @PostMapping("/{featureFlagName}/users/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    void addRestrictedUser(@PathVariable("featureFlagName") final String featureFlagName,
                           @PathVariable("userId") final String userId) {
        featureFlagManager.execute(new FeatureFlagManager.FeatureFlagCommand.AddRestrictedUser(
                new FeatureFlagName(featureFlagName), new FeatureFlagUser(userId)));
    }

    @DeleteMapping("/{featureFlagName}/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void removeRestrictedUser(@PathVariable("featureFlagName") final String featureFlagName,
                              @PathVariable("userId") final String userId) {
        featureFlagManager.execute(new FeatureFlagManager.FeatureFlagCommand.RemoveRestrictedUser(
                new FeatureFlagName(featureFlagName), new FeatureFlagUser(userId)));
    }

    @DeleteMapping("/{featureFlagName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable("featureFlagName") final String featureFlagName) {
        featureFlagManager.execute(new FeatureFlagManager.FeatureFlagCommand.DeleteFeatureFlag(new FeatureFlagName(featureFlagName)));
    }

    @GetMapping
    FeatureFlagDefinitionsResponse findAll() {
        return featureFlagRepository.findAll()
                .map(def -> FeatureFlagDefinitionDto.from(def,
                        assignmentSupplier.findUsersByFlagName(def.name())))
                .collect(Collectors.collectingAndThen(Collectors.toList(), FeatureFlagDefinitionsResponse::new));
    }

    record CreateRequest(String name, FeatureFlagState enabled, Set<String> entitledUsers) {
        FeatureFlagEntitledUsers toEntitledUsers() {
            var users = Objects.requireNonNullElse(entitledUsers, new HashSet<String>())
                    .stream()
                    .map(FeatureFlagUser::new)
                    .collect(Collectors.toSet());
            return FeatureFlagEntitledUsers.of(users);
        }
    }

    record FeatureFlagDefinitionDto(String name, FeatureFlagState enabled, Set<String> entitledUsers) {
        static FeatureFlagDefinitionDto from(final FeatureFlagDefinition definition, final FeatureFlagEntitledUsers users) {
            var userIds = users.stream()
                    .map(FeatureFlagUser::toString)
                    .collect(Collectors.toUnmodifiableSet());
            return new FeatureFlagDefinitionDto(
                    definition.name().toString(),
                    definition.enabled(),
                    userIds);
        }
    }

    record FeatureFlagDefinitionsResponse(List<FeatureFlagDefinitionDto> definitions) {
    }
}
