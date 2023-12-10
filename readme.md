# spring boot feature flags

* [Description](#description)
* [Features](#available-features)
* [How to use](#how-to-use)
* [Configuration](#how-to-configure)
    * [Base](#base)
    * [Additional](#additional-configuration)

## Description

> Effortlessly manage and control application functionalities with our versatile library. Designed for consistent
> handling on both the backend and frontend, this tool allows you to easily enable or disable features, providing a
> seamless and technically efficient solution for optimizing user experiences.

### Available features

Library:

- Facilitates the seamless enabling and disabling of functionalities, catering to both global application settings or
  selective configuration for individual users.
- Employs MetricRegistry for efficient metric publication, offering a transparent insight into feature utilization,
  aiding
  in informed decision-making.
- Furnishes a comprehensive API for retrieving the values of exclusively enabled feature flags, ensuring a uniform
  approach to managing functionality availability.
- Enables the straightforward definition of sources for validating feature flags, extending through a straightforward
  interface for enhanced customization.
- Equips you with the capability to efficiently manage feature flags through a dedicated API, further streamlining the
  control and customization of your application's functionalities.

`(*) By default, library is not aware of being used in a cluster with all the consequences of that.`

## How to use

1. [Configure](#how-to-configure) library
2. On backend, to verify whether a flag is enabled, you should inject and utilize the FeatureFlagVerifier.
3. On frontend, you have access to an [REST endpoint](#fetch-enabled-feature-flags-api) that will return names of only enabled flags.

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

or: 

```groovy
    implementation 'io.github.orczykowski:springboot-feature-flag:1.0.0'
```

2. Enable feature-flags in properties file:

```yaml
feature-flags:
  enabled: true
```

3. Define feature flags
   You can define two type of feature flags. Allowing you to enable functionality globally or only for specific users.
   If you want to be able to define feature flags for specific user, you need to create a spring Bean implementing
   `UserContextProvider`. More about how to configure UserContextProvider [here](#user-context-provider)

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
make it a bean) `UserContextProvider` and create bean of it in spring context.

```java
public interface UserContextProvider {

    Optional<FeatureFlagDefinition.User> provide();
}
```

Provide method should return a unique user/user group identifier. It must be deterministic.
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

2. Optionally, you can define custom endpoint path, default is `\feature-flags`:

  ```yaml
feature-flags:
  api:
    expose:
      path: my-feature-flags
```

#### API manage

> A specialized API designed for the administration of feature flags provides the capability to execute CRUD (Create,
> Read, Update, Delete) operations. It is important to note that the library, by default, operates independently across
> instances within a cluster. Therefore, when utilizing the default implementation relying on properties files, it is
> imperative to effect any changes on each service instance individually to ensure consistent behavior and feature flag
> maintenance.

<details>
 <summary><code>GET</code> <code><b>manage/feature-flags</b></code></summary>

##### Responses

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

You have the flexibility to establish a custom storage location for feature flags, such as a database. This can be
achieved by implementing the FeatureFlagSupplier interface and annotating the implementation as a @Bean. It is crucial
to note that in case the endpoint supplier is enabled, a comprehensive repository must be defined, as elaborated in the
subsequent section.

```java
public interface FeatureFlagSupplier {

    Stream<FeatureFlagDefinition> findAllEnabledFeatureFlags();

    Optional<FeatureFlagDefinition> findByName(FeatureFlagDefinition.FeatureFlagName featureFlagName);

}
```

!**When implementing your own storage, remember about performance aspects such as cache, etc.**!

#### Custom feature flag repository

When utilizing an endpoint for feature flag management and aiming to establish a customized repository for storing these
flags, the technical procedure involves the implementation of the **FeatureFlagRepository** as a @Bean in lieu of the
**FeatureFlagSupplier**. Notably, the **FeatureFlagRepository** extends **FeatureFlagSupplier**, necessitating the
implementation of
two methods inherited from the supplier interface, along with an additional three methods that are specific to the
repository.

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

