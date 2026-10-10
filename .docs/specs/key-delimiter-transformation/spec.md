---
type: spec
title: Key Delimiter Transformation
updated: 2026-09-01
---

## Purpose

Lets a caller view an existing `ApplicationProperty` under a different key
delimiter — for example to also look up `app.name.value` for a property
whose canonical key is `app-name-value` — by wrapping it in one of six
`ChangeDelimiterProperty` subclasses instead of building a new key by hand.

## Requirements

### Requirement: A property's key delimiter can be substituted in either direction between dash, dot and underscore
`req~key-delimiter-transformation.delimiter-substitution~1`

Each of the six `naming` classes wraps an `ApplicationProperty` and
overrides `key()` to run the wrapped property's key through
`String.replaceAll`, substituting every occurrence of one delimiter for
another (not just the first): `ChangeDashToDotProperty` (`-` to `.`),
`ChangeDashToUnderscoreProperty` (`-` to `_`), `ChangeDotToDashProperty`
(`.` to `-`), `ChangeDotToUnderscoreProperty` (`.` to `_`),
`ChangeUnderscoreToDashProperty` (`_` to `-`) and
`ChangeUnderscoreToDotProperty` (`_` to `.`). All other `ApplicationProperty`
behavior (`isSecret()`, `value()`, `exception()`) is inherited unchanged
from the wrapped property.

#### Scenario: Dash to dot

- **WHEN** a `ChangeDashToDotProperty` wraps a property whose key is
  `"app-name-value"`
- **THEN** its `key()` returns `"app.name.value"`

#### Scenario: Dash to underscore

- **WHEN** a `ChangeDashToUnderscoreProperty` wraps a property whose key
  is `"app-name-value"`
- **THEN** its `key()` returns `"app_name_value"`

#### Scenario: Dot to dash

- **WHEN** a `ChangeDotToDashProperty` wraps a property whose key is
  `"app.name.value"`
- **THEN** its `key()` returns `"app-name-value"`

#### Scenario: Dot to underscore

- **WHEN** a `ChangeDotToUnderscoreProperty` wraps a property whose key is
  `"app.name.value"`
- **THEN** its `key()` returns `"app_name_value"`

#### Scenario: Underscore to dash

- **WHEN** a `ChangeUnderscoreToDashProperty` wraps a property whose key
  is `"app_name_value"`
- **THEN** its `key()` returns `"app-name-value"`

#### Scenario: Underscore to dot

- **WHEN** a `ChangeUnderscoreToDotProperty` wraps a property whose key is
  `"app_name_value"`
- **THEN** its `key()` returns `"app.name.value"`
