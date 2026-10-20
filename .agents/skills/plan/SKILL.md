---
name: plan
description: Planning workflow for a new app, screen or significant feature in the Spark project. Turns the decisions from the grill-me discovery interview into specifications.md and a milestone-based implementation plan, then waits for explicit approval before any code is written. Use when the user asks to plan, design or scope something (for example "/plan", or the same request in any language).
---

# Plan

## Purpose

Turn confirmed requirements into a written, verifiable plan. Nothing is implemented until the user approves the plan. The plan is the contract for the iterative development loop (`.agents/rules/iterative-development-loop.md`).

## Prerequisite: the discovery interview is complete

Planning starts only when the decision log from the `grill-me` skill is confirmed by the user. If it is not, run `grill-me` first. Never plan on assumptions. If a decision is still `OPEN`, either ask it or record it under "Open questions" and do not build on it.

## Process

1. **Recap the decisions.** Summarize: problem, MVP, entities and attributes, flows, integrations, persistence, visual archetype, name and icon, and what is out of scope.
2. **Check consistency.** Look for contradictions (for example "fully offline" plus "live sync"), missing permissions, and gaps in the data lifecycle. Resolve them with `grill-me`, do not assume.
3. **Write `specifications.md`** at the repository root, using `references/specifications-template.md`. Give every requirement an ID (`R1`, `R2`, ...) and a testable acceptance criterion.
4. **Write the implementation plan** as milestones (`M1`, `M2`, ...). Keep each milestone small enough to verify in one loop. For each milestone, list:
   - scope, and the requirement IDs it covers;
   - the files or packages it touches;
   - acceptance criteria;
   - the verification commands (see the verification list below);
   - its dependencies on earlier milestones.
5. **List risks and open questions.** Include anything that could block a milestone.
6. **Present the plan and ask for approval.** Use one clear question, for example "Approve this plan, or what should change?" Do not start coding before an explicit approval. If the user asks for changes, apply them, present the revised plan, and ask again.

## Milestone order for this template

1. Domain and data (pure Kotlin models, repositories, Room DAOs), when the app needs them.
2. Theme and design system (colors, typography, shapes), using the `spark-jetpack` skill.
3. Navigation and the root scaffold (routes as `NavKey` objects, navigation bar).
4. Screens, one milestone per main screen, each with the four UI states (loading, empty, content, error).
5. Settings screen with the mandatory "Built with Spark!" section. Include it in the first UI milestone.
6. Release wiring (`version.properties`, release script), only when the user asks for a release.

## Verification commands

Every milestone lists the checks it must pass:

- `./gradlew testDebugUnitTest` for unit tests.
- `./gradlew assembleDebug` for a debug build without warnings.
- Visual check on an emulator or device, with screenshots of the changed screens.
- Specific checks for the milestone (for example a permission flow, a notification, or an intent).

## Quality checklist for a plan

- Every requirement in `specifications.md` maps to at least one milestone and one acceptance criterion.
- Every milestone has at least one verification step.
- Nothing outside the MVP is included unless it is marked "later".
- The UI follows the chosen archetype (Classic or Impact).
- The navigation bar has at most 4 items in the Impact style, with no fallback.
- Each screen plans its loading, empty, content and error states.
- Permissions are requested only in response to a user action, with a fallback when refused.
- Persistence and offline behavior are stated explicitly.
- No emoji in any planned UI text or icon.
- Text that the user sees is planned as string resources (English default, plus any locales requested).

## Output format

Use these sections, in this order:

1. **Summary:** two or three sentences on what will be built.
2. **Decisions:** the decision table (ID, topic, decision).
3. **Milestones:** the list described above.
4. **Verification:** the checks for each milestone.
5. **Risks and open questions.**
6. **Approval request:** one question.

## After approval

Run the iterative loop from `.agents/rules/iterative-development-loop.md`: implement one milestone at a time, verify it, and check it against `specifications.md`. The loop never exceeds 5 cycles. Deliver the versioned APK only when the release rules ask for it.
