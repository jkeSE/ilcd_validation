#!/bin/bash

PROJECT_BASEDIR=$1
if [ -z $PROJECT_BASEDIR ]; then
  echo "No PROJECT_BASEDIR given. Must point to Maven project."
  exit 1
fi

APP_BUNDLE=$2
if [ -z $APP_BUNDLE ]; then
  echo "No APP_BUNDLE given. Must point to macOS application bundle."
  exit 1
fi

if [ -z $CODESIGN_ID ]; then
  echo "No CODESIGN_ID given. Configure Code Signing ID via environment."
  exit 1
fi

# Function to check if a file is already signed
is_signed() {
    codesign --verify "$1" 2>/dev/null
}

# Function to sign file only if not already signed
sign_if_needed() {
    local file="$1"
    
    # Skip the main executable - it will be signed with the app bundle
    if [[ "$file" == */Contents/MacOS/* ]]; then
        echo "Skipping main executable (will be signed with bundle): $file"
        return 0
    fi
    
    if is_signed "$file"; then
        echo "✓ Already signed: $file"
        return 0
    else
        echo "→ Signing: $file"
        if codesign \
          -s "$CODESIGN_ID" \
          --timestamp \
          --options runtime \
          --entitlements "$PROJECT_BASEDIR/signing/entitlements.plist" \
          -vvvv "$file"; then
            echo "✓ Successfully signed: $file"
            return 0
        else
            echo "✗ Failed to sign: $file"
            return 1
        fi
    fi
}

rm -rf $APP_BUNDLE/Contents/Eclipse/p2/org.eclipse.equinox.p2.core/cache

echo "Starting signing process..."

# Process individual files (preserving existing signatures)
while IFS= read -r -d '' file; do
    sign_if_needed "$file" || exit 1
done < <(find $APP_BUNDLE -type f \( -name '*.dylib' -o -name '*.so' \) -print0)

while IFS= read -r -d '' file; do
    sign_if_needed "$file" || exit 1
done < <(find $APP_BUNDLE -perm +111 -type f -print0)

# Always re-sign the app bundle (this is common practice)
echo "→ Signing main app bundle (with --force to replace existing signature)..."
if codesign -s "$CODESIGN_ID" \
  --force \
  --timestamp \
  --options runtime \
  --entitlements "$PROJECT_BASEDIR/signing/entitlements.plist" \
  -vvvv \
  "$APP_BUNDLE"; then
    echo "✓ Successfully signed main app bundle"
else
    echo "✗ Failed to sign main app bundle"
    exit 1
fi

echo "Signing completed successfully"