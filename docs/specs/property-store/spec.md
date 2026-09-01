---
type: spec
title: Property Store
updated: 2026-09-01
---

## Purpose

Lets a caller look up an `ApplicationProperty`'s value from a JVM system
property, an OS environment variable or a classpath properties file
through one `ApplicationPropertyStore` interface, and combine or wrap
those sources — fallback chaining, retrying under a transformed key,
pass-through debug logging — via `or`, `decorate` and the two decorator
implementations, instead of branching on the source in caller code.

## Requirements

### Requirement: Combining two stores with `or` prefers the first, falling back only when it found nothing
`req~property-store.or-fallback~1`

`ApplicationPropertyStore.or(other)` must return a store whose `get`
returns this store's result when present, and otherwise the other
store's result. The first store's result is never overridden by the
second's.

#### Scenario: Primary store has the value

- **WHEN** `a.or(b).get(property)` is called and `a.get(property)` is
  present
- **THEN** `a`'s value is returned without consulting `b`

#### Scenario: Primary store is empty, fallback has the value

- **WHEN** `a.or(b).get(property)` is called and `a.get(property)` is
  empty but `b.get(property)` is present
- **THEN** `b`'s value is returned

#### Scenario: Both stores are empty

- **WHEN** `a.or(b).get(property)` is called and both `a.get(property)`
  and `b.get(property)` are empty
- **THEN** `Optional.empty()` is returned

### Requirement: A store can be wrapped by a caller-supplied factory
`req~property-store.decorate~1`

`ApplicationPropertyStore.decorate(factory)` must return
`factory.apply(this)` — the store this factory produces when applied to
the current store. This is the composition hook the `RetryStore` and
`ComposedApplicationPropertyStore` decorators below are built with.

#### Scenario: Decorate applies the factory to the current store

- **WHEN** `store.decorate(factory)` is called
- **THEN** the returned store is `factory.apply(store)`

### Requirement: A property can be looked up from a JVM system property or an OS environment variable
`req~property-store.system-sources~1`

`SystemPropertyStore` looks up `System.getProperty(property.key())` and
`SystemEnvironmentStore` looks up `System.getenv(property.key())`. Each
returns `Optional.empty()` when the underlying lookup is `null`, and
otherwise wraps the found text via the property's own `value(String)`
method before returning it.

#### Scenario: System property present

- **WHEN** `SystemPropertyStore.get(property)` is called and
  `System.getProperty(property.key())` returns text
- **THEN** an `ApplicationPropertyValue` wrapping that text is returned

#### Scenario: System property absent

- **WHEN** `SystemPropertyStore.get(property)` is called and
  `System.getProperty(property.key())` returns `null`
- **THEN** `Optional.empty()` is returned

#### Scenario: Environment variable present

- **WHEN** `SystemEnvironmentStore.get(property)` is called and
  `System.getenv(property.key())` returns text
- **THEN** an `ApplicationPropertyValue` wrapping that text is returned

#### Scenario: Environment variable absent

- **WHEN** `SystemEnvironmentStore.get(property)` is called and
  `System.getenv(property.key())` returns `null`
- **THEN** `Optional.empty()` is returned

### Requirement: Properties can be loaded from a classpath properties file named after an environment
`req~property-store.properties-file-source~1`

`PropertiesFileStore(logFactory, environment)` loads
`<environment>.properties` from the classpath via
`ClassLoader.getResourceAsStream`, parsing each line by splitting on `=`
with `String.split("=")` (no limit argument). When the resource cannot be
found, or an `IOException` occurs while reading it, no properties are
loaded and every `get` on that store returns `Optional.empty()` — no
exception is raised. Because `split("=")` applies to every occurrence, a
line whose value itself contains `=` is truncated at the first `=`.

#### Scenario: Property present in the file

- **WHEN** the classpath resource contains a line `key=value` and
  `get(property)` is called for a property whose key is `key`
- **THEN** an `ApplicationPropertyValue` wrapping `value` is returned

#### Scenario: Resource file is missing

- **WHEN** `<environment>.properties` does not exist on the classpath
- **THEN** every `get(property)` on that store returns
  `Optional.empty()`
- **AND** no exception is raised

#### Scenario: Value containing `=` is truncated

- **WHEN** the classpath resource contains a line `key=a=b`
- **THEN** `get(property)` for `key` returns an `ApplicationPropertyValue`
  wrapping `a`, not `a=b`

### Requirement: A store's lookup can be retried once against the same store under a transformed property
`req~property-store.retry~1`

`RetryStore(logFactory, retryProperty, store)` first calls
`store.get(property)`. If that is empty, it calls
`retryProperty.apply(property)` to derive a second `ApplicationProperty`
and calls `store.get(...)` again with that — against the same
underlying store, not a different one.

#### Scenario: Direct lookup succeeds

- **WHEN** `RetryStore.get(property)` is called and
  `store.get(property)` is present
- **THEN** that value is returned and `retryProperty` is not consulted

#### Scenario: Direct lookup misses, retried lookup succeeds

- **WHEN** `RetryStore.get(property)` is called, `store.get(property)`
  is empty, and `store.get(retryProperty.apply(property))` is present
- **THEN** the retried lookup's value is returned

#### Scenario: Both lookups miss

- **WHEN** `RetryStore.get(property)` is called and both
  `store.get(property)` and `store.get(retryProperty.apply(property))`
  are empty
- **THEN** `Optional.empty()` is returned

### Requirement: A store's lookups can be wrapped with debug logging that does not alter the result
`req~property-store.logging-passthrough~1`

`ComposedApplicationPropertyStore(logFactory, store)` logs the property's
key before delegating to `store.get(property)`, and returns exactly what
the wrapped store returned.

#### Scenario: Wrapped store finds a value

- **WHEN** `ComposedApplicationPropertyStore.get(property)` is called and
  the wrapped store's `get(property)` is present
- **THEN** that same value is returned unchanged

#### Scenario: Wrapped store finds nothing

- **WHEN** `ComposedApplicationPropertyStore.get(property)` is called and
  the wrapped store's `get(property)` is empty
- **THEN** `Optional.empty()` is returned
