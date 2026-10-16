---
name: agent-builder
description: Continuous build, semantic versioning, and Obtainium multi-app self-hosted release automation for Android projects using Gradle and Caddy over Tailscale.
---

# Agent Builder: Continuous Build & Multi-App Release Skill

This skill documents the automated release pipeline, semantic versioning, and multi-tenant distribution system for Spark! Android applications.

## Key Capabilities
- **Semantic Versioning (SemVer)**: Automated bumps for `patch`, `minor`, `major` via `version.properties`.
- **Client-Driven Multi-App Isolation**: Every app deploys into its own namespace directory (`dist/<appSlug>/`) on the shared Obtainium distribution server.
- **Automated Verification**: Automatic execution of unit tests (`testDebugUnitTest`) before release builds.
- **Signed Release Packaging**: Compiles release APK with cryptographic signing and delivers to root `/apk/`.
- **Obtainium Metadata Generation**: Emits `dist/<appSlug>/version.json` and static HTML landing page `dist/<appSlug>/index.html`.
- **HTTP Streaming Deployment**: One-shot authenticated gzip streaming over Tailscale HTTP to `$SERVER_URL/upload`.

## Quick Command Reference

```bash
# Execute patch release (1.0.3 -> 1.0.4)
./release-agent patch -m "Changelog message"

# Execute minor release (1.0.3 -> 1.1.0)
./release-agent minor -m "Added new features"

# Execute major release (1.0.3 -> 2.0.0)
./release-agent major -m "Major redesign"

# Alternative syntax via build automation script
./build/build.sh --release patch -m "Bug fixes"
```

## Multi-Tenant File Hierarchy on Distribution Server

```
/mnt/.../Obtainium/
├── dist/
│   ├── <app-a>/
│   │   ├── <app-a>-latest.apk
│   │   ├── <app-a>-latest.apk.sha256
│   │   ├── <app-a>-v1.0.0-b1-release.apk
│   │   ├── <app-a>-v1.0.0-b1-release.apk.sha256
│   │   ├── version.json
│   │   └── index.html
│   └── <future-app>/
│       ├── <future-app>-latest.apk
│       ├── version.json
│       └── index.html
├── Caddyfile
├── docker-compose.yml
└── uploader.py
```

## Obtainium Endpoints
- **Update Feed (JSON)**: `http://<SERVER_IP>:3200/<appSlug>/version.json`
- **Landing Page (HTML)**: `http://<SERVER_IP>:3200/<appSlug>/`
- **Direct Latest APK**: `http://<SERVER_IP>:3200/<appSlug>/<AppName>-latest.apk`
