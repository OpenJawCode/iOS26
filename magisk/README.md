# magisk/

Magisk module packaging (ADR-0008). `ios26-stack/` = the one-flash provisioning module: systemless priv-app install, system RROs, config-store bootstrap (`/data/adb/ios26/`), optional hook provisioning, `update.json` update flow. Release builds land in `ios26-stack/release/` (gitignored).

Owner: provisioning-owner. Scripts are POSIX sh, idempotent, PASS/FAIL output (conventions §6, testing Tier 3).
