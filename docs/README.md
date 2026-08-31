---
type: index
title: java-properties — Knowledge Layer
lang: en
updated: 2026-08-31
---

# java-properties — Knowledge Layer

Application property store abstraction — reads configuration from system properties,
environment variables and properties files through a common `ApplicationPropertyStore`
interface, composable and decoratable (e.g. retry, name mapping), with typed access via
an `ApplicationProperty` enum.

Repo-specific knowledge. What concerns more than this repo lives in the knowledge layer
of the WS root (`~/workspaces/personal/docs/`).

## Folders

- `project/decisions/` — why things are the way they are (ADRs)
- `project/worklog/` — work logs, one file per day
- `project/research/` — self-collected material
- `project/sources/` — material delivered by others
- `wayfinding/` — undertakings whose path is not yet settled
- `changes/` — ongoing undertakings whose path is settled
- `archive/` — completed changes
- `specs/` — current state per capability

## Entry points

- `pom.xml` — coordinates (`com.github.zrdj:java-properties`), Java 11, slf4j dependency
- `src/main/java/com/github/zrdj/java/properties/ApplicationPropertyStore.java` — the core store interface (`or`, `decorate`, `get`)
- `src/main/java/com/github/zrdj/java/properties/store/PropertiesFileStore.java` — one of the concrete store implementations
- `src/main/java/com/github/zrdj/java/properties/ApplicationProperty.java` — typed property key contract
- `README.md` (repo root) — store composition and typed-enum usage examples

This repo does not (yet) have its own `CLAUDE.md` — working rules apply from
`zrdj/CLAUDE.md` and the provider levels above it.
