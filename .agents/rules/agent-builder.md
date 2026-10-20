# Rule: Continuous Release & Multi-App Distribution

## Overview

This rule covers every continuous release, semantic versioning, build automation and self-hosted distribution operation for Spark applications. The agent acts as the release engineer and drives the whole lifecycle from the project, without manual steps by the user and without server-side changes.

The detailed procedure is in the skill `.agents/skills/agent-builder/SKILL.md`.

---

## 1. Client-driven multi-app isolation (mandatory)

The self-hosted distribution server (for example a Caddy server reached over a private network such as Tailscale, or any HTTP server) is a **multi-tenant repository** that can host several Spark applications at the same time.

- **Mandatory app namespace:**
  - Every project MUST isolate its distribution artifacts in a subdirectory named after the app slug: `dist/<appSlug>/`.
  - NEVER write release artifacts (`version.json`, `index.html`, `*-latest.apk`, versioned APKs) directly in the root of `dist/`.
- **Client-driven isolation (no server changes):**
  - The release script streams the app folder to the server as a tar.gz archive, so the server creates `/<appSlug>/` on its own.
  - Each new app is deployed without any server configuration.
- **Endpoints:**
  - Obtainium feed: `<SERVER_URL>/<appSlug>/version.json`
  - Landing page: `<SERVER_URL>/<appSlug>/`
  - Latest APK: `<SERVER_URL>/<appSlug>/<AppName>-latest.apk`
  - Versioned APK: `<SERVER_URL>/<appSlug>/<AppName>-v<versionName>-b<versionCode>-release.apk`

---

## 2. Single source of truth for versioning

- Version state lives only in `<projectRoot>/version.properties`:
  ```properties
  appName=<AppName>
  appSlug=<appSlug>
  versionMajor=1
  versionMinor=0
  versionPatch=4
  versionCode=5
  versionName=1.0.4
  ```
- `app/build.gradle.kts` reads `version.properties` for `versionCode`, `versionName` and the copy task that places the APK in `/apk`.
- Releases follow SemVer: `patch` (fixes), `minor` (features), `major` (breaking changes), or an explicit `<X.Y.Z>`.

---

## 3. The five-phase release pipeline

Each release runs these phases in order:

1. **SemVer calculation and properties update:**
   - Compute the new `versionCode` (always the previous value plus 1) and the new `versionName`.
   - Update `version.properties` atomically, with rollback on failure.
2. **Unit tests:**
   - Run `./gradlew testDebugUnitTest --quiet`.
   - If a test fails, stop immediately and restore the previous `version.properties`.
3. **Signed release build:**
   - Run `./gradlew assembleRelease --quiet`, using the release keystore (v2 signing scheme).
   - Copy the APK to `<projectRoot>/apk/<AppName>-v<versionName>-b<versionCode>-release.apk`.
4. **Metadata and Obtainium feed:**
   - Copy the APK to `dist/<appSlug>/<AppName>-v<versionName>-b<versionCode>-release.apk` and to `dist/<appSlug>/<AppName>-latest.apk`.
   - Compute the SHA-256 checksums (`.sha256` files).
   - Generate the Obtainium feed `dist/<appSlug>/version.json`:
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
   - Generate the landing page `dist/<appSlug>/index.html`, in English, with a modern Material 3 dark theme.
5. **Authenticated streaming deployment:**
   - Stream `dist/<appSlug>/` as a tar.gz archive to `${SERVER_URL}/upload`, with a bearer token.
   - Check for an HTTP 200 response, then print the live endpoints.

---

## 4. Agent execution rules

1. **Zero user friction:** the user is never asked to run release commands by hand. The agent runs the release through the shell:
   ```bash
   ./release-agent patch -m "Release notes"
   ```
   `./release-agent` is the only entry point. It forwards to `scripts/release.sh`.
2. **Pre-flight probe:** before reporting the release as delivered, check that `${SERVER_URL}/<appSlug>/version.json` answers with HTTP 200.
3. **Obtainium instructions:** at the end of each release, give clear links and steps to add or update the app in Obtainium on an Android device on the same network or private network.
4. **Secrets:** server URL, upload endpoint and bearer token come from environment variables or the local `.env` file. They are never written into the repository or printed in the output (see `development-standards.md`, section 9).
