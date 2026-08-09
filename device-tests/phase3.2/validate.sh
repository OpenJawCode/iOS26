#!/usr/bin/env bash
# Phase 3.2 device validation runner (Tier 3). Idempotent; safe radio tests only.
# Usage: ./validate.sh <adb-host:port>   (e.g. 100.113.213.67:5555)
set -u
ADB="adb -s ${1:-100.113.213.67:5555}"
EV=/data/local/tmp/ios26/shared/events
SHOT=/tmp/opencode/cc-validate
mkdir -p "$SHOT"
PASS=0; FAIL=0
ok()   { echo "  PASS: $1"; PASS=$((PASS+1)); }
bad()  { echo "  FAIL: $1"; FAIL=$((FAIL+1)); }

$ADB connect "$1" >/dev/null 2>&1 || true
$ADB shell input keyevent 224 >/dev/null 2>&1; sleep 1
$ADB shell "svc power stayon true" >/dev/null 2>&1

echo "== 0. baseline =="
$ADB shell "pidof dev.ios26.launcher" >/dev/null 2>&1 || $ADB shell "am start -n dev.ios26.launcher/.LauncherActivity" >/dev/null 2>&1
sleep 4
$ADB shell "dumpsys gfxinfo dev.ios26.launcher reset" >/dev/null 2>&1

echo "== 1. open gesture (hook event -> overlay) =="
$ADB shell "su -c 'rm -f $EV/cc-open.json; touch $EV/cc-open.json'" >/dev/null 2>&1
sleep 2
W=$($ADB shell "dumpsys window windows 2>/dev/null" | grep -c "ty=APPLICATION_OVERLAY.*blurBehindRadius=30" 2>/dev/null | tr -d '\r')
[ "$W" -ge 1 ] && ok "overlay window attached (blur-behind 30)" || bad "overlay window missing"
$ADB exec-out screencap -p > "$SHOT/open.png"
python3 /home/ubuntu/projects/Android/iOS26/tools/cc-pixels.py "$SHOT/open.png"

echo "== 2. close: tap outside =="
$ADB shell "input tap 100 1500" >/dev/null 2>&1; sleep 1
$ADB shell "dumpsys window windows 2>/dev/null" | grep -q "ty=APPLICATION_OVERLAY.*blurBehindRadius=30" \
  && bad "tap-outside did not dismiss" || ok "tap-outside dismissed"

echo "== 3. reopen, drag-close slow (settle back) =="
$ADB shell "su -c 'touch $EV/cc-open.json'" >/dev/null 2>&1; sleep 2
$ADB shell "input swipe 700 600 700 900 400" >/dev/null 2>&1; sleep 1   # slow drag on panel
$ADB shell "dumpsys window windows 2>/dev/null" | grep -q "ty=APPLICATION_OVERLAY.*blurBehindRadius=30" \
  && ok "slow drag settled back (still open)" || bad "slow drag dismissed (should settle)"
$ADB exec-out screencap -p > "$SHOT/slow-drag.png"

echo "== 4. velocity fling dismiss =="
$ADB shell "input swipe 900 400 900 1400 80" >/dev/null 2>&1; sleep 1
$ADB shell "dumpsys window windows 2>/dev/null" | grep -q "ty=APPLICATION_OVERLAY.*blurBehindRadius=30" \
  && bad "fling did not dismiss" || ok "fling dismissed (velocity-aware)"

echo "== 5. interruptibility (open, then fling mid-entrance) =="
$ADB shell "su -c 'touch $EV/cc-open.json'" >/dev/null 2>&1
sleep 0.3
$ADB shell "input swipe 900 300 900 1500 60" >/dev/null 2>&1; sleep 1
$ADB shell "dumpsys window windows 2>/dev/null" | grep -q "ty=APPLICATION_OVERLAY.*blurBehindRadius=30" \
  && bad "interrupted entrance still open (fling should win)" || ok "interrupted entrance dismissed"
$ADB shell "pidof dev.ios26.launcher" >/dev/null 2>&1 && ok "process alive after interruption" || bad "process died"

echo "== 6. toggles (safe: BT, flashlight, rotation, focus) =="
$ADB shell "su -c 'touch $EV/cc-open.json'" >/dev/null 2>&1; sleep 2
BT0=$($ADB shell "settings get global bluetooth_on" 2>/dev/null | tr -d '\r')
$ADB shell "input tap 488 285" >/dev/null 2>&1; sleep 2   # Bluetooth tile (calibrated)
BT1=$($ADB shell "settings get global bluetooth_on" 2>/dev/null | tr -d '\r')
[ "$BT0" != "$BT1" ] && ok "bluetooth toggled ($BT0 -> $BT1)" || bad "bluetooth unchanged"
$ADB exec-out screencap -p > "$SHOT/toggles.png"

echo "== 7. brightness slider =="
B0=$($ADB shell "settings get system screen_brightness" | tr -d '\r')
B0v=$($ADB shell "settings get system screen_brightness" | tr -d '\r')
$ADB shell "input swipe 900 835 300 835 300" >/dev/null 2>&1; sleep 1   # brightness slider
B1v=$($ADB shell "settings get system screen_brightness" | tr -d '\r')
[ "$B0v" != "$B1v" ] && ok "brightness changed ($B0v -> $B1v)" || bad "brightness unchanged"
B1=$($ADB shell "settings get system screen_brightness" | tr -d '\r')
[ "$B0" != "$B1" ] && ok "brightness changed ($B0 -> $B1)" || bad "brightness unchanged"

echo "== 8. volume slider =="
V0=$($ADB shell "dumpsys audio 2>/dev/null" | grep -oE "STREAM_MUSIC[^=]*=[0-9]+" | head -1 | grep -oE "[0-9]+$")
V0v=$($ADB shell "cmd audio get-stream-volume STREAM_MUSIC 2>/dev/null" | tr -d '\r')
$ADB shell "input swipe 900 1241 300 1241 300" >/dev/null 2>&1; sleep 1   # volume slider
V1v=$($ADB shell "cmd audio get-stream-volume STREAM_MUSIC 2>/dev/null" | tr -d '\r')
[ "$V0v" != "$V1v" ] && ok "volume changed ($V0v -> $V1v)" || bad "volume unchanged"
V1=$($ADB shell "dumpsys audio 2>/dev/null" | grep -oE "STREAM_MUSIC[^=]*=[0-9]+" | head -1 | grep -oE "[0-9]+$")
[ "$V0" != "$V1" ] && ok "volume changed ($V0 -> $V1)" || bad "volume unchanged"

echo "== 8b. media session (active) =="
# Play a YouTube/Instagram video via intent to create an active media session
$ADB shell "am start -a android.intent.action.VIEW -d 'https://youtu.be/dQw4w9WgXcQ' -t 'text/plain'" >/dev/null 2>&1
sleep 5
$ADB exec-out screencap -p > "$SHOT/media-active.png"
$ADB shell "dumpsys media_session 2>/dev/null | grep -cE 'controller|PlaybackState' | head -1"
echo "(media check: see screenshot + dumpsys)"

echo "== 9. media card =="
$ADB exec-out screencap -p > "$SHOT/media.png"
python3 /home/ubuntu/projects/Android/iOS26/tools/cc-pixels.py "$SHOT/media.png" >/dev/null
$ADB shell "uiautomator dump /sdcard/ui.xml >/dev/null 2>&1; cat /sdcard/ui.xml 2>/dev/null" | grep -q "Now Playing" \
  && ok "media card present" || bad "media card not found in hierarchy"

echo "== 10. perf snapshot =="
$ADB shell "dumpsys gfxinfo dev.ios26.launcher" > "$SHOT/gfxinfo.txt" 2>&1
$ADB shell "dumpsys meminfo dev.ios26.launcher" > "$SHOT/meminfo.txt" 2>&1
grep -E "Total frames|Janky frames|50th|90th|95th" "$SHOT/gfxinfo.txt" | head -6
grep -E "TOTAL PSS" "$SHOT/meminfo.txt" | head -1

echo "== 11. crash check =="
C=$($ADB shell "logcat -d -b crash 2>/dev/null" | grep -c "Process: dev.ios26" | tr -d '\r')
[ "$C" -eq 0 ] && ok "no crashes in buffer" || bad "$C crashes in buffer"

echo "=============================================="
echo "RESULT: PASS=$PASS FAIL=$FAIL"
