# spring boot feature flags

## Description

**spring boot feature flags** is a library that allows developers to implement feature flags in Spring boot
applications.
Feature flags enable dynamically toggling, enabling, or disabling features in real-time without modifying the source
code.
This allows developers to test new features, control deployments, and easily adapt to changing environmental conditions.

### Features:

- Enabling/Disabling Features: **Spring boot feature flags** enables developers to dynamically manage features by
  deciding which ones are
  currently active or deactivated.
- Conditionally Enabling Features: The ability to set conditions that determine whether a specific feature should be
  available to users, depending on user type, location, or other parameters.
- Integration with Various Configuration Sources: **Spring boot feature flags** allows configuration through different
  sources, such as
  configuration files, databases, or even configuration management systems.
- Support for Different State Storage Strategies: The library supports various state storage strategies, such as
  in-memory, in files, or in a database.
- Ease of Use: Provides a simple API interface for using feature flags in the application code.
- It provides the ability to retrieve enabled feature flags for a user through the API.

## How to configure

1. Add dependency
2. Enable feature-flags in properties
3. Define feature flags

### Additional configuration

- api presenting
- custom ff provider
- user context provider

## How to contribute

Feel free to contribute.

## TODO
- enable cacheing + endpoint management
- manage feature flags / security is on your site
- enable metrics / prometheus
- release 1.0.0
- readme:
    - add info about configuration
    - fix description
    - add diagram how it can work
