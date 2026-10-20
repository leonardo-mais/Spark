# Spark: Instructions for AI Agents

This file is the entry point for any AI coding agent that works in this repository. It is tool-neutral: follow it whatever the model or the IDE.

## What this project is

Spark is an Android starter template for building native apps with Jetpack Compose and Material 3 Expressive. It includes Hilt, Room, an offline-first data approach, a settings screen, and a self-hosted release pipeline that feeds Obtainium.

- Single Gradle module: `:app`, package `it.spark.app`.
- Versions: `version.properties` is the single source of truth.
- Release: `./release-agent` (or `scripts/release.sh`) builds, signs, and publishes to `dist/<appSlug>/`.

## Workflow for a new app or a significant feature

Follow these steps in order. For small fixes that change no requirement, skip steps 1 and 2 and say so.

1. **Discovery interview.** Use the `grill-me` skill (`.agents/skills/grill-me/SKILL.md`). It is mandatory before planning. Output: a confirmed decision log.
2. **Plan.** Use the `plan` skill (`.agents/skills/plan/SKILL.md`). Output: `specifications.md` and a milestone plan. Do not write app code until the user explicitly approves the plan.
3. **Build.** Run the iterative loop in `.agents/rules/iterative-development-loop.md`: one milestone per cycle, verify, check against the specification. Never exceed 5 cycles.
4. **Release.** Run `./release-agent` as described in `.agents/rules/agent-builder.md`, only when a release is requested.

## Where things live

| Path | Content |
|---|---|
| `.agents/rules/` | Non-negotiable rules. Read them before writing code. |
| `.agents/rules/development-standards.md` | Architecture, design system, performance, accessibility, localization, build. |
| `.agents/rules/planning-interview.md` | When and why the discovery interview is mandatory. |
| `.agents/rules/iterative-development-loop.md` | Implementation, verification and delta-fix cycle. |
| `.agents/rules/agent-builder.md` | Release pipeline and distribution rules. |
| `.agents/skills/` | Skills, one folder per skill, each with a `SKILL.md`. |
| `.agents/skills/spark-jetpack/` | Material 3 Expressive design system: tokens, typography, shapes, components. |
| `.agents/skills/grill-me/`, `.agents/skills/plan/` | Discovery interview and planning methods. |
| `.agents/skills/agent-builder/` | Release automation, in skill form. |
| `.agents/skills/<official Android skills>/` | Official Google Android skills (Navigation 3, Compose performance, adaptive layouts, and others). See `ATTRIBUTION.md`. |
| `specifications.md` | Specification of the app being built (the single source of truth for requirements). |
| `app/` | Android application module. |
| `scripts/release.sh`, `release-agent` | Release script and its entry point. |

## Priority when guidance conflicts

1. Official Google Android skills in `.agents/skills/`.
2. `spark-jetpack` (design system).
3. `.agents/rules/development-standards.md` (project rules).

## Non-negotiables (summary; the rules have the details)

- Material 3 Expressive only. No Material 2 components. No hardcoded colors. No shadows or tonal elevation for hierarchy.
- No emoji anywhere in the app: UI, resources, notifications, empty states.
- Every piece of user-visible text comes from string resources. English is the default in `values/strings.xml`. Other languages go in `values-<code>/strings.xml`.
- Secrets are never written to the repository, to scripts, or to logs. They come from environment variables or a local `.env` file, which is git-ignored.
- Release builds fail fast when signing secrets are missing. Never fall back to real values.
- Release artifacts go only to `apk/` and `dist/<appSlug>/`.

## Language

- Repository files (rules, skills, documentation, code comments, commit messages) are written in English.
- Talk with the user in the language the user writes in.

## Tooling notes

- Gradle: `./gradlew testDebugUnitTest`, `./gradlew assembleDebug`, `./gradlew assembleRelease`.
- On Windows, run the shell scripts from Git Bash.
- Some agents offer extra features such as slash commands or question widgets. They are optional. The workflow above works without them: ask the user directly, with numbered options when there are choices.
