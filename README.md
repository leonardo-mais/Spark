# ✨ Spark! • Modern Android Starter Template & Framework

Spark! is a starter template and framework for building Android apps quickly, without writing the boilerplate by hand.

It is built on **Jetpack Compose** and uses **Material 3 Expressive**, Google's latest design system, to make your app dynamic and modern. You decide how far to push the expressive style.

The release pipeline is designed for a **self-hosted server** that feeds **Obtainium**, so users can install and update the app in one tap.

The repository works with any AI coding agent. Rules and skills live in `.agents/`, and [`AGENTS.md`](./AGENTS.md) is the entry point for every agent.

---

## Create a new app with Spark!

### Option 1: GitHub template (recommended)

1. Click **"Use this template"** → **"Create a new repository"**.
2. Clone the new repository and open it in your IDE and in your AI agent.
3. Ask the agent to plan your app, for example: *"Plan a new app for tracking my reading."*
4. The agent runs the discovery interview (`.agents/skills/grill-me`), asks one question at a time, and ends with the choice between the **Classic** and **Impact** visual styles.
5. The agent writes `specifications.md` and a milestone plan (`.agents/skills/plan`), then waits for your approval.
6. After approval, the agent builds the app milestone by milestone, verifying each one.

---

## What the template contains

| Folder or file | What it is | Keep in new apps? |
|---|---|:---:|
| `AGENTS.md` | Entry point for every AI agent | **Yes** |
| `.agents/rules/` | Non-negotiable rules: architecture, design, localization, release | **Yes** |
| `.agents/skills/spark-jetpack/` | Material 3 Expressive design system: tokens, typography, shapes, components | **Yes** |
| `.agents/skills/grill-me/` | Discovery interview method | **Yes** |
| `.agents/skills/plan/` | Planning method and specification template | **Yes** |
| `.agents/skills/agent-builder/` | Release automation, in skill form | **Yes** |
| `.agents/skills/` (other folders) | Official Google Android skills: Navigation 3, Compose performance, adaptive layouts and more. See [`ATTRIBUTION.md`](./.agents/skills/ATTRIBUTION.md) | **Yes** |
| `scripts/release.sh` and `release-agent` | Versioning, tests, signing and distribution | **Yes** |
| `app/src/main/res/font/google_sans_flex.ttf` | Variable font bundled for offline use | **Yes** |
| `app/src/main/java/it/spark/app/presentation/theme/Type.kt` | `flexFont` engine and `ExpressiveType` styles | **Yes** |
| `app/src/main/java/it/spark/app/presentation/common/` | Shared components (`MediumFAB`, `CircularBackButton`, `GroupedCard`) | **Yes** |
| `app/src/main/res/values*/strings.xml` | Localized strings (English default, Italian example) | **Yes** |
| `.env` (created from `.env.example`) | Server and signing settings. Never committed | Create locally |
| `version.properties` | App name, slug and version | Reset for a new app |
| `app/src/main/java/.../presentation/home/` and `settings/` | Example screens | Replace with your own |
| `specifications.md` | Specification of the app being built | Rewrite for each app |

---

## Languages

- The default language is **English**: `app/src/main/res/values/strings.xml`.
- Italian is included as an example translation: `app/src/main/res/values-it/strings.xml`.
- To add a language, create `app/src/main/res/values-<code>/strings.xml` with the same keys. Android picks it up from the device language.
- Every user-visible text uses string resources. Do not hardcode text in composables.

---

## Release and self-hosted distribution (Obtainium)

The repository includes a release agent (`./release-agent`, which runs `./scripts/release.sh`). It handles semantic versioning, unit tests, signing, and upload to your server.

```bash
# Patch release (for example 1.0.0 -> 1.0.1)
./release-agent patch -m "Release description"

# Minor release (for example 1.0.1 -> 1.1.0)
./release-agent minor -m "New features"

# Major release (for example 1.1.0 -> 2.0.0)
./release-agent major -m "New architecture"
```

Outputs:

- `apk/`: the signed APK for local use, named `<AppName>-v<version>-b<build>-release.apk`.
- `dist/<appSlug>/`: the Obtainium feed `version.json`, the landing page `index.html`, and the APK files with their checksums.
- Remote server: when `.env` is configured, the release is uploaded over HTTP.

The full setup, including the server configuration, is in [`RELEASE.md`](./RELEASE.md).

---

## Build and test

```bash
# Unit tests
./gradlew testDebugUnitTest

# Debug build
./gradlew assembleDebug

# Signed release build
./gradlew assembleRelease
```

---

## License

Apache License 2.0. See [`LICENSE`](./LICENSE).
