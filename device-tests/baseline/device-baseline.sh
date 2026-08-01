#!/bin/sh
# ============================================================
# Device baseline collector — Phase 0 (Tier 3 harness seed)
# Runs ON the rooted Edge 20. Prints PASS/FAIL lines.
# Usage (from a machine with adb):
#   adb push device-baseline.sh /data/local/tmp/
#   adb shell su -c 'sh /data/local/tmp/device-baseline.sh'
# Output: copy into docs/phase0/baseline/YYYY-MM-DD.txt
# ============================================================
set -u

PASS=0
FAIL=0

check() { # check <name> <status>
  if [ "$2" = "PASS" ]; then PASS=$((PASS+1)); else FAIL=$((FAIL+1)); fi
  echo "$2  $1"
}

# --- Device identity ---
MODEL=$(getprop ro.product.model 2>/dev/null)
RELEASE=$(getprop ro.build.version.release)
SDK=$(getprop ro.build.version.sdk)
PATCH=$(getprop ro.build.version.security_patch)
FINGERPRINT=$(getprop ro.build.fingerprint)

echo "MODEL=$MODEL RELEASE=$RELEASE SDK=$SDK PATCH=$PATCH"
echo "FINGERPRINT=$FINGERPRINT"

# --- Root / Magisk ---
if su -c 'magisk -v' 2>/dev/null; then
  MAGISK_VER=$(su -c 'magisk -v' 2>/dev/null)
  check "magisk $MAGISK_VER" PASS
else
  check "magisk -v (root access)" FAIL
fi

# --- Zygisk ---
if su -c 'magisk --zygisk' 2>/dev/null | grep -qi 'true\|enabled'; then
  check "zygisk enabled" PASS
else
  check "zygisk enabled" FAIL
fi

# --- Magisk modules present ---
MODULES=$(su -c 'ls /data/adb/modules 2>/dev/null' | tr '\n' ' ')
echo "MODULES=$MODULES"

# --- LSPosed ---
LSPD=$(su -c 'ls /data/adb/lspd 2>/dev/null')
if [ -n "$LSPD" ]; then
  check "lspd dir present (/data/adb/lspd)" PASS
else
  check "lspd dir present (/data/adb/lspd)" FAIL
fi
LSPD_VER=$(su -c 'getprop lspd.version 2>/dev/null')
echo "LSPD_VERSION=$LSPD_VER"

# --- SystemUI identity (hook target) ---
SYS_VER=$(dumpsys package com.android.systemui 2>/dev/null | grep -m1 versionName)
echo "SYSTEMUI $SYS_VER"

# --- Firmware freeze sanity ---
if [ "$SDK" = "33" ]; then
  check "android 13 (sdk 33)" PASS
else
  check "android 13 (sdk 33, got sdk=$SDK)" FAIL
fi

# --- Config store path (expected future home) ---
if su -c 'ls -ld /data/adb/ios26 2>/dev/null'; then
  check "config store dir /data/adb/ios26" PASS
else
  echo "INFO  /data/adb/ios26 not present (created by Magisk module at install time)"
fi

echo "TOTAL PASS=$PASS FAIL=$FAIL"
[ "$FAIL" -eq 0 ] || exit 1
