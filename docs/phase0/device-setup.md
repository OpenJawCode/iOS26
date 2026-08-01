# Phase 0 — Device Setup (Edge 20 → dev machine)

This machine is the Tier 3 device lab. One-time setup + daily connect instructions.

## One-time (already done on this machine)

- `adb` / `fastboot` installed (Ubuntu packages, adb 34.0.4, aarch64)
- udev rules: `/etc/udev/rules.d/51-android.rules` (Motorola vendor `22b8`, Google `18d1`)
- JDK 21 (openjdk-21.0.11) — for survey tooling (jadx/apktool) and Phase 1

## On the phone (one-time)

1. Settings → About phone → tap **Build number** 7× → Developer options enabled.
2. Developer options → enable **USB debugging** (leave "Stay awake" on while connected).
3. (Optional) USB debugging → **Wireless debugging** if you prefer adb-over-WiFi.

## Connect (every session)

1. Plug in via USB. On the phone's USB notification, choose **File transfer (MTP)** — USB-debugging visibility is most reliable in that mode on Motorola.
2. If a dialog appears: **Allow USB debugging?** → check "Always allow" → Allow. (Authorizes this machine's RSA key.)
3. On this machine:

```bash
adb devices          # expect: <serial>  device
```

If it shows `unauthorized` → unlock the phone and tap Allow. If it shows `no permissions` → `sudo adb kill-server && adb start-server`.

## Baseline collection

```bash
adb push device-tests/baseline/device-baseline.sh /data/local/tmp/
adb shell su -c 'sh /data/local/tmp/device-baseline.sh' | tee docs/phase0/baseline/$(date +%F).txt
```

Save output as `docs/phase0/baseline/<date>.txt` (committed — it pins the reference device state).

## Convenience

- `adb shell su -c '...'` — root shell for one-liners (system app installs, config store, lspd).
- `adb pull /data/adb/...` / `adb push` — moving files to/from the config store.
- `adb logcat` with `-s` filters for hook debugging in Phase 3.
- Wireless alternative: `adb pair <ip>:<port>` (code shown in Wireless debugging), then `adb connect <ip>:<port>`.
