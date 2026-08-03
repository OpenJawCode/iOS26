---
name: project-baseline-profiles
description: "PROJECT-SPECIFIC (iOS26). Baseline Profiles + Macrobenchmark for THIS repo (benchmarks/macrobenchmark + launcher/baseline-prof modules exist). Use before perf gates and releases: generate baseline profile from the launcher start + CC open/close flows, wire Macrobenchmark scenarios, compare against the 8.33ms budget (ADR-0030). General Compose-perf depth comes from vendored skydoves/chrisbanes skills."
license: GPL-3.0
project: iOS26
source: project-specific (ADRs 0029/0030, docs/phase3/CONTROL_CENTER_PERFORMANCE.md)
---

# Baseline Profiles + Macrobenchmark (this repo)

1. Profile source: `launcher/baseline-prof` — include the critical flows: LauncherActivity
   cold start, ControlCenterSurface open (event → spring entrance), drag-dismiss, slider
   drag, media card render.
2. Macrobenchmark: `benchmarks/macrobenchmark` — add `StartupBenchmark` + `CcOpenCloseBenchmark`
   scenarios against `dev.ios26.launcher`; measure frame time (50/90/95th) + jank % and
   compare with the ADR-0030 8.33ms budget and the 3.1 gallery baseline (25ms median).
3. Rule: profile regeneration + benchmark run are required before any 3.2/3.3 acceptance;
   `./gradlew :benchmarks:macrobenchmark:connectedDebugAndroidTest` on the wired device.
4. Compose-compiler diagnostics (composables.txt / stability reports) via the vendored
   chrisbanes `compose-stability-diagnostics` and skydoves audit skills.
