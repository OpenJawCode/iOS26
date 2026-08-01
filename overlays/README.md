# overlays/

System RRO overlay projects. `systemui/` themes SystemUI resources (status bar, QS remnants); `framework/` covers framework-level resources (fonts, colors). Resources flow from `libs/design` tokens at build time — no hand-duplicated values. Installed by the Magisk module (ADR-0008).

Owner: provisioning-owner (with design-owner for token sync). Verified Tier 3 on device.
