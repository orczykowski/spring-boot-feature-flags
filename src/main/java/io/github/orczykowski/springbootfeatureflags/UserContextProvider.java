package io.github.orczykowski.springbootfeatureflags;

import java.util.Optional;

public interface UserContextProvider {

    Optional<User> provide();
}
