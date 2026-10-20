#!/usr/bin/env bash
# ==============================================================================
# Spark Release Agent (self-hosted distribution and Obtainium feed)
# ==============================================================================
# Bumps the semantic version, runs unit tests, builds the signed release APK,
# writes the Obtainium version.json feed and a landing page into dist/<appSlug>/,
# and optionally uploads the result to a self-hosted server.
# Entry point: ./release-agent (wrapper) or ./scripts/release.sh.
# ==============================================================================

set -euo pipefail

# Color palette for the terminal output
C_RESET="\033[0m"
C_BOLD="\033[1m"
C_CYAN="\033[36m"
C_GREEN="\033[32m"
C_YELLOW="\033[33m"
C_RED="\033[31m"

SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do
    DIR="$(cd -P "$(dirname "$SOURCE")" && pwd)"
    SOURCE="$(readlink "$SOURCE")"
    [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE"
done
SCRIPT_DIR="$(cd -P "$(dirname "$SOURCE")" && pwd)"
if [[ -f "$SCRIPT_DIR/version.properties" ]]; then
    PROJECT_ROOT="$SCRIPT_DIR"
else
    PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
fi
VERSION_PROPS="$PROJECT_ROOT/version.properties"
DIST_DIR="$PROJECT_ROOT/dist"
APK_ROOT_DIR="$PROJECT_ROOT/apk"

# Load local environment overrides (server URL, token, signing) if present
if [[ -f "$PROJECT_ROOT/.env" ]]; then
    # shellcheck disable=SC1091
    source "$PROJECT_ROOT/.env"
fi

print_header() {
    echo -e "${C_CYAN}${C_BOLD}"
    echo "=================================================================="
    echo "                 Spark Release Agent"
    echo "           Self-hosted distribution and Obtainium feed"
    echo "=================================================================="
    echo -e "${C_RESET}"
}

usage() {
    print_header
    echo -e "${C_BOLD}USAGE:${C_RESET}"
    echo -e "  ./release-agent [patch|minor|major|<explicit_version>] [options]"
    echo -e "  ./scripts/release.sh [patch|minor|major|<explicit_version>] [options]"
    echo ""
    echo -e "${C_BOLD}ARGUMENTS:${C_RESET}"
    echo -e "  ${C_GREEN}patch${C_RESET}               Bump the patch number: 1.0.0 -> 1.0.1 (default)"
    echo -e "  ${C_GREEN}minor${C_RESET}               Bump the minor number: 1.0.0 -> 1.1.0"
    echo -e "  ${C_GREEN}major${C_RESET}               Bump the major number: 1.0.0 -> 2.0.0"
    echo -e "  ${C_GREEN}<X.Y.Z>${C_RESET}             Set an explicit version (for example 1.2.0)"
    echo ""
    echo -e "${C_BOLD}OPTIONS:${C_RESET}"
    echo -e "  ${C_YELLOW}-m, --notes \"...\"${C_RESET}    Release notes (changelog written to version.json)"
    echo -e "  ${C_YELLOW}--skip-tests${C_RESET}         Skip the unit tests before the build"
    echo -e "  ${C_YELLOW}--no-deploy${C_RESET}          Skip the remote upload, even if .env is configured"
    echo -e "  ${C_YELLOW}--dry-run${C_RESET}            Print the computed changes without building or writing files"
    echo -e "  ${C_YELLOW}-h, --help${C_RESET}           Show this help"
    echo ""
    echo -e "${C_BOLD}EXAMPLES:${C_RESET}"
    echo -e "  ./release-agent patch -m \"Fix contrast in dark mode\""
    echo -e "  ./release-agent minor -m \"Add the statistics screen\""
    echo -e "  ./release-agent 1.5.0 -m \"Milestone release\""
    echo ""
    exit 0
}

# Default values
BUMP_TYPE="patch"
RELEASE_NOTES=""
SKIP_TESTS=false
NO_DEPLOY=false
DRY_RUN=false

# Positional and flag parsing
while [[ $# -gt 0 ]]; do
    case "$1" in
        patch|minor|major)
            BUMP_TYPE="$1"
            shift
            ;;
        [0-9]*.[0-9]*.[0-9]*)
            BUMP_TYPE="$1"
            shift
            ;;
        -m|--notes)
            RELEASE_NOTES="${2:-}"
            shift 2
            ;;
        --skip-tests)
            SKIP_TESTS=true
            shift
            ;;
        --no-deploy)
            NO_DEPLOY=true
            shift
            ;;
        --dry-run)
            DRY_RUN=true
            shift
            ;;
        -h|--help)
            usage
            ;;
        *)
            if [[ -z "$RELEASE_NOTES" && "$1" != -* ]]; then
                RELEASE_NOTES="$1"
                shift
            else
                echo -e "${C_RED}Unrecognized option: $1${C_RESET}"
                usage
            fi
            ;;
    esac
done

print_header

# ------------------------------------------------------------------------------
# 1. Read the current version and compute the next one
# ------------------------------------------------------------------------------
if [[ ! -f "$VERSION_PROPS" ]]; then
    echo -e "${C_YELLOW}version.properties not found. Creating it with default values 1.0.0 (build 1)...${C_RESET}"
    cat <<EOF > "$VERSION_PROPS"
versionMajor=1
versionMinor=0
versionPatch=0
versionCode=1
versionName=1.0.0
EOF
fi

# Read existing properties
APP_NAME=$(grep -E '^appName=' "$VERSION_PROPS" | cut -d'=' -f2 | tr -d '[:space:]')
APP_NAME=${APP_NAME:-"App"}
APP_SLUG=$(grep -E '^appSlug=' "$VERSION_PROPS" | cut -d'=' -f2 | tr -d '[:space:]')
if [[ -z "$APP_SLUG" ]]; then
    APP_SLUG=$(echo "$APP_NAME" | tr '[:upper:]' '[:lower:]' | tr -cs 'a-z0-9' '-' | sed 's/^-//;s/-$//')
fi

CURR_MAJOR=$(grep -E '^versionMajor=' "$VERSION_PROPS" | cut -d'=' -f2 | tr -d '[:space:]')
CURR_MINOR=$(grep -E '^versionMinor=' "$VERSION_PROPS" | cut -d'=' -f2 | tr -d '[:space:]')
CURR_PATCH=$(grep -E '^versionPatch=' "$VERSION_PROPS" | cut -d'=' -f2 | tr -d '[:space:]')
CURR_CODE=$(grep -E '^versionCode=' "$VERSION_PROPS" | cut -d'=' -f2 | tr -d '[:space:]')
CURR_NAME=$(grep -E '^versionName=' "$VERSION_PROPS" | cut -d'=' -f2 | tr -d '[:space:]')

# Safety fallbacks when a value is empty
CURR_MAJOR=${CURR_MAJOR:-1}
CURR_MINOR=${CURR_MINOR:-0}
CURR_PATCH=${CURR_PATCH:-0}
CURR_CODE=${CURR_CODE:-1}
CURR_NAME=${CURR_NAME:-"${CURR_MAJOR}.${CURR_MINOR}.${CURR_PATCH}"}

# Compute the new version
NEW_CODE=$((CURR_CODE + 1))

case "$BUMP_TYPE" in
    patch)
        NEW_MAJOR="$CURR_MAJOR"
        NEW_MINOR="$CURR_MINOR"
        NEW_PATCH=$((CURR_PATCH + 1))
        NEW_NAME="${NEW_MAJOR}.${NEW_MINOR}.${NEW_PATCH}"
        ;;
    minor)
        NEW_MAJOR="$CURR_MAJOR"
        NEW_MINOR=$((CURR_MINOR + 1))
        NEW_PATCH=0
        NEW_NAME="${NEW_MAJOR}.${NEW_MINOR}.${NEW_PATCH}"
        ;;
    major)
        NEW_MAJOR=$((CURR_MAJOR + 1))
        NEW_MINOR=0
        NEW_PATCH=0
        NEW_NAME="${NEW_MAJOR}.${NEW_MINOR}.${NEW_PATCH}"
        ;;
    *)
        # Explicit X.Y.Z version
        NEW_MAJOR=$(echo "$BUMP_TYPE" | cut -d'.' -f1)
        NEW_MINOR=$(echo "$BUMP_TYPE" | cut -d'.' -f2)
        NEW_PATCH=$(echo "$BUMP_TYPE" | cut -d'.' -f3)
        NEW_NAME="$BUMP_TYPE"
        ;;
esac

if [[ -z "$RELEASE_NOTES" ]]; then
    RELEASE_NOTES="${APP_NAME} release v${NEW_NAME} (build ${NEW_CODE})"
fi

echo -e "${C_BOLD}Version status:${C_RESET}"
echo -e "   Current version:   ${C_YELLOW}v${CURR_NAME}${C_RESET} (versionCode: ${CURR_CODE})"
echo -e "   New version:       ${C_GREEN}${C_BOLD}v${NEW_NAME}${C_RESET} (versionCode: ${NEW_CODE})"
echo -e "   Bump type:         ${C_CYAN}${BUMP_TYPE}${C_RESET}"
echo -e "   Changelog:         \"${RELEASE_NOTES}\""
echo ""

if [[ "$DRY_RUN" == true ]]; then
    echo -e "${C_YELLOW}[DRY RUN] No file was changed. Exiting.${C_RESET}"
    exit 0
fi

# Temporary backup, used to roll back on error
PROPS_BACKUP=$(mktemp)
cp "$VERSION_PROPS" "$PROPS_BACKUP"

cleanup_on_error() {
    echo -e "\n${C_RED}Error during the release! Rolling back version.properties...${C_RESET}"
    if [[ -f "$PROPS_BACKUP" ]]; then
        cp "$PROPS_BACKUP" "$VERSION_PROPS"
        rm -f "$PROPS_BACKUP"
    fi
    exit 1
}
trap cleanup_on_error ERR INT TERM

# ------------------------------------------------------------------------------
# 2. Update version.properties
# ------------------------------------------------------------------------------
echo -e "${C_CYAN}[1/5] Updating the version configuration...${C_RESET}"
cat <<EOF > "$VERSION_PROPS"
# Spark versioning configuration (single source of truth)
# Modified automatically by release-agent / scripts/release.sh
appName=${APP_NAME}
appSlug=${APP_SLUG}
versionMajor=${NEW_MAJOR}
versionMinor=${NEW_MINOR}
versionPatch=${NEW_PATCH}
versionCode=${NEW_CODE}
versionName=${NEW_NAME}
EOF
echo -e "   Written ${VERSION_PROPS}"

# ------------------------------------------------------------------------------
# 3. Unit tests (skipped with --skip-tests)
# ------------------------------------------------------------------------------
cd "$PROJECT_ROOT"
chmod +x ./gradlew

if [[ "$SKIP_TESTS" == false ]]; then
    echo -e "${C_CYAN}[2/5] Running the unit test suite...${C_RESET}"
    ./gradlew testDebugUnitTest --quiet
    echo -e "   Unit tests passed."
else
    echo -e "${C_YELLOW}[2/5] Unit tests skipped (--skip-tests).${C_RESET}"
fi

# ------------------------------------------------------------------------------
# 4. Signed release build (assembleRelease)
# ------------------------------------------------------------------------------
echo -e "${C_CYAN}[3/5] Building the signed release APK...${C_RESET}"
./gradlew assembleRelease --quiet
echo -e "   Release APK built."

# The build succeeded: remove the properties backup
rm -f "$PROPS_BACKUP"
trap - ERR INT TERM

# ------------------------------------------------------------------------------
# 5. Locate the APK and compute the metadata
# ------------------------------------------------------------------------------
echo -e "${C_CYAN}[4/5] Preparing the artifacts for Obtainium in dist/...${C_RESET}"

# The Gradle task copyReleaseApkToRoot places the APK in apk/ with this name
RELEASE_APK_ROOT="$APK_ROOT_DIR/${APP_NAME}-v${NEW_NAME}-b${NEW_CODE}-release.apk"

# Fallback search if the name differs
if [[ ! -f "$RELEASE_APK_ROOT" ]]; then
    FOUND_APK=$(find "$APK_ROOT_DIR" -name "*release.apk" | head -n 1)
    if [[ -n "$FOUND_APK" && -f "$FOUND_APK" ]]; then
        RELEASE_APK_ROOT="$FOUND_APK"
    else
        echo -e "${C_RED}Could not find the release APK in $APK_ROOT_DIR${C_RESET}"
        exit 1
    fi
fi

# Isolated folder for this app: dist/<appSlug>/
APP_DIST_DIR="$DIST_DIR/$APP_SLUG"
mkdir -p "$APP_DIST_DIR"

VERSIONED_APK_NAME="${APP_NAME}-v${NEW_NAME}-b${NEW_CODE}-release.apk"
VERSIONED_APK_PATH="$APP_DIST_DIR/$VERSIONED_APK_NAME"
LATEST_APK_NAME="${APP_NAME}-latest.apk"
LATEST_APK_PATH="$APP_DIST_DIR/$LATEST_APK_NAME"

cp -f "$RELEASE_APK_ROOT" "$VERSIONED_APK_PATH"
cp -f "$RELEASE_APK_ROOT" "$LATEST_APK_PATH"

# SHA-256 checksum and file size
if command -v shasum &>/dev/null; then
    SHA256=$(shasum -a 256 "$VERSIONED_APK_PATH" | awk '{print $1}')
elif command -v sha256sum &>/dev/null; then
    SHA256=$(sha256sum "$VERSIONED_APK_PATH" | awk '{print $1}')
else
    SHA256="unknown"
fi

echo "$SHA256" > "$VERSIONED_APK_PATH.sha256"
echo "$SHA256" > "$LATEST_APK_PATH.sha256"

FILE_SIZE_BYTES=$(wc -c < "$VERSIONED_APK_PATH" | tr -d '[:space:]')
FILE_SIZE_HUMAN=$(du -h "$VERSIONED_APK_PATH" | cut -f1 | tr -d '[:space:]')
TIMESTAMP_ISO=$(date -u +"%Y-%m-%dT%H:%M:%SZ")

# ------------------------------------------------------------------------------
# Obtainium feed: dist/<appSlug>/version.json
# ------------------------------------------------------------------------------
cat <<EOF > "$APP_DIST_DIR/version.json"
{
  "name": "${APP_NAME}",
  "slug": "${APP_SLUG}",
  "version": "${NEW_NAME}",
  "versionCode": ${NEW_CODE},
  "apkUrl": "${VERSIONED_APK_NAME}",
  "latestApkUrl": "${LATEST_APK_NAME}",
  "sha256": "${SHA256}",
  "sizeBytes": ${FILE_SIZE_BYTES},
  "releaseNotes": "${RELEASE_NOTES}",
  "publishedAt": "${TIMESTAMP_ISO}"
}
EOF

# ------------------------------------------------------------------------------
# Landing page: dist/<appSlug>/index.html (HTML source for Obtainium and browsers)
# ------------------------------------------------------------------------------
cat <<EOF > "$APP_DIST_DIR/index.html"
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>${APP_NAME} • Official release channel</title>
  <style>
    :root {
      --bg: #111418;
      --surface: #1b1f24;
      --surface-container: #22272e;
      --primary: #9ecaff;
      --on-primary: #003258;
      --text: #e2e2e6;
      --text-muted: #8c9199;
      --border: #44474e;
      --accent: #d3bbff;
    }
    body {
      margin: 0;
      padding: 24px;
      font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Google Sans", sans-serif;
      background-color: var(--bg);
      color: var(--text);
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 100vh;
      box-sizing: border-box;
    }
    .card {
      background: var(--surface);
      border: 1px solid var(--border);
      border-radius: 24px;
      padding: 32px;
      max-width: 520px;
      width: 100%;
    }
    .badge {
      display: inline-block;
      background: var(--surface-container);
      color: var(--primary);
      padding: 6px 14px;
      border-radius: 12px;
      font-size: 0.85rem;
      font-weight: 700;
      margin-bottom: 12px;
      border: 1px solid var(--border);
    }
    h1 {
      margin: 0 0 8px 0;
      font-size: 1.8rem;
      letter-spacing: -0.5px;
    }
    p.desc {
      color: var(--text-muted);
      margin: 0 0 24px 0;
      font-size: 0.95rem;
      line-height: 1.5;
    }
    .meta-box {
      background: var(--surface-container);
      border-radius: 16px;
      padding: 16px;
      margin-bottom: 24px;
      font-size: 0.88rem;
    }
    .meta-row {
      display: flex;
      justify-content: space-between;
      margin-bottom: 8px;
    }
    .meta-row:last-child {
      margin-bottom: 0;
    }
    .meta-label { color: var(--text-muted); }
    .meta-val { font-weight: 600; color: var(--text); }
    .sha {
      font-family: monospace;
      font-size: 0.75rem;
      word-break: break-all;
      color: var(--accent);
    }
    .btn {
      display: block;
      width: 100%;
      box-sizing: border-box;
      background: var(--primary);
      color: var(--on-primary);
      text-align: center;
      padding: 16px;
      border-radius: 16px;
      text-decoration: none;
      font-weight: 700;
      font-size: 1.05rem;
      transition: opacity 0.2s;
    }
    .btn:hover { opacity: 0.9; }
    .notes {
      margin-top: 20px;
      padding-top: 16px;
      border-top: 1px solid var(--border);
      font-size: 0.85rem;
      color: var(--text-muted);
    }
    .obtainium-tip {
      margin-top: 20px;
      background: rgba(158, 202, 255, 0.08);
      border: 1px dashed var(--primary);
      border-radius: 14px;
      padding: 14px;
      font-size: 0.82rem;
      line-height: 1.4;
    }
    code {
      background: rgba(255,255,255,0.08);
      padding: 2px 6px;
      border-radius: 6px;
      font-family: monospace;
      color: var(--primary);
    }
  </style>
</head>
<body>
  <div class="card">
    <div class="badge">Self-hosted distribution</div>
    <h1>${APP_NAME}</h1>
    <p class="desc">Official release channel for ${APP_NAME}.</p>

    <div class="meta-box">
      <div class="meta-row">
        <span class="meta-label">Version:</span>
        <span class="meta-val">v${NEW_NAME} (build ${NEW_CODE})</span>
      </div>
      <div class="meta-row">
        <span class="meta-label">Size:</span>
        <span class="meta-val">${FILE_SIZE_HUMAN}</span>
      </div>
      <div class="meta-row">
        <span class="meta-label">Released:</span>
        <span class="meta-val">${TIMESTAMP_ISO}</span>
      </div>
      <div class="meta-row" style="flex-direction: column; gap: 4px; margin-top: 8px;">
        <span class="meta-label">SHA-256 checksum:</span>
        <span class="sha">${SHA256}</span>
      </div>
    </div>

    <a href="/${APP_SLUG}/${VERSIONED_APK_NAME}" class="btn">Download APK v${NEW_NAME}</a>
    <p style="text-align: center; margin-top: 10px; margin-bottom: 0;">
      <a href="/${APP_SLUG}/${LATEST_APK_NAME}" style="color: var(--primary); font-size: 0.85rem; text-decoration: none;">Direct link to the latest APK</a>
    </p>

    <div class="obtainium-tip">
      <strong>Obtainium integration:</strong><br>
      Add this app in Obtainium with the <em>HTML</em> source and this URL:<br>
      <code>${SERVER_URL:-http://localhost:3200}/${APP_SLUG}/</code><br>
      Or with the <em>Direct APK link</em> source and this URL:<br>
      <code>${SERVER_URL:-http://localhost:3200}/${APP_SLUG}/${LATEST_APK_NAME}</code>
    </div>

    <div class="notes">
      <strong>Release notes:</strong><br>
      ${RELEASE_NOTES}
    </div>
  </div>
</body>
</html>
EOF

echo -e "   Artifacts written to ${APP_DIST_DIR}:"
echo -e "      • ${VERSIONED_APK_NAME} (${FILE_SIZE_HUMAN})"
echo -e "      • ${LATEST_APK_NAME} (stable link to the latest build)"
echo -e "      • version.json (Obtainium feed)"
echo -e "      • index.html (self-hosted landing page)"

# ------------------------------------------------------------------------------
# 6. Remote distribution (HTTP upload, or SSH rsync/scp)
# ------------------------------------------------------------------------------
echo -e "${C_CYAN}[5/5] Distributing to the server...${C_RESET}"

SERVER_URL="${SERVER_URL:-}"
UPLOAD_ENDPOINT="${UPLOAD_ENDPOINT:-${SERVER_URL:+${SERVER_URL}/upload}}"
UPLOAD_TOKEN="${UPLOAD_TOKEN:-}"
DEPLOY_METHOD="${DEPLOY_METHOD:-http}"
REMOTE_HOST="${REMOTE_HOST:-}"
REMOTE_USER="${REMOTE_USER:-}"
REMOTE_PATH="${REMOTE_PATH:-}"
REMOTE_PORT="${REMOTE_PORT:-22}"

if [[ "$NO_DEPLOY" == false && -n "$UPLOAD_ENDPOINT" && "$DEPLOY_METHOD" == "http" ]]; then
    echo -e "   Destination server: ${C_YELLOW}${SERVER_URL:-$UPLOAD_ENDPOINT}${C_RESET}"
    echo -e "   Uploading the release package for ${APP_SLUG}/ over HTTP..."

    # Stream dist/<appSlug> as a tar.gz archive to the upload endpoint
    UPLOAD_RES=$(COPYFILE_DISABLE=1 tar --exclude='._*' -czf - -C "$DIST_DIR" "$APP_SLUG" | curl -s -w "\n%{http_code}" \
        -X POST \
        -H "Authorization: Bearer ${UPLOAD_TOKEN}" \
        -H "Content-Type: application/gzip" \
        -H "X-App-Name: ${APP_SLUG}" \
        --data-binary @- \
        "$UPLOAD_ENDPOINT" || echo -e "\n000")

    HTTP_STATUS=$(echo "$UPLOAD_RES" | tail -n 1)
    RESPONSE_BODY=$(echo "$UPLOAD_RES" | sed '$d')

    if [[ "$HTTP_STATUS" == "200" ]]; then
        echo -e "   ${C_GREEN}Upload completed (HTTP 200).${C_RESET}"
        echo -e "      Server response: ${RESPONSE_BODY}"
        echo -e "      Obtainium feed: ${C_BOLD}${SERVER_URL}/${APP_SLUG}/version.json${C_RESET}"
        echo -e "      Landing page:   ${C_BOLD}${SERVER_URL}/${APP_SLUG}/${C_RESET}"
    elif [[ "$HTTP_STATUS" == "404" ]]; then
        echo -e "   ${C_YELLOW}Upload endpoint not found (HTTP 404).${C_RESET}"
    elif [[ "$HTTP_STATUS" == "401" ]]; then
        echo -e "   ${C_RED}Error 401 Unauthorized: the bearer token in .env does not match the server.${C_RESET}"
    else
        echo -e "   ${C_YELLOW}Upload failed (status: ${HTTP_STATUS}). Response: ${RESPONSE_BODY}${C_RESET}"
    fi

elif [[ "$NO_DEPLOY" == false && -n "$REMOTE_HOST" && -n "$REMOTE_PATH" ]]; then
    echo -e "   Syncing over SSH to ${REMOTE_USER:+$REMOTE_USER@}${REMOTE_HOST}:${REMOTE_PATH}..."
    if command -v rsync &>/dev/null; then
        rsync -avz --delete -e "ssh -p ${REMOTE_PORT}" "$DIST_DIR/" "${REMOTE_USER:+$REMOTE_USER@}${REMOTE_HOST}:${REMOTE_PATH}/"
        echo -e "   rsync sync completed."
    elif command -v scp &>/dev/null; then
        scp -P "${REMOTE_PORT}" -r "$DIST_DIR"/* "${REMOTE_USER:+$REMOTE_USER@}${REMOTE_HOST}:${REMOTE_PATH}/"
        echo -e "   scp sync completed."
    else
        echo -e "${C_YELLOW}   Neither rsync nor scp is available. Remote distribution skipped.${C_RESET}"
    fi
else
    if [[ "$NO_DEPLOY" == true ]]; then
        echo -e "   Remote sync disabled by the --no-deploy flag."
    else
        echo -e "   No remote destination configured in .env."
        echo -e "      The files are ready in the local folder: ${C_BOLD}dist/${C_RESET}"
    fi
fi

# ------------------------------------------------------------------------------
# Final summary
# ------------------------------------------------------------------------------
echo ""
echo -e "${C_GREEN}${C_BOLD}==================================================================${C_RESET}"
echo -e "${C_GREEN}${C_BOLD}RELEASE COMPLETED SUCCESSFULLY${C_RESET}"
echo -e "${C_GREEN}${C_BOLD}==================================================================${C_RESET}"
echo -e "Application:        ${C_BOLD}${APP_NAME}${C_RESET} (${APP_SLUG})"
echo -e "Published version:  ${C_BOLD}v${NEW_NAME}${C_RESET} (versionCode: ${NEW_CODE})"
echo -e "Output folder:      ${APP_DIST_DIR}"
echo -e "SHA-256 checksum:   ${SHA256}"
echo -e "Obtainium feed:     ${APP_DIST_DIR}/version.json"
echo ""
echo -e "${C_BOLD}How to add the app in Obtainium on Android:${C_RESET}"
echo -e "1. Open Obtainium and tap ${C_CYAN}Add App (+)${C_RESET}"
echo -e "2. Paste the feed URL: ${C_YELLOW}${SERVER_URL:-http://localhost:3200}/${APP_SLUG}/version.json${C_RESET}"
echo -e "   Or the landing page URL with the HTML source: ${C_YELLOW}${SERVER_URL:-http://localhost:3200}/${APP_SLUG}/${C_RESET}"
echo -e "3. Obtainium detects new versions automatically and notifies you about updates."
echo "=================================================================="
