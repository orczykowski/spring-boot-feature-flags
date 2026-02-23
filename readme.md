# Spring Boot Feature Flags

A lightweight Spring Boot starter for feature flag management. Enable or disable application features globally or per user, expose flags via REST API, and track usage with Micrometer metrics.

[![Maven Central](https://img.shields.io/maven-central/v/io.github.orczykowski/spring-boot-feature-flags)](https://central.sonatype.com/artifact/io.github.orczykowski/spring-boot-feature-flags)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## Table of Contents

- [Requirements](#requirements)
- [Installation](#installation)
- [Quick Start](#quick-start)
- [Configuration Reference](#configuration-reference)
- [Feature Flag States](#feature-flag-states)
- [Verifying Flags in Code](#verifying-flags-in-code)
- [User Context Provider](#user-context-provider)
- [REST APIs](#rest-apis)
  - [Presenter API (Read-Only)](#presenter-api)
  - [Management API (CRUD)](#management-api)
- [Metrics](#metrics)
- [Custom Storage](#custom-storage)
  - [Read-Only Supplier](#read-only-supplier)
  - [Full Repository (for Management API)](#full-repository)
- [Contributing](#contributing)
- [License](#license)

## Requirements

- Java 21+
- Spring Boot 4.0+

## Installation

**Maven:**

```xml
<dependency>
    <groupId>io.github.orczykowski</groupId>
    <artifactId>spring-boot-feature-flags</artifactId>
    <version>2.0.0</version>
</dependency>
```

**Gradle:**

```groovy
implementation 'io.github.orczykowski:spring-boot-feature-flags:2.0.0'
```

Add the library package to your component scan:

```java
@SpringBootApplication(scanBasePackages = {"your.app.package", "io.github.orczykowski"})
```

## Quick Start

1. Enable feature flags and define them in `application.yml`:

```yaml
feature-flags:
  enabled: true

  definitions:
    - name: NEW_CHECKOUT
      enabled: ANYBODY
    - name: DARK_MODE
      enabled: RESTRICTED
      entitledUsers: [user-101, user-202]
    - name: LEGACY_EXPORT
      enabled: NOBODY
```

2. Inject `FeatureFlagVerifier` and check flags:

```java
@Service
public class CheckoutService {

    private final FeatureFlagVerifier featureFlags;

    public CheckoutService(FeatureFlagVerifier featureFlags) {
        this.featureFlags = featureFlags;
    }

    public void checkout() {
        if (featureFlags.verify(new FeatureFlagName("NEW_CHECKOUT"))) {
            // new checkout flow
        } else {
            // legacy checkout flow
        }
    }
}
```

## Configuration Reference

```yaml
feature-flags:
  enabled: true                         # Master switch (required)

  definitions:                          # Feature flag definitions
    - name: FLAG_NAME                   # Unique name (max 120 characters)
      enabled: ANYBODY                  # State: ANYBODY | NOBODY | RESTRICTED
      entitledUsers: [user1, user2]     # Required when state is RESTRICTED

  api:
    expose:
      enabled: false                    # Enable read-only presenter API
      path: /feature-flags              # Custom endpoint path
    manage:
      enabled: false                    # Enable CRUD management API
  manage:
    path: /manage/feature-flags         # Custom endpoint path

  metrics:
    enabled: false                      # Enable Micrometer metrics (requires Spring Boot Actuator)
```

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `feature-flags.enabled` | `boolean` | `false` | Master switch. Must be `true` for any feature to work. |
| `feature-flags.definitions` | `list` | `[]` | List of feature flag definitions. |
| `feature-flags.api.expose.enabled` | `boolean` | `false` | Enable the read-only presenter REST endpoint. |
| `feature-flags.api.expose.path` | `string` | `/feature-flags` | Path for the presenter API. |
| `feature-flags.api.manage.enabled` | `boolean` | `false` | Enable the CRUD management REST endpoints. |
| `feature-flags.manage.path` | `string` | `/manage/feature-flags` | Path for the management API. |
| `feature-flags.metrics.enabled` | `boolean` | `false` | Enable Micrometer metrics publishing. Requires `MeterRegistry` on classpath (Spring Boot Actuator). |

## Feature Flag States

| State | Behavior |
|-------|----------|
| `ANYBODY` | Enabled for all users. |
| `NOBODY` | Disabled for everyone. |
| `RESTRICTED` | Enabled only for users listed in `entitledUsers`. Requires a [`UserContextProvider`](#user-context-provider) bean. |

## Verifying Flags in Code

Inject `FeatureFlagVerifier` into any Spring-managed component:

```java
boolean isEnabled = featureFlags.verify(new FeatureFlagName("MY_FLAG"));
```

Behavior:
- Returns `true` if the flag exists and is enabled (considering user context for `RESTRICTED` flags).
- Returns `false` if the flag does not exist, is `NOBODY`, or the current user is not in the entitled list.
- Publishes metrics for each verification if metrics are enabled.

## User Context Provider

To use `RESTRICTED` flags, implement the `UserContextProvider` functional interface and register it as a Spring bean. This tells the library who the current user is.

```java
@Bean
public UserContextProvider userContextProvider() {
    return () -> {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return Optional.of(new User(auth.getName()));
        }
        return Optional.empty();
    };
}
```

The `provide()` method should return a deterministic, unique user or user-group identifier. When no `UserContextProvider` bean is registered, all flags are evaluated globally (`RESTRICTED` flags will not match any user).

The `User` record accepts both `String` and `Number` identifiers:

```java
new User("user-101")   // String ID
new User(42)           // Numeric ID
```

## REST APIs

### Presenter API

Read-only endpoint that returns the names of feature flags enabled for the current user. Useful for frontend applications that need to know which features are available.

> Only enabled flags are returned to prevent leaking information about unreleased features.

**Enable it:**

```yaml
feature-flags:
  enabled: true
  api:
    expose:
      enabled: true
```

---

#### `GET /feature-flags`

Returns names of all feature flags enabled for the current user context.

**Response** `200 OK`:

```json
{
  "featureFlags": ["NEW_CHECKOUT", "DARK_MODE"]
}
```

**Example:**

```bash
curl http://localhost:8080/feature-flags
```

---

### Management API

CRUD endpoints for administering feature flags at runtime. Changes made through this API are stored in memory by default. For persistent storage, provide a custom [`FeatureFlagRepository`](#full-repository) bean.

> **Cluster note:** The default in-memory storage is not shared across instances. When running in a cluster, changes must be applied to each instance individually, or a shared storage backend must be implemented.

**Enable it:**

```yaml
feature-flags:
  enabled: true
  api:
    manage:
      enabled: true
```

---

#### `GET /manage/feature-flags`

Returns all defined feature flags.

**Response** `200 OK`:

```json
{
  "definitions": [
    {
      "name": "NEW_CHECKOUT",
      "enabled": "ANYBODY",
      "entitledUsers": []
    },
    {
      "name": "DARK_MODE",
      "enabled": "RESTRICTED",
      "entitledUsers": ["user-101", "user-202"]
    }
  ]
}
```

**Example:**

```bash
curl http://localhost:8080/manage/feature-flags
```

---

#### `POST /manage/feature-flags`

Creates a new feature flag.

**Request body:**

```json
{
  "name": "BETA_SEARCH",
  "enabled": "RESTRICTED",
  "entitledUsers": ["user-101"]
}
```

**Responses:**

| Status | Description |
|--------|-------------|
| `201 Created` | Flag created. Returns the created definition. |
| `409 Conflict` | A flag with that name already exists. |
| `422 Unprocessable Entity` | Invalid request (missing name, invalid state, etc.). |

**Example:**

```bash
curl -X POST http://localhost:8080/manage/feature-flags \
  -H "Content-Type: application/json" \
  -d '{"name": "BETA_SEARCH", "enabled": "RESTRICTED", "entitledUsers": ["user-101"]}'
```

---

#### `PUT /manage/feature-flags/{flagName}`

Updates an existing feature flag's state and entitled users.

**Request body:**

```json
{
  "enabled": "ANYBODY",
  "entitledUsers": []
}
```

**Responses:**

| Status | Description |
|--------|-------------|
| `200 OK` | Flag updated. |
| `404 Not Found` | No flag with that name exists. |
| `422 Unprocessable Entity` | Invalid request. |

**Example:**

```bash
curl -X PUT http://localhost:8080/manage/feature-flags/BETA_SEARCH \
  -H "Content-Type: application/json" \
  -d '{"enabled": "ANYBODY", "entitledUsers": []}'
```

---

#### `DELETE /manage/feature-flags/{flagName}`

Deletes a feature flag.

**Responses:**

| Status | Description |
|--------|-------------|
| `204 No Content` | Flag deleted. |

**Example:**

```bash
curl -X DELETE http://localhost:8080/manage/feature-flags/BETA_SEARCH
```

---

## Metrics

When metrics are enabled, the library publishes counters via Micrometer. Requires Spring Boot Actuator (`spring-boot-starter-actuator`) on the classpath.

```yaml
feature-flags:
  enabled: true
  metrics:
    enabled: true
```

### Published Metrics

| Metric Name | Type | Tags | Description |
|-------------|------|------|-------------|
| `feature_flags_verification_result.count` | Counter | `flag_name`, `user`, `result` | Incremented on each flag verification. |
| `feature_flags_not_existing_flag.count` | Counter | — | Incremented when a non-existent flag is verified. |

You can provide a custom `MetricsPublisher` bean to replace the default Micrometer implementation:

```java
@Bean
public MetricsPublisher metricsPublisher() {
    return new MyCustomMetricsPublisher();
}
```

## Custom Storage

By default, feature flags are read from the YAML/properties configuration and stored in a `ConcurrentHashMap`. You can replace this with any storage backend (database, Redis, remote config service, etc.).

### Read-Only Supplier

If you only need flag verification (no management API), implement `FeatureFlagSupplier`:

```java
@Bean
public FeatureFlagSupplier featureFlagSupplier() {
    return new FeatureFlagSupplier() {
        @Override
        public Stream<FeatureFlagDefinition> findAllEnabledFeatureFlags() {
            // return enabled flags from your storage
        }

        @Override
        public Optional<FeatureFlagDefinition> findByName(FeatureFlagName name) {
            // look up a single flag
        }
    };
}
```

### Full Repository

When using the management API, implement `FeatureFlagRepository` (which extends `FeatureFlagSupplier`) to support CRUD operations:

```java
@Bean
public FeatureFlagRepository featureFlagRepository() {
    return new FeatureFlagRepository() {
        @Override
        public FeatureFlagDefinition save(FeatureFlagDefinition definition) { /* ... */ }

        @Override
        public void removeByName(FeatureFlagName flagName) { /* ... */ }

        @Override
        public Stream<FeatureFlagDefinition> findAll() { /* ... */ }

        @Override
        public Stream<FeatureFlagDefinition> findAllEnabledFeatureFlags() { /* ... */ }

        @Override
        public Optional<FeatureFlagDefinition> findByName(FeatureFlagName name) { /* ... */ }
    };
}
```

> When implementing custom storage, consider performance aspects such as caching — flag verification may be called on every request.

## Contributing

This is an open-source project. Contributions are welcome — create a pull request with a description of your changes and include tests.

## License

[MIT](https://opensource.org/licenses/MIT)
