#!/usr/bin/env sh
set -e

# Log helper
log() { echo "[entrypoint] $*"; }

# Decodificador
DEC="base64 -d"
command -v base64 >/dev/null 2>&1 || DEC="openssl base64 -d -A"
log "decoder: $DEC"

mkdir -p /run/secrets/iam /run/secrets/firebase

# IAM private key
if [ -n "$IAM_PRIVATE_KEY_B64" ]; then
  echo "$IAM_PRIVATE_KEY_B64" | $DEC > /run/secrets/iam/iam_private.pem || { log "decode private FAIL"; exit 1; }
  chmod 600 /run/secrets/iam/iam_private.pem
  log "private key written: $(ls -l /run/secrets/iam/iam_private.pem)"
  head -n 2 /run/secrets/iam/iam_private.pem || true
else
  log "IAM_PRIVATE_KEY_B64 is EMPTY"
fi

# IAM public key
if [ -n "$IAM_PUBLIC_KEY_B64" ]; then
  echo "$IAM_PUBLIC_KEY_B64" | $DEC > /run/secrets/iam/iam_public.pem || { log "decode public FAIL"; exit 1; }
  chmod 644 /run/secrets/iam/iam_public.pem
  log "public key written:  $(ls -l /run/secrets/iam/iam_public.pem)"
  head -n 2 /run/secrets/iam/iam_public.pem || true
else
  log "IAM_PUBLIC_KEY_B64 is EMPTY"
fi

# Firebase admin JSON
if [ -n "$FIREBASE_ADMIN_JSON_B64" ]; then
  echo "$FIREBASE_ADMIN_JSON_B64" | $DEC > /run/secrets/firebase/firebase-admin.json || { log "decode firebase FAIL"; exit 1; }
  chmod 600 /run/secrets/firebase/firebase-admin.json
  log "firebase json written: $(ls -l /run/secrets/firebase/firebase-admin.json)"
  head -n 2 /run/secrets/firebase/firebase-admin.json || true
else
  log "FIREBASE_ADMIN_JSON_B64 is EMPTY"
fi

exec java -jar /app/app.jar
