---
type: spec
title: Property Value Access
updated: 2026-09-01
---

## Purpose

Lets a caller declare an application property (its key and whether it is a
secret) via `ApplicationProperty`, and read a found value back out through
`ApplicationPropertyValue` — as plain text, as partially masked text, or
converted to a numeric type — without the caller having to guard every
conversion against a parse exception itself.

## Requirements

### Requirement: A property's raw text is wrapped according to whether the property is a secret
`req~property-value-access.value-wrapping~1`

`ApplicationProperty.value(String)` must return an
`ApplicationPropertyValue.ImmutableSecured` when `isSecret()` is `true`,
and an `ApplicationPropertyValue.Immutable` when it is `false` (the
default). Only the secured wrapper masks its text on
`asStringProtected()`.

#### Scenario: Non-secret property

- **WHEN** `value(text)` is called on a property whose `isSecret()` is
  `false`
- **THEN** an `ApplicationPropertyValue.Immutable` wrapping `text` is
  returned

#### Scenario: Secret property

- **WHEN** `value(text)` is called on a property whose `isSecret()` is
  `true`
- **THEN** an `ApplicationPropertyValue.ImmutableSecured` wrapping `text`
  is returned

### Requirement: A missing property's exception names the property's key
`req~property-value-access.missing-property-exception~1`

`ApplicationProperty.exception()` must return a
`MissingApplicationPropertyException` whose message contains the
property's own `key()`.

#### Scenario: Exception message names the key

- **WHEN** `exception()` is called on a property whose `key()` is
  `"aws.dynamo.region"`
- **THEN** the returned exception's message contains
  `"aws.dynamo.region"`

### Requirement: A value's protected text masks all but its leading 20%
`req~property-value-access.protected-text~1`

`asStringProtected()` defaults to returning the full, unmasked
`asString()`. `ApplicationPropertyValue.ImmutableSecured` overrides it to
keep the leading 20% of the text's characters (rounded down) and replace
the remainder with the fixed literal `"*******"`, regardless of how long
the remainder actually is.

#### Scenario: Unsecured value is not masked

- **WHEN** `asStringProtected()` is called on an
  `ApplicationPropertyValue.Immutable`
- **THEN** it returns the same text as `asString()`

#### Scenario: Secured value is masked

- **WHEN** `asStringProtected()` is called on an
  `ApplicationPropertyValue.ImmutableSecured` wrapping a 10-character
  value
- **THEN** it returns the value's first 2 characters followed by
  `"*******"`

### Requirement: A value converts to a numeric type using that type's own Java parser, yielding empty rather than throwing on failure
`req~property-value-access.numeric-conversion~1`

Each numeric accessor delegates to a specific `java.lang`/`java.math`
parser on `asString()`: `asBigDecimal()` to the `BigDecimal(String)`
constructor, `asDouble()` to `Double.parseDouble`, `asInteger()` to
`Integer.parseInt` and `asLong()` to `Long.parseLong`. When that parser
throws, the accessor returns `Optional.empty()` instead of letting the
exception propagate.

#### Scenario: BigDecimal conversion

- **WHEN** `asBigDecimal()` is called on text parseable by
  `new BigDecimal(String)`
- **THEN** `Optional.of` the parsed `BigDecimal` is returned

#### Scenario: Double conversion

- **WHEN** `asDouble()` is called on text parseable by
  `Double.parseDouble`
- **THEN** `Optional.of` the parsed `Double` is returned

#### Scenario: Integer conversion

- **WHEN** `asInteger()` is called on text parseable by `Integer.parseInt`
- **THEN** `Optional.of` the parsed `Integer` is returned

#### Scenario: Long conversion

- **WHEN** `asLong()` is called on text parseable by `Long.parseLong`
- **THEN** `Optional.of` the parsed `Long` is returned

#### Scenario: Conversion failure is swallowed

- **WHEN** any of the four numeric accessors is called on text its
  underlying parser rejects
- **THEN** `Optional.empty()` is returned
- **AND** no exception propagates to the caller
