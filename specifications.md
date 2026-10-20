# Specifications: Spark Application Blueprint

> [!NOTE]
> This document is the single source of truth (SSOT) for the architecture and the functions of the app built with **Spark**.
> The agent writes it after the discovery interview is confirmed (`.agents/skills/grill-me`), and plans it with the `plan` skill (`.agents/skills/plan`). The blank template is in `.agents/skills/plan/references/specifications-template.md`.

---

## 1. Vision & Architecture Overview

- **App name:** Spark App (to be defined during discovery)
- **App slug:** `spark-app`
- **Core objective:** solve the user's main problem with a native, offline-first experience.
- **Architecture pattern:** single `:app` module, layered by package (`presentation`, `domain`, `data`), with MVI (Model-View-Intent) and a unidirectional data flow (UDF).
- **Design system:** Material 3 Expressive (`@OptIn(ExperimentalMaterial3ExpressiveApi::class)`) with Google Sans Flex.
- **Persistence:** Room DAOs exposed as `Flow<T>`, the local single source of truth.

---

## 2. Navigation & Screen Hierarchy

- **Navigation:** multi-scaffold architecture. The root scaffold hosts only the navigation bar and the snackbar host.
- **Main screens:**
  - Home / dashboard screen
  - Detail and editor screens
  - Settings screen, mandatory, with the "Built with Spark!" section

---

## 3. Data Model & Domain Entities

*(Defined during the planning session.)*

---

## 4. System Integrations & Permissions

- Permissions are requested only in response to a user action, at the moment they are needed (runtime permissions).
- Local notifications are supported when the app needs them. Notifications are requested with `POST_NOTIFICATIONS`.
- The app manages its lifecycle with state saving (`rememberSaveable`, `SavedStateHandle`), so input survives rotation and process death.

---

## 5. Acceptance Criteria & Iterative Verification

1. The build completes with no errors and no warnings: `./gradlew assembleDebug`.
2. The preliminary unit tests pass: `./gradlew testDebugUnitTest`.
3. The versioned APK is in `apk/`, with its SHA-256 checksum, and is published with `./release-agent` when a release is requested.
