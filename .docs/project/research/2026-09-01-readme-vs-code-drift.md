---
type: research
title: README vs. code drift
updated: 2026-09-01
---

Found while deriving the as-is spec (see `../../specs/`).

## Version mismatch

`README.md`'s Maven snippet pins `<version>0.2.1</version>`; `pom.xml`
itself declares `<version>0.2.0</version>`. Same pattern as
`java-identifiers`: `.github/workflows/release.yml` only rewrites
`README.md`'s `<version>` elements on release, never `pom.xml`'s own
`<version>`.

## Misspelled class name in the usage example

The "Configure it the way you like it" usage example in `README.md`
instantiates `ComposedApplcationPropertyStore` (missing the `i` in
"Application"). The actual class is
`com.github.zrdj.java.properties.store.ComposedApplicationPropertyStore`.
As written, the README's example does not compile.
