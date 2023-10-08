package com.github.orczykowski.springbootfeatureflags;

import java.util.Optional;

class GuestUserContextProvider implements UserContextProvider {
  @Override
  public Optional<User> provide() {
    return Optional.empty();
  }
}
