---
type: decision
title: A real module descriptor, not an automatic module name
updated: 2026-09-07
status: accepted
origin: []
---

## Context

`tibbots/bots` cut itself into five named JPMS modules
(`../../../../tibbots/bots/docs/project/decisions/2026-09-07-the-aggregator-is-what-carries-the-modules.md`)
and needs a dependency to `requires` this library from a named module. A named module can only
`requires` another named module — an automatic module (one derived from a plain jar with no
descriptor) works there too, but its name is derived from the jar filename, not chosen.

Checked directly against the published artifact
(`https://jitpack.io/com/github/zrdj/java-properties/0.2.1/java-properties-0.2.1.jar`): no
`module-info.class`, no `Automatic-Module-Name` manifest entry. Absent both, the JPMS filename
algorithm turns `java-properties-0.2.1.jar` into the automatic module name `java.properties` — a
name in the `java.*` namespace the JDK reserves for platform modules.

That name is not rejected. Measured on 2026-09-07 in `tibbots/bots` on JDK 25: a named module's
`requires java.properties;` compiles, the reactor builds all five modules, and the packaged jar
starts and shuts down cleanly. Maven warns, but only with the generic automodule warning that the
same build already earns from `inject-1.0.0.jar` and `javax.inject-1.jar`. The reason nothing
breaks is narrower than it looks: that consumer packages into a single jar and starts from the
classpath, so the runtime module system never resolves the name.

## Decision

**Ship a real `module-info.java`, module name `com.github.zrdj.java.properties`, matching the root
package.** No automatic-module-name fallback: the reserved namespace collision does not go away by
picking a manifest entry instead of a real descriptor, since `Automatic-Module-Name` would need the
same non-`java.*` value anyway, and a real descriptor additionally states the module's dependency
(`requires org.slf4j`) and its public surface explicitly instead of leaving every package readable.

Exported packages are the four that exist, all of them public API, none held back:

- `com.github.zrdj.java.properties` — `ApplicationProperty`, `ApplicationPropertyStore`,
  `ApplicationPropertyValue`
- `com.github.zrdj.java.properties.error` — `MissingApplicationPropertyException`
- `com.github.zrdj.java.properties.naming` — the `ChangeDelimiterProperty` family
- `com.github.zrdj.java.properties.store` — `ComposedApplicationPropertyStore`,
  `PropertiesFileStore`, `RetryStore`, `SystemEnvironmentStore`, `SystemPropertyStore`

## Rationale

- **The trigger is a latent failure, not a current one.** A consumer can use this library from a
  named module today; what it cannot do is choose the name it uses. The derived name lands in the
  namespace the JDK reserves for itself, and the only reason that costs nothing today is that the
  consumer's runtime never sees it. The first module-path start, `jlink` image, or JDK that
  tightens the check turns a green build red — and no test in either repo would have caught it
  beforehand, because the descriptor is the only place the name appears.
- **A descriptor is also documentation.** `requires java.properties;` in a consumer reads as a
  platform module. `requires com.github.zrdj.java.properties;` reads as what it is.
- **Every package here is public API, so every package is exported.** No package holds a class
  without a `public` modifier — checked across the source tree, not assumed. There is nothing
  internal to keep unexported.
- **`requires org.slf4j`, not `requires transitive`.** `Logger` only appears as a constructor
  parameter type on internal implementations (e.g. `ComposedApplicationPropertyStore`'s
  `Function<Class<?>, Logger> logFactory`), never on a type a caller must read to use the public
  return type of a public method the way `tibbots/bots`' `HttpActor.Command.RegisterGet` forces
  `io.javalin` onto its callers. A consumer that never logs through this library's `Logger`
  parameter does not need to read `org.slf4j` itself.

## Consequences

- `pom.xml`'s `<version>` moves from `0.2.0` to `0.3.0`, catching it up to the already-tagged
  `0.2.1` and going one further because this is a source-incompatible-for-JPMS-consumers change,
  not a patch — see `../research/2026-09-01-readme-vs-code-drift.md` for the `0.2.0`/`0.2.1` drift
  this also closes.
- `maven.compiler.source`/`target` stay at `11`: JPMS exists since Java 9, and
  `maven-compiler-plugin` 3.15.0 auto-detects `module-info.java` and compiles it under `source`/
  `target` 11 without needing `<release>`. No `pom.xml` build configuration change beyond the
  version bump.
- Classpath consumers (an unnamed module, or a plain `<dependency>` with no `module-info.java` of
  its own) are unaffected: a modular jar keeps working unchanged when read from the classpath.
- `slf4j-api` must keep publishing a module name of `org.slf4j` (currently an
  `Automatic-Module-Name` manifest entry) for this descriptor's `requires org.slf4j` to resolve —
  true of the `2.0.18` pinned in `pom.xml` and every 1.8+/2.x release.
- Consumers already on `0.2.1` keep working. This release removes a latent problem, it does not
  fix a broken state — nothing is urgent about adopting it.
