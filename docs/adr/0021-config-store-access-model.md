# ADR-0021: Config store access model — split store + SELinux policy

- Status: Accepted (amends ADR-0006 — location & access model only; the store's design stands)
- Date: 2026-08-02
- Related: ADR-0006, ADR-0019; spike findings R7

## Context

ADR-0006 placed the config store under `/data/adb/ios26/`. The CC spike (R7) proved on-device that this path is **inaccessible to apps**:

- `/data/adb` is mode 0700 root-only (Magisk's deliberate protection) — DAC blocks `untrusted_app`.
- SELinux labels it `adb_data_file` — `untrusted_app` has no access, and `magiskpolicy --live` did not lift it (verified empirically).
- `/data/local/tmp` (`shell_data_file`) IS accessible to apps (verified via `run-as`) — but is not a production location.

The architecture needs: hooks (SystemUI process, `platform_app` domain) and the launcher/companion (`untrusted_app`) to share config and events through files, with zero process coupling (ADR-0019).

## Decision

Split the store into two zones under a Magisk-managed root:

```
/data/adb/ios26/
├── system/      # SYSTEM STORE — root/system-only state
│   ├── hooks/   #   hook config, lspd state, provisioning records
│   └── logs/    #   crash/observability records (ADR-0018)
└── shared/      # SHARED STORE — cross-component config + events
    ├── config/  #   schema-validated config tree (ADR-0006)
    └── events/  #   event bus (ADR-0019, ephemeral tmp+rename)
```

- **System store:** mode 0700, `adb_data_file` — root/system only. A security win: hook/provisioning state is not app-readable.
- **Shared store:** dedicated SELinux context `u:object_r:ios26_store:s0`, DAC 0777/0666, enforced by a Magisk module `sepolicy.rule` (Phase 4) granting: `untrusted_app`, `platform_app`, `system_app`, `system_server`, `shell` → read/write/search on `ios26_store`.
- **Events** live under `shared/events/`; writers use atomic tmp+rename; readers may consume/delete (debounced, ADR-0019).
- The `libs/config` public API is unchanged (paths are implementation detail behind the deep module, ADR-0006).
- Dev/provisioning mirrors the policy via `magiskpolicy --live` (documented in `tools/scripts/`); AVD/Tier-2 tests use a configurable temp store path (no SELinux on AVD).

## Consequences

- Magisk module becomes load-bearing earlier: it must ship the `sepolicy.rule`, create both zones at boot, and set ownership (Phase 4; dev scripts fill in until then).
- App-readable config is explicitly scoped to the shared zone — the system zone stays private (better than the original single-store design).
- The event bus remains the zero-coupling channel (ADR-0019 unchanged).
- ADR-0006's "/data/adb/ios26" location paragraph is superseded; everything else (schema-first, atomicity, deep module, migrations) stands.
