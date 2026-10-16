# Antigravity & AI Agent Rule: Spark! Continuous Agent Builder & Multi-App Release

## Overview
This rule mandates that all continuous release, semantic versioning, build automation, and self-hosted distribution operations for Spark! applications follow an automated, multi-app isolated architecture. The AI Agent acts as the autonomous release engineer, driving the entire lifecycle directly from the client without requiring manual user intervention or server-side reconfiguration.

---

## 🏢 1. Client-Driven Multi-App Isolation Standard (Mandatory Invariant)
The self-hosted distribution server (e.g. Caddy + Obtainium on Tailscale `http://<SERVER_IP>:3200`) is a **multi-tenant repository** that hosts multiple Spark! applications simultaneously.

- **Mandatory App Namespace Subfolder**:
  - Every project MUST isolate its distribution artifacts inside a dedicated subdirectory named after the application slug: `dist/<appSlug>/` (e.g., `dist/my-app/`).
  - **NEVER** write release artifacts (`version.json`, `index.html`, `*-latest.apk`, or versioned APKs) directly into the root of `dist/`.
- **Client-Driven Isolation (Zero Server Changes)**:
  - The client release script packaging preserves the app slug root directory when streaming to the remote endpoint:
    ```bash
    COPYFILE_DISABLE=1 tar --exclude='._*' -czf - -C dist "<appSlug>" | curl -X POST -H "Authorization: Bearer <TOKEN>" -H "Content-Type: application/gzip" --data-binary @- "<SERVER_URL>/upload"
    ```
  - When extracted on the server into `/srv`, the archive automatically creates `/srv/<appSlug>/` (which maps to `<hostDistRoot>/<appSlug>/`).
  - The server requires **zero manual configuration or reconfiguration** when a new application is deployed.
- **Obtainium & Web Endpoints**:
  - Obtainium Update Feed: `<SERVER_URL>/<appSlug>/version.json`
  - Branded Web Landing Page: `<SERVER_URL>/<appSlug>/`
  - Latest Persistent APK: `<SERVER_URL>/<appSlug>/<AppName>-latest.apk`
  - Versioned Release APK: `<SERVER_URL>/<appSlug>/<AppName>-v<versionName>-b<versionCode>-release.apk`

---

## 🔢 2. Single Source of Truth for Versioning (`version.properties`)
- Version state is exclusively managed in `<projectRoot>/version.properties`:
  ```properties
  appName=<AppName>
  appSlug=<appSlug>
  versionMajor=1
  versionMinor=0
  versionPatch=4
  versionCode=5
  versionName=1.0.4
  ```
- `app/build.gradle.kts` dynamically reads `version.properties` for `versionCode`, `versionName`, and root `/apk` copy tasks.
- Releases follow SemVer conventions: `patch` (bug fixes), `minor` (features), `major` (breaking changes), or explicit `<X.Y.Z>`.

---

## ⚙️ 3. The 5-Phase Automated Release Pipeline
Whenever the user or agent triggers a release, the pipeline executes sequentially:
1. **SemVer Calculation & Properties Update**:
   - Computes new `versionCode` (always incremental integer: `CURR_CODE + 1`) and new `versionName`.
   - Atomically updates `version.properties` with rollback protection.
2. **Automated Unit Testing**:
   - Executes `./gradlew testDebugUnitTest --quiet`.
   - If tests fail, the pipeline halts immediately and restores the previous version.
3. **Signed Release Compilation**:
   - Compiles `./gradlew assembleRelease --quiet`.
   - Uses cryptographic release signing configuration (v2 scheme compliant).
   - Delivers root APK copy to `<projectRoot>/apk/<AppName>-v<versionName>-b<versionCode>-release.apk`.
4. **Isolated Metadata & Obtainium Feed Generation**:
   - Copies APKs to `dist/<appSlug>/<AppName>-v<versionName>-b<versionCode>-release.apk` and `dist/<appSlug>/<AppName>-latest.apk`.
   - Computes SHA-256 checksums (`.sha256`).
   - Generates Obtainium JSON feed `dist/<appSlug>/version.json`:
     ```json
     {
       "name": "<AppName>",
       "slug": "<appSlug>",
       "version": "<versionName>",
       "versionCode": <versionCode>,
       "apkUrl": "<AppName>-v<versionName>-b<versionCode>-release.apk",
       "latestApkUrl": "<AppName>-latest.apk",
       "sha256": "<SHA256>",
       "sizeBytes": <sizeBytes>,
       "releaseNotes": "<notes>",
       "publishedAt": "<timestampISO>"
     }
     ```
   - Generates localized, modern Material 3 dark-themed `dist/<appSlug>/index.html` landing page.
5. **Authenticated Streaming HTTP Deployment**:
   - Streams `dist/<appSlug>/` via tar.gz to `${SERVER_URL}/upload` with Bearer token authentication.
   - Validates HTTP 200 response and outputs the live endpoints.

---

## 🤖 4. Autonomous Agent Execution Rules
1. **Zero User Friction**: The user MUST NOT be asked to run release commands manually in their terminal. The AI Agent executes releases directly via `run_command`:
   ```bash
   ./release-agent [patch|minor|major] -m "Release notes"
   ```
   or via the build automation entrypoint:
   ```bash
   ./build/build.sh --release [patch|minor|major] -m "Release notes"
   ```
2. **Pre-flight Health Probing**:
   - Before reporting final delivery, the agent performs a curl probe to verify that `${SERVER_URL}/<appSlug>/version.json` responds with `HTTP 200 OK`.
3. **Obtainium Configuration Instructions**:
   - At the conclusion of each release, the agent provides clear, clickable links and instructions for configuring Obtainium on Android devices connected via Tailscale.
