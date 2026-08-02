# hooks/control-center

CC surface hook (modern libxposed API, ADR-0032). Targets SystemUI only; flag-gated (default off); all-or-nothing rollback (ADR-0033). Entry: `ControlCenterModule` via `META-INF/xposed/java_init.list`, `module.prop` targetApiVersion=101.
