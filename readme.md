# spring boot feature flags

* [Description](#description)
* [Features](#available-features)
* [How to use](#how-to-use)
* [Configuration](#how-to-configure)
    * [Base](#base)
    * [Additional](#additional-configuration)

## Description

> **spring boot feature flags** is a library that allows developers to implement feature flags in Spring boot
> applications. Feature flags enable toggling, enabling, or disabling features in real-time without modifying the source
> code. This allows developers to test new features, control deployments, and easily adapt to changing environmental
> conditions.

### Available features

Library:

- allows you to enable and disable functionality for all users or selectively for a single user
  user via configuration
- publish metrics (via MeterRegistry) that allow us to clearly determine the level of use of our feature
- provides API that allows you to retrieve the value of only enabled feature flags to manage power in a
  consistent manner
  functionality availability
- allows you to easily define sources for validating feature flags by extending a simple interface
- provides ability to manage feature flags via API (*)

`(*) Bu Default, library is not aware of being used in a cluster with all the consequences of that`

## How to use

1. [Configure](#how-to-configure) library
2. On backend site you can verify

## How to configure

### base configuration

1. Add dependency

  ```xml

<dependency>
    <groupId>io.github.orczykowski</groupId>
    <artifactId>springboot-feature-flags</artifactId>
    <version>1.0.0</version>
</dependency>
```

2. Enable feature-flags in properties file:

```yaml
feature-flags:
  enabled: true
```

3. Define feature flags
   You can define two type of feature flags. Allowing you to enable functionality globally or only for specific users.
   if you want to be able to define feature flags for the user, you need to create a Bean implementing
   `UserContextProvider`. More about it [here](#user-context-provider)

```
ON - functionality will be enabled for all users
OFF - functionality will be disabled 
RESTRICTED_FOR_USERS - functionality will only be available to users included in the list. 
```

and:

```yaml
feature-flags:
  definitions:
    - name: FLAG_1
      enabled: ON
    - name: FLAG_2
      enabled: ON
    - name: FLAG_3
      enabled: OFF
```

### Additional configuration

#### User context provider

If you want to be able to enable or disable functionality for a single user/group of users, you need to implement (and
make it a bean) `UserContextProvider`.

```java
public interface UserContextProvider {

    Optional<FeatureFlagDefinition.User> provide();
}
```

The provide method should return a unique user/user group identifier. It must be deterministic.
Now You can define entitled users in Feature Flag configuration:

```yaml
feature-flags:
  definitions:
    - name: FLAG_1
      enabled: RESTRICTED_FOR_USERS
      entitledUsers: 213, 1232
...
```

#### API presenting

> The api returns only the enabled feature flags (User context is taken into account if UserContexProvider has been
> defined). It allows their logic to be taken into account in a coherent way in other parts of our system, e.g. on the
> frontend. Flags have been limited to enabled flags only to limit leakage of information about tested features

##### fetch enabled feature flags api

<details>
 <summary><code>GET</code> <code><b>/feature-flags</b></code></summary>

##### Responses

| http code | content-type       | response                                 |
|-----------|--------------------|------------------------------------------|
| `200`     | `application/json` | `{"featureFlags": ["FLAG_1", "FLAG_2"]}` |

##### Example cURL

> ```javascript
>  curl http://localhost:8080/feature-flags -H "Content-Type: application/json"
> ```

</details>

##### How to configure

1. To enable API you need to change property value:

  ```yaml
feature-flags:
  api:
    expose:
      enabled: true
```

2. Optionally, you can defined custom endpoint path, default is `\feature-flags`:

  ```yaml
feature-flags:
  api:
    expose:
      path: my-feature-flags
```

#### API manage

> API dedicated to managing feature flags. It gives You possibility to make CRUD operations. Remember, by default
> library is not aware of other instances/working in cluster,
> so if you use default implementation based on properties files, you must make the changes on each instance of your
> service to maintain consistency.

<details>
 <summary><code>GET</code> <code><b>manage/feature-flags</b></code></summary>

##### Responses

String name, FeatureFlagState enabled, Set<String> entitledUsers

| http code | content-type       | response                                                                                    |
|-----------|--------------------|---------------------------------------------------------------------------------------------|
| `200`     | `application/json` | `{"definitions": [{"name": "FLAG_NAME", "enabled": "OFF", "entitledUsers": ["USR_ID_1"]}]}` |

##### Example cURL

> ```javascript
>  curl http://localhost:8080/manage/feature-flags -H "Content-Type: application/json"
> ```

</details>
<details>
 <summary><code>DELETE</code> <code><b>manage/feature-flags/{FEATURE_FLAG_NAME}</b></code></summary>

##### Responses

String name, FeatureFlagState enabled, Set<String> entitledUsers

| http code | content-type       | response |
|-----------|--------------------|----------|
| `204`     | `application/json` | void     |

##### Example cURL

> ```javascript
>  curl -X DELETE http://localhost:8080/manage/feature-flags 
> ```

</details>
<details>
 <summary><code>POST</code> <code><b>manage/feature-flags</b></code></summary>

##### Responses

String name, FeatureFlagState enabled, Set<String> entitledUsers

| http code | content-type       | request                                                                | response                                                               |
|-----------|--------------------|------------------------------------------------------------------------|------------------------------------------------------------------------|
| `201`     | `application/json` | {"name": "FLAG_NAME", "enabled": "OFF", "entitledUsers": ["USR_ID_1"]} | {"name": "FLAG_NAME", "enabled": "OFF", "entitledUsers": ["USR_ID_1"]} |
| `409`     | `application/json` | {"name": "FLAG_NAME", "enabled": "OFF", "entitledUsers": ["USR_ID_1"]} | {"message": ""}                                                        |
| `422`     | `application/json` | {"name": "invalid request"}                                            | {"message": ""}                                                        |

##### Example cURL

> ```javascript
>  curl -X POST http://localhost:8080/manage/feature-flags -H "Content-Type: application/json" -d '{"name": "FLAG_NAME", "enabled": "OFF", "entitledUsers": ["USR_ID_1"]}'
> ```

</details>


<details>
 <summary><code>PUT</code> <code><b>manage/feature-flags/{FEATURE_FLAG_NAME}</b></code></summary>

##### Responses

String name, FeatureFlagState enabled, Set<String> entitledUsers

| http code | content-type       | request                                                                       | response                                                               |
|-----------|--------------------|-------------------------------------------------------------------------------|------------------------------------------------------------------------|
| `201`     | `application/json` | {"name": "FLAG_NAME", "enabled": "OFF", "entitledUsers": ["USR_ID_1"]}        | {"name": "FLAG_NAME", "enabled": "OFF", "entitledUsers": ["USR_ID_1"]} |
| `422`     | `application/json` | {"name": "invalid request"}                                                   | {"message": ""}                                                        |
| `404`     | `application/json` | {"name": "NO_EXISTING_FLAG", "enabled": "OFF", "entitledUsers": ["USR_ID_1"]} | {"message": ""}                                                        |

##### Example cURL

> ```javascript
>  curl -X PUT http://localhost:8080/manage/feature-flags -H "Content-Type: application/json" -d '{"name": "FLAG_NAME", "enabled": "OFF", "entitledUsers": ["USR_ID_1"]}'
> ```

</details>

1. To enable API you need to change property value:

  ```yaml
feature-flags:
  api:
    menage:
      enabled: true
```

2. Optionally, you can set custom endpoint path, default is `\managment\feature-flags`:

```yaml
feature-flags:
  api:
    menage:
      path: my-admin-feature-flags
```

### Custom storage

You can define your own place where you will store feature flags, e.g. in db. All you need to do is implement the
`FeatureFlagSupplier` interface (and make implementation as @Bean).
Important! If you have enabled endpoint supplier, you must define the full repository (description can be found in the
next section)

```java
public interface FeatureFlagSupplier {

    Stream<FeatureFlagDefinition> findAllEnabledFeatureFlags();

    Optional<FeatureFlagDefinition> findByName(FeatureFlagDefinition.FeatureFlagName featureFlagName);

}
```

!**When implementing your own storage, remember about performance aspects such as cache, etc**!

#### Custom feature flag repository

If you use endpoint to manage flags and you want to have your own place to store feature flags, You heave to
implement `FeatureFlagRepository` (as a @Bean) instead of `FeatureFlagSupplier`. Feature
FeatureFlagRepository extends FeatureFlagSupplier, so You have to implement extra 5 methods.

```java
public interface FeatureFlagRepository {
    void removeByName(FeatureFlagDefinition.FeatureFlagName flagName);

    FeatureFlagDefinition save(FeatureFlagDefinition definition);

    Stream<FeatureFlagDefinition> findAll();

    Stream<FeatureFlagDefinition> findAllEnabledFeatureFlags();

    Optional<FeatureFlagDefinition> findByName(FeatureFlagDefinition.FeatureFlagName featureFlagName);
}
```

## How to contribute

Its open source so feel free to contribute. Create pull request with some description what you want change / add, write
tests and wait wor merge. 

