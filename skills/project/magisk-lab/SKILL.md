---
name: project-magisk-lab
description: "PROJECT-SPECIFIC (iOS26). Magisk provisioning and SELinux policy on the ARM64 lab + rooted Edge 20: magiskpolicy --live grants, module install/removal, boot-loop protection, permission grants, lab quirks (qemu aapt2, disk hygiene)."
license: GPL-3.0
project: iOS26
source: project-specific (DEVICE_SETUP.md, BUILD.md, ADR-0021)
---

# Magisk Lab Operations (ARM64 host + Edge 20)

## Device facts

- Edge 20, stock Android 13 (API 33), Magisk 30.7, LSPosed = JingMatrix fork (zygisk_lsposed).
- Wireless lab: adb via Tailscale; root auth through `/data/misc/adb/adb_keys`.
- NEVER reboot or restart core SystemUI without explicit user approval (daily driver).
  Reboot wipes adb authorization.

## Permission grants for the launcher (validation builds, debuggable)

- `pm grant dev.ios26.launcher android.permission.WRITE_SECURE_SETTINGS` (airplane/mobile data)
- `appops set dev.ios26.launcher android:write_settings allow` (brightness/rotation —
  WRITE_SETTINGS is role-managed; `pm grant` FAILS for it)
- `appops set dev.ios26.launcher SYSTEM_ALERT_WINDOW allow` + `pm grant SYSTEM_ALERT_WINDOW`
- `pm grant ... CAMERA` (flashlight), `BLUETOOTH_CONNECT`, `MEDIA_CONTENT_CONTROL`
  (NOT changeable — guard in code instead)
- VIBRATE is install-time; missing it crashes HapticEngine → always guard haptics.

## SELinux (magiskpolicy --live)

- `platform_app → shell_data_file` (SystemUI event writes): allow file { read write open
  create rename unlink } + dir { read write open search create rename add_name }.
- `untrusted_app → shell_data_file` (launcher event cleanup): same shape; live-granted for
  validation; production = Phase-4 sepolicy (ADR-0021). Event DELETION (consume) is denied
  without it — processing still works.
- Verify denials with `dmesg | grep avc` and `ls -laZ`.

## Reboot protocol (learned 2026-08-09)

- A reboot while the phone is in a stuck-shade/keyguard state can leave the phone
  unreachable for 45+ min (tailscaled sending, rx 0; port 5555 closed) — likely a
  degraded/slow boot; Magisk bootloop protection disables modules if it truly loops
  (no wipe possible). Budget physical recovery (press-and-hold power) when rebooting
  overnight; prefer daytime reboots.
- Post-reboot: adbd listens on 5555 automatically (persist.adb.tcp.port), but the
  tailnet can lag; wait for `tailscale status` to show the peer WITHOUT "offline"
  before connecting.

## Lab quirks (ARM64 box)

- aapt2 is x86_64 → runs via qemu-user binfmt; never "fix" it (BUILD.md).
- Disk fills fast: clean `~/.gradle/caches/build-cache-1`, `transforms`, `daemon`,
  journal vacuum (`journalctl --vacuum-time=2d`), `/tmp/opencode`.
- Always write scripts to /tmp and run; never heredoc with `!`.
