#!/usr/bin/env bash
# ==============================================================================
# AuraStanza / Spark! Release Agent (Self-Hosted & Obtainium CI/CD)
# ==============================================================================
# Automates semantic versioning bump, unit testing, release compilation,
# Obtainium version.json metadata generation, local dist/ export,
# and optional remote deployment (via rsync/scp/docker).
# ==============================================================================

set -euo pipefail

# Color palette for modern CLI aesthetics
C_RESET="\033[0m"
C_BOLD="\033[1m"
C_CYAN="\033[36m"
C_GREEN="\033[32m"
C_YELLOW="\033[33m"
C_RED="\033[31m"
C_MAGENTA="\033[35m"

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

# Load local environment overrides if present
if [[ -f "$PROJECT_ROOT/.env" ]]; then
    # shellcheck disable=SC1091
    source "$PROJECT_ROOT/.env"
fi

print_header() {
    echo -e "${C_CYAN}${C_BOLD}"
    echo "=================================================================="
    echo "            🚀 Spark! Continuous Release Agent"
    echo "             Self-Hosted & Obtainium Publisher"
    echo "=================================================================="
    echo -e "${C_RESET}"
}

usage() {
    print_header
    echo -e "${C_BOLD}UTILIZZO:${C_RESET}"
    echo -e "  ./release-agent [patch|minor|major|<versione_esplicita>] [opzioni]"
    echo -e "  ./scripts/release.sh [patch|minor|major|<versione_esplicita>] [opzioni]"
    echo ""
    echo -e "${C_BOLD}PARAMETRI:${C_RESET}"
    echo -e "  ${C_GREEN}patch${C_RESET}               Incrementa patch: 1.0.0 -> 1.0.1 (default)"
    echo -e "  ${C_GREEN}minor${C_RESET}               Incrementa minor: 1.0.0 -> 1.1.0"
    echo -e "  ${C_GREEN}major${C_RESET}               Incrementa major: 1.0.0 -> 2.0.0"
    echo -e "  ${C_GREEN}<X.Y.Z>${C_RESET}             Imposta una versione esplicita (es. 1.2.0)"
    echo ""
    echo -e "${C_BOLD}OPZIONI:${C_RESET}"
    echo -e "  ${C_YELLOW}-m, --notes \"...\"${C_RESET}    Note di rilascio (changelog per version.json)"
    echo -e "  ${C_YELLOW}--skip-tests${C_RESET}         Salta l'esecuzione dei test unitari preliminari"
    echo -e "  ${C_YELLOW}--no-deploy${C_RESET}          Salta la sincronizzazione remota (anche se .env è configurato)"
    echo -e "  ${C_YELLOW}--dry-run${C_RESET}            Mostra le modifiche calcolate senza eseguire build o modifiche file"
    echo -e "  ${C_YELLOW}-h, --help${C_RESET}           Mostra questo manuale di aiuto"
    echo ""
    echo -e "${C_BOLD}ESEMPI:${C_RESET}"
    echo -e "  ./release-agent patch -m \"Fix dark mode M3 colors and performance\""
    echo -e "  ./release-agent minor -m \"Added real-time energy charts and scenes\""
    echo -e "  ./release-agent 1.5.0 -m \"Major redesign with Material 3 Expressive\""
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
                echo -e "${C_RED}❌ Opzione non riconosciuta: $1${C_RESET}"
                usage
            fi
            ;;
    esac
done

print_header

# ------------------------------------------------------------------------------
# 1. Lettura e Calcolo Versionamento Semantico
# ------------------------------------------------------------------------------
if [[ ! -f "$VERSION_PROPS" ]]; then
    echo -e "${C_YELLOW}⚠️  version.properties non trovato. Inizializzazione con valori di default 1.0.0 (build 1)...${C_RESET}"
    cat <<EOF > "$VERSION_PROPS"
versionMajor=1
versionMinor=0
versionPatch=0
versionCode=1
versionName=1.0.0
EOF
fi

# Leggi proprietà esistenti
APP_NAME=$(grep -E '^appName=' "$VERSION_PROPS" | cut -d'=' -f2 | tr -d '[:space:]')
APP_NAME=${APP_NAME:-"AuraStanza"}
APP_SLUG=$(grep -E '^appSlug=' "$VERSION_PROPS" | cut -d'=' -f2 | tr -d '[:space:]')
if [[ -z "$APP_SLUG" ]]; then
    APP_SLUG=$(echo "$APP_NAME" | tr '[:upper:]' '[:lower:]' | tr -cs 'a-z0-9' '-' | sed 's/^-//;s/-$//')
fi

CURR_MAJOR=$(grep -E '^versionMajor=' "$VERSION_PROPS" | cut -d'=' -f2 | tr -d '[:space:]')
CURR_MINOR=$(grep -E '^versionMinor=' "$VERSION_PROPS" | cut -d'=' -f2 | tr -d '[:space:]')
CURR_PATCH=$(grep -E '^versionPatch=' "$VERSION_PROPS" | cut -d'=' -f2 | tr -d '[:space:]')
CURR_CODE=$(grep -E '^versionCode=' "$VERSION_PROPS" | cut -d'=' -f2 | tr -d '[:space:]')
CURR_NAME=$(grep -E '^versionName=' "$VERSION_PROPS" | cut -d'=' -f2 | tr -d '[:space:]')

# Fallback di sicurezza se vuoti
CURR_MAJOR=${CURR_MAJOR:-1}
CURR_MINOR=${CURR_MINOR:-0}
CURR_PATCH=${CURR_PATCH:-0}
CURR_CODE=${CURR_CODE:-1}
CURR_NAME=${CURR_NAME:-"${CURR_MAJOR}.${CURR_MINOR}.${CURR_PATCH}"}

# Calcola nuova versione
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
        # Versione esplicita X.Y.Z
        NEW_MAJOR=$(echo "$BUMP_TYPE" | cut -d'.' -f1)
        NEW_MINOR=$(echo "$BUMP_TYPE" | cut -d'.' -f2)
        NEW_PATCH=$(echo "$BUMP_TYPE" | cut -d'.' -f3)
        NEW_NAME="$BUMP_TYPE"
        ;;
esac

if [[ -z "$RELEASE_NOTES" ]]; then
    RELEASE_NOTES="${APP_NAME} release v${NEW_NAME} (build ${NEW_CODE})"
fi

echo -e "${C_BOLD}📌 Stato Versionamento:${C_RESET}"
echo -e "   Versione Attuale:  ${C_YELLOW}v${CURR_NAME}${C_RESET} (versionCode: ${CURR_CODE})"
echo -e "   Nuova Versione:    ${C_GREEN}${C_BOLD}v${NEW_NAME}${C_RESET} (versionCode: ${NEW_CODE})"
echo -e "   Tipo Incremento:   ${C_CYAN}${BUMP_TYPE}${C_RESET}"
echo -e "   Changelog:         \"${RELEASE_NOTES}\""
echo ""

if [[ "$DRY_RUN" == true ]]; then
    echo -e "${C_YELLOW}🔍 [DRY RUN] Nessuna modifica apportata ai file. Uscita.${C_RESET}"
    exit 0
fi

# Backup temporaneo per rollback in caso di errore
PROPS_BACKUP=$(mktemp)
cp "$VERSION_PROPS" "$PROPS_BACKUP"

cleanup_on_error() {
    echo -e "\n${C_RED}❌ Errore rilevato durante il ciclo di rilascio! Rollback version.properties in corso...${C_RESET}"
    if [[ -f "$PROPS_BACKUP" ]]; then
        cp "$PROPS_BACKUP" "$VERSION_PROPS"
        rm -f "$PROPS_BACKUP"
    fi
    exit 1
}
trap cleanup_on_error ERR INT TERM

# ------------------------------------------------------------------------------
# 2. Aggiornamento version.properties
# ------------------------------------------------------------------------------
echo -e "${C_CYAN}[1/5] Aggiornamento file di configurazione semver...${C_RESET}"
cat <<EOF > "$VERSION_PROPS"
# AuraStanza Versioning Configuration (SSOT)
# Modified automatically by release-agent / scripts/release.sh
appName=${APP_NAME}
appSlug=${APP_SLUG}
versionMajor=${NEW_MAJOR}
versionMinor=${NEW_MINOR}
versionPatch=${NEW_PATCH}
versionCode=${NEW_CODE}
versionName=${NEW_NAME}
EOF
echo -e "   ✅ Scritto ${VERSION_PROPS}"

# ------------------------------------------------------------------------------
# 3. Test Unitari di Sicurezza (opzionale se --skip-tests)
# ------------------------------------------------------------------------------
cd "$PROJECT_ROOT"
chmod +x ./gradlew

if [[ "$SKIP_TESTS" == false ]]; then
    echo -e "${C_CYAN}[2/5] Esecuzione suite di test unitari automatizzati...${C_RESET}"
    ./gradlew testDebugUnitTest --quiet
    echo -e "   ✅ Tutti i test unitari sono passati con successo!"
else
    echo -e "${C_YELLOW}[2/5] Test unitari saltati (--skip-tests richiesto).${C_RESET}"
fi

# ------------------------------------------------------------------------------
# 4. Compilazione Binario di Rilascio Firmato (assembleRelease)
# ------------------------------------------------------------------------------
echo -e "${C_CYAN}[3/5] Compilazione APK Release (firmato crittograficamente)...${C_RESET}"
./gradlew assembleRelease --quiet
echo -e "   ✅ Compilazione APK release completata con successo!"

# Rimuovi backup version.properties poiché il build è riuscito
rm -f "$PROPS_BACKUP"
trap - ERR INT TERM

# ------------------------------------------------------------------------------
# 5. Individuazione APK generato e Calcolo Metadati
# ------------------------------------------------------------------------------
echo -e "${C_CYAN}[4/5] Riorganizzazione artefatti per Obtainium in dist/...${C_RESET}"

# L'APK compilato viene già consegnato in apk/ con nome coerente da copyApkToRoot
RELEASE_APK_ROOT="$APK_ROOT_DIR/${APP_NAME}-v${NEW_NAME}-b${NEW_CODE}-release.apk"

# Ricerca alternativa se copyApkToRoot ha generato un nome differente
if [[ ! -f "$RELEASE_APK_ROOT" ]]; then
    FOUND_APK=$(find "$APK_ROOT_DIR" -name "*release.apk" | head -n 1)
    if [[ -n "$FOUND_APK" && -f "$FOUND_APK" ]]; then
        RELEASE_APK_ROOT="$FOUND_APK"
    else
        echo -e "${C_RED}❌ Impossibile individuare l'APK di release in $APK_ROOT_DIR${C_RESET}"
        exit 1
    fi
fi

# Prepara cartella isolata per app: dist/<appSlug>/
APP_DIST_DIR="$DIST_DIR/$APP_SLUG"
mkdir -p "$APP_DIST_DIR"

DEST_APK_NAME="${APP_NAME}-v${NEW_NAME}-b${NEW_CODE}-universal.apk"
DEST_APK_PATH="$APP_DIST_DIR/$DEST_APK_NAME"
LATEST_APK_NAME="${APP_NAME}-universal.apk"
LATEST_APK_PATH="$APP_DIST_DIR/$LATEST_APK_NAME"

# Copia file in dist/<appSlug>/ (nomi universali per Obtainium + alias legacy)
cp -f "$RELEASE_APK_ROOT" "$DEST_APK_PATH"
cp -f "$RELEASE_APK_ROOT" "$LATEST_APK_PATH"
cp -f "$RELEASE_APK_ROOT" "$APP_DIST_DIR/${APP_NAME}-v${NEW_NAME}-b${NEW_CODE}-release.apk"
cp -f "$RELEASE_APK_ROOT" "$APP_DIST_DIR/${APP_NAME}-latest.apk"

# Calcolo SHA-256 e dimensione
if command -v shasum &>/dev/null; then
    SHA256=$(shasum -a 256 "$DEST_APK_PATH" | awk '{print $1}')
elif command -v sha256sum &>/dev/null; then
    SHA256=$(sha256sum "$DEST_APK_PATH" | awk '{print $1}')
else
    SHA256="unknown"
fi

echo "$SHA256" > "$DEST_APK_PATH.sha256"
echo "$SHA256" > "$LATEST_APK_PATH.sha256"
echo "$SHA256" > "$APP_DIST_DIR/${APP_NAME}-v${NEW_NAME}-b${NEW_CODE}-release.apk.sha256"
echo "$SHA256" > "$APP_DIST_DIR/${APP_NAME}-latest.apk.sha256"

# Dimensione file in byte e human-readable
FILE_SIZE_BYTES=$(wc -c < "$DEST_APK_PATH" | tr -d '[:space:]')
FILE_SIZE_HUMAN=$(du -h "$DEST_APK_PATH" | cut -f1 | tr -d '[:space:]')
TIMESTAMP_ISO=$(date -u +"%Y-%m-%dT%H:%M:%SZ")

# ------------------------------------------------------------------------------
# Generazione version.json conforme a Obtainium in dist/<appSlug>/
# ------------------------------------------------------------------------------
cat <<EOF > "$APP_DIST_DIR/version.json"
{
  "name": "${APP_NAME}",
  "slug": "${APP_SLUG}",
  "version": "${NEW_NAME}",
  "versionCode": ${NEW_CODE},
  "apkUrl": "${DEST_APK_NAME}",
  "latestApkUrl": "${LATEST_APK_NAME}",
  "sha256": "${SHA256}",
  "sizeBytes": ${FILE_SIZE_BYTES},
  "releaseNotes": "${RELEASE_NOTES}",
  "publishedAt": "${TIMESTAMP_ISO}"
}
EOF

# ------------------------------------------------------------------------------
# Generazione landing page statica index.html per accesso Web e scrape HTML
# ------------------------------------------------------------------------------
cat <<EOF > "$APP_DIST_DIR/index.html"
<!DOCTYPE html>
<html lang="it">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>${APP_NAME} • Canale di Rilascio Ufficiale</title>
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
      box-shadow: 0 16px 36px rgba(0,0,0,0.4);
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
    <div class="badge">Spark! Self-Hosted Distribution</div>
    <h1>${APP_NAME}</h1>
    <p class="desc">Promemoria medicine e gestione inventario con live notification in Material 3 Expressive.</p>

    <div class="meta-box">
      <div class="meta-row">
        <span class="meta-label">Versione:</span>
        <span class="meta-val">v${NEW_NAME} (Build ${NEW_CODE})</span>
      </div>
      <div class="meta-row">
        <span class="meta-label">Dimensione:</span>
        <span class="meta-val">${FILE_SIZE_HUMAN}</span>
      </div>
      <div class="meta-row">
        <span class="meta-label">Rilasciato il:</span>
        <span class="meta-val">${TIMESTAMP_ISO}</span>
      </div>
      <div class="meta-row" style="flex-direction: column; gap: 4px; margin-top: 8px;">
        <span class="meta-label">SHA-256 Checksum:</span>
        <span class="sha">${SHA256}</span>
      </div>
    </div>

    <a href="/${APP_SLUG}/${DEST_APK_NAME}" class="btn">Scarica APK v${NEW_NAME} (Universal)</a>
    <p style="text-align: center; margin-top: 10px; margin-bottom: 0;">
      <a href="/${APP_SLUG}/${LATEST_APK_NAME}" style="color: var(--primary); font-size: 0.85rem; text-decoration: none;">⬇️ Link Diretto Ultima Versione Universale</a>
    </p>

    <div class="obtainium-tip">
      📱 <strong>Integrazione Obtainium:</strong><br>
      Aggiungi questa applicazione su Obtainium usando la sorgente <em>HTML</em> puntando all'URL:<br>
      <code>${SERVER_URL:-http://localhost:3200}/${APP_SLUG}/</code><br>
      Oppure usando la sorgente <em>Direct APK Link</em> puntando a:<br>
      <code>${SERVER_URL:-http://localhost:3200}/${APP_SLUG}/${LATEST_APK_NAME}</code>
    </div>

    <div class="notes">
      <strong>Note di Rilascio:</strong><br>
      ${RELEASE_NOTES}
    </div>
  </div>
</body>
</html>
EOF

echo -e "   ✅ Generati artefatti in ${APP_DIST_DIR}:"
echo -e "      • ${DEST_APK_NAME} (${FILE_SIZE_HUMAN})"
echo -e "      • ${LATEST_APK_NAME} (link statico persistente)"
echo -e "      • version.json (feed Obtainium)"
echo -e "      • index.html (landing page self-hosted)"

# ------------------------------------------------------------------------------
# 6. Distribuzione Remota (HTTP Upload su Caddy o SSH rsync/scp)
# ------------------------------------------------------------------------------
echo -e "${C_CYAN}[5/5] Distribuzione verso il server...${C_RESET}"

SERVER_URL="${SERVER_URL:-}"
UPLOAD_ENDPOINT="${UPLOAD_ENDPOINT:-${SERVER_URL:+${SERVER_URL}/upload}}"
UPLOAD_TOKEN="${UPLOAD_TOKEN:-}"
DEPLOY_METHOD="${DEPLOY_METHOD:-http}"
REMOTE_HOST="${REMOTE_HOST:-}"
REMOTE_USER="${REMOTE_USER:-}"
REMOTE_PATH="${REMOTE_PATH:-}"
REMOTE_PORT="${REMOTE_PORT:-22}"

if [[ "$NO_DEPLOY" == false && -n "$UPLOAD_ENDPOINT" && "$DEPLOY_METHOD" == "http" ]]; then
    echo -e "   🌐 Server di destinazione: ${C_YELLOW}${SERVER_URL:-$UPLOAD_ENDPOINT}${C_RESET}"
    echo -e "   🚀 Caricamento pacchetto release isolato per app (${APP_SLUG}/) via HTTP..."

    # Streaming di dist/<appSlug> racchiuso nella cartella dell'app direttamente a /upload
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
        echo -e "   ${C_GREEN}✅ Upload HTTP completato con successo (HTTP 200)!${C_RESET}"
        echo -e "      Risposta server: ${RESPONSE_BODY}"
        echo -e "      Obtainium Feed: ${C_BOLD}${SERVER_URL}/${APP_SLUG}/version.json${C_RESET}"
        echo -e "      Landing Page:   ${C_BOLD}${SERVER_URL}/${APP_SLUG}/${C_RESET}"
    elif [[ "$HTTP_STATUS" == "404" ]]; then
        echo -e "   ${C_YELLOW}⚠️  Endpoint /upload non trovato (HTTP 404).${C_RESET}"
    elif [[ "$HTTP_STATUS" == "401" ]]; then
        echo -e "   ${C_RED}❌ Errore 401 Unauthorized: Il Bearer token in .env non corrisponde a quello del server.${C_RESET}"
    else
        echo -e "   ${C_YELLOW}⚠️  Upload HTTP non riuscito (Status: ${HTTP_STATUS}). Risposta: ${RESPONSE_BODY}${C_RESET}"
    fi

elif [[ "$NO_DEPLOY" == false && -n "$REMOTE_HOST" && -n "$REMOTE_PATH" ]]; then
    echo -e "   🚀 Sincronizzazione SSH verso ${REMOTE_USER:+$REMOTE_USER@}${REMOTE_HOST}:${REMOTE_PATH}..."
    if command -v rsync &>/dev/null; then
        rsync -avz --delete -e "ssh -p ${REMOTE_PORT}" "$DIST_DIR/" "${REMOTE_USER:+$REMOTE_USER@}${REMOTE_HOST}:${REMOTE_PATH}/"
        echo -e "   ✅ Sincronizzazione rsync completata con successo!"
    elif command -v scp &>/dev/null; then
        scp -P "${REMOTE_PORT}" -r "$DIST_DIR"/* "${REMOTE_USER:+$REMOTE_USER@}${REMOTE_HOST}:${REMOTE_PATH}/"
        echo -e "   ✅ Sincronizzazione scp completata con successo!"
    else
        echo -e "${C_YELLOW}   ⚠️ rsync o scp non disponibili. Distribuzione remota ignorata.${C_RESET}"
    fi
else
    if [[ "$NO_DEPLOY" == true ]]; then
        echo -e "   ℹ️ Sincronizzazione remota disattivata tramite flag (--no-deploy)."
    else
        echo -e "   ℹ️ Nessuna destinazione remota configurata in .env."
        echo -e "      I file sono pronti nella cartella locale: ${C_BOLD}dist/${C_RESET}"
    fi
fi

# ------------------------------------------------------------------------------
# Riepilogo Finale
# ------------------------------------------------------------------------------
echo ""
echo -e "${C_GREEN}${C_BOLD}==================================================================${C_RESET}"
echo -e "${C_GREEN}${C_BOLD}🎉 RILASCIO COMPLETATO CON SUCCESSO!${C_RESET}"
echo -e "${C_GREEN}${C_BOLD}==================================================================${C_RESET}"
echo -e "📦 Applicazione:          ${C_BOLD}${APP_NAME}${C_RESET} (${APP_SLUG})"
echo -e "📦 Versione Pubblicata:  ${C_BOLD}v${NEW_NAME}${C_RESET} (versionCode: ${NEW_CODE})"
echo -e "📁 Directory Output:      ${APP_DIST_DIR}"
echo -e "🔑 SHA-256 Checksum:     ${SHA256}"
echo -e "📄 File di Tracking:     ${APP_DIST_DIR}/version.json"
echo ""
echo -e "${C_BOLD}Come configurare Obtainium sullo smartphone Android (rete Tailscale):${C_RESET}"
echo -e "1. Apri Obtainium -> Tocca ${C_CYAN}Aggiungi App (+)${C_RESET}"
echo -e "2. Incolla l'URL del tuo endpoint: ${C_YELLOW}${SERVER_URL:-http://localhost:3200}/${APP_SLUG}/version.json${C_RESET}"
echo -e "   Oppure l'URL della landing page: ${C_YELLOW}${SERVER_URL:-http://localhost:3200}/${APP_SLUG}/${C_RESET} (con sorgente HTML)"
echo -e "3. Obtainium rileverà automaticamente la nuova versione e ti notificherà gli aggiornamenti."
echo "=================================================================="
