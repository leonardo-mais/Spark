# Specifications: <App name>

> Single source of truth for the application. Written by the agent after the `grill-me` interview is confirmed, and used by the `plan` skill and the iterative loop. Every requirement has an ID and a testable acceptance criterion.

## 1. Vision and architecture

- **App name:** <name confirmed by the user>
- **App slug:** <slug, used for `dist/<slug>/`>
- **Core objective:** <one sentence on the user problem>
- **Architecture:** single `:app` module, layered by package (`presentation`, `domain`, `data`). Modular split only when a feature grows. See `.agents/rules/development-standards.md`.
- **Design system:** Material 3 Expressive, Google Sans Flex, `MaterialExpressiveTheme`.
- **Visual archetype:** <Classic | Impact>
- **Persistence:** <offline-first with Room | cloud sync | other>

## 2. Navigation and screen hierarchy

- **Root scaffold:** bottom navigation bar and snackbar host only.
- **Primary screens (max 4 in the Impact style):**
  - <Screen name>: <purpose>
- **Secondary screens:** <list, each with its parent>
- **Routes:** `@Serializable` objects or classes that implement `NavKey`.

## 3. Data model and domain entities

| Entity | Attributes | Lifecycle (create, update, archive, delete) | Source (user, seed, catalog) |
|---|---|---|---|
| <Entity> | <attributes> | <rules> | <source> |

## 4. System integrations and permissions

- **Notifications:** <none | local | push>, requested only with a user action (`POST_NOTIFICATIONS`).
- **Other permissions:** <list, each with its user action and fallback>.
- **Files and media:** the Android Photo Picker only, with no broad storage permissions.

## 5. Functional requirements

| ID | Requirement | Acceptance criterion (testable) | Screen |
|---|---|---|---|
| R1 | <requirement> | <criterion> | <screen> |

## 6. Non-functional requirements

- **Accessibility:** touch targets of at least 48dp, contrast of at least 4.5:1 for text (3:1 for large text and icons), TalkBack labels from string resources.
- **Localization:** English default in `values/strings.xml`. Additional locales in `values-<code>/strings.xml`, selected by the device language.
- **Resilience:** each screen handles loading, empty, content and error states. Input survives rotation and process death (`rememberSaveable` or `SavedStateHandle`).
- **Performance:** stable keys in lazy lists, animations within the snappy timings of the `spark-jetpack` skill.

## 7. Acceptance and verification

1. `./gradlew testDebugUnitTest` passes.
2. `./gradlew assembleDebug` builds without warnings.
3. Every requirement in section 5 has passing evidence (test or visual check).
4. Versioned APK in `apk/` with its SHA-256, only when a release is requested.

## 8. Out of scope

- <item>

## 9. Open questions

- <question, with the decision owner>
