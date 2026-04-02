package io.github.orczykowski.springbootfeatureflags.integration

import io.github.orczykowski.springbootfeatureflags.FeatureFlagUser
import io.github.orczykowski.springbootfeatureflags.FeatureFlagUserContextProvider
import org.springframework.stereotype.Component

import java.util.concurrent.atomic.AtomicReference

@Component
class TestFeatureFlagUserContextProvider implements FeatureFlagUserContextProvider {

    private final AtomicReference<FeatureFlagUser> currentUser = new AtomicReference<>()

    @Override
    Optional<FeatureFlagUser> provide() {
        return Optional.ofNullable(currentUser.get())
    }

    void setUser(FeatureFlagUser user) {
        currentUser.set(user)
    }

    void clearUser() {
        currentUser.set(null)
    }
}
