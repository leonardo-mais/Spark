---
name: grill-me
description: Discovery interview technique for the Spark project. Interviews the user one question at a time, each with a recommended answer, walks every branch of the decision tree and resolves dependencies first, until every decision is explicit. Use before planning any new app, screen or significant feature, and whenever the user asks to be interviewed, quizzed or "grilled" (for example "/grill-me", or the same request in any language).
---

# Grill Me: Discovery Interview

## Purpose

Make the user's requirements explicit before anything is planned or coded. The interview is mandatory before planning a new app or a significant feature. The agent must not guess requirements, and must not start implementation during the interview.

The interview produces a **decision log**. The `plan` skill turns that log into `specifications.md` and an implementation plan.

## Rules of the interview

1. **One question at a time.** Never send a wall of questions.
2. **Every question has a recommended answer**, with a one-line reason. The user can accept it, change it, or write their own answer.
3. **Offer choices when the host supports them.** Use selectable options (with a free-text option) when the tool allows it. Otherwise use a numbered list the user can answer with a number. Always allow a free-text answer.
4. **Walk the decision tree.** Start from the core problem. Resolve first the decisions that others depend on: persistence before the data model, the visual archetype before UI details, scope before screens.
5. **Do not accept vague answers.** For "make it nice" or "simple", ask for a concrete example or a measurable criterion before moving on.
6. **Challenge contradictions and scope creep politely.** Example: "This conflicts with the offline-only decision. Which one wins?"
7. **Keep a running decision log.** Columns: ID, topic, decision, status (`DECIDED` or `OPEN`). Show the log every 5 to 7 decisions and again at the end.
8. **Stop when every area in `references/discovery-questions.md` is `DECIDED` or explicitly deferred**, and the user confirms the log. Then hand over to the `plan` skill.
9. **Language.** Talk with the user in the user's language. Write the decision log and the specifications in English, which is the project convention, unless the user asks otherwise.

## Order of topics

Follow `references/discovery-questions.md` in this order:

1. Vision and core objective
2. Scope and MVP
3. Data model and information lifecycle
4. UX, interface and flows
5. Name and icon
6. Visual archetype
7. Distribution & Obtainium setup (the mandatory last question before plan & first build)

## Name and icon

- Before proposing names, check with a web search that none of them conflicts with an existing app on the Play Store or a well-known brand. Report what you checked.
- Propose 3 distinct, verified names, and always allow a custom name.
- For the icon, look for the Material Symbol that best matches the app's domain, with FILL active. Never use emoji.

## Visual archetype

Present both archetypes side by side, with their ideal contexts (the full comparison is in the `spark-jetpack` skill, section 21):

- **Classic style:** strict Material 3 Expressive, 15 standard roles, standard `NavigationBar`, dense and sober. Best for utility, enterprise and form-heavy apps.
- **Impact style:** dynamic 6-axis typography, hero cards with large numbers, Floating Pill Dock with at most 4 items, expressive greetings. Best for consumer, wellness and habit-tracking apps.

Never choose the archetype for the user. Record the answer in the log.

## Distribution & Obtainium configuration (last question, mandatory before first build)

As the final question before finalizing the plan and initiating the first build, always ask the user via `ask_question` their preferred Obtainium distribution strategy:
- **Private/Public GitHub Repository**:
  - The app's release APKs and releases are hosted directly on GitHub Releases.
  - Return the exact link format for Obtainium (e.g. `https://github.com/<owner>/<repo>/releases` or `https://github.com/<owner>/<repo>` with GitHub PAT for private repos).
- **Self-Hosted Distribution Server (Tailscale / HTTP / Caddy)**:
  - The app uses the Spark release pipeline (`./release-agent`) uploading to a self-hosted endpoint.
  - Ask for the base server URL/IP (or use the one configured in `.env`) and return the exact Obtainium feed URL: `http://<SERVER_IP>:<PORT>/<appSlug>/version.json` (or landing page `http://<SERVER_IP>:<PORT>/<appSlug>/`).

## Short mode

Use short mode only if the user explicitly asks for a faster pass. Then ask only the MVP and archetype questions, and list every other decision as a proposed default that the user must confirm in one reply.

## Exit

1. Show the final decision log, with any `OPEN` items.
2. Ask the user to confirm the log, or to correct any entry.
3. Continue with the `plan` skill.
