# Phase 0 — Device Setup (Edge 20 → dev machine)

This machine is the Tier 3 device lab. Three connection modes, best first:

## Mode A — Tailscale mesh (wireless, works from ANYWHERE, over 4G/5G)

Zero-config mesh VPN (WireGuard-based). Phone + machine join one private tailnet; adb reaches the phone over the internet like it's plugged in. **This is the primary lab connection.**

**Machine side (done):** Tailscale 1.98.10 installed (`/usr/local/bin/tailscale{,d}`).

**Phone side (one-time, ~3 min):**
1. Play Store → install **Tailscale** → open → sign in (any account — it becomes your tailnet owner).
2. Settings → About phone → tap **Build number** 7× → Developer options enabled.
3. Developer options → enable **USB debugging** and **Wireless debugging** (tap it, then **Pair device with pairing code** — leave this screen open when pairing).
4. Tell the dev session "tailscale up" → click the auth link it gives you (approves the machine into your tailnet) → phone app shows Connected.
5. Read back the **pairing code + IP:port** from the Pair screen → dev session pairs and connects.

```bash
# dev machine, once both ends are on the tailnet:
adb pair <phone-tailscale-ip>:<pairing-port>   # enter the 6-digit code
adb connect <phone-tailscale-ip>:<connect-port>
```

> Once paired, reconnects are just `adb connect`. The phone stays fully usable — adb is only a debug channel; you'll see screen activity when the lab drives it.

## Mode B — Wireless debugging (same-WiFi only, no Tailscale)

Steps 2–3 above, then when both ends are on the same LAN:
`adb pair <phone-lan-ip>:<pairing-port>` → `adb connect <phone-lan-ip>:<connect-port>`.

## Mode C — USB cable (fallback)

1. Plug in, choose **File transfer (MTP)** in the USB notification.
2. Accept **Allow USB debugging?** → Always allow.
3. `adb devices` — expect `<serial>  device`.

If `unauthorized` → unlock + tap Allow. If `no permissions` → `sudo adb kill-server && adb start-server`.

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
