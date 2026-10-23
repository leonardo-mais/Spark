# Rule: Discovery Interview Before Planning

## Overview

Whenever the user starts planning a new app, a new screen, or a significant feature, the agent MUST run a structured **discovery interview** before writing the plan or any code. The agent never plans on assumptions.

The method is described in the `grill-me` skill (`.agents/skills/grill-me/SKILL.md`). The questions are in `.agents/skills/grill-me/references/discovery-questions.md`. The plan is written with the `plan` skill (`.agents/skills/plan/SKILL.md`).

---

## Mandatory

1. **Run the interview first.** Use the `grill-me` skill for:
   - a new application;
   - a new screen, or a feature that changes data or navigation;
   - any request that is ambiguous or incomplete.
2. **Cover the seven areas:** vision and objective, scope and MVP, data model and lifecycle, UX and flows, name and icon, visual archetype, and distribution channel.
3. **Visual archetype and distribution channel last:**
   - The Classic or Impact choice is presented first among the final steps, presenting both options equally.
   - **Distribution target question (mandatory last question before planning/build):** The agent MUST ask the user how the app will be distributed/retrieved with Obtainium:
     - Option A: Via GitHub Releases (public or private repository).
     - Option B: Via a self-hosted distribution server (Caddy/Tailscale HTTP endpoint).
     - In both cases, the agent provides the exact Obtainium link/feed URL to add to Obtainium.
4. **One question at a time.** Each question has a recommended answer. Use selectable options when the host supports them, and always allow a free-text answer.
5. **No code during the interview.** The agent writes no application code until the plan is approved.
6. **Hand over to the plan.** The confirmed decision log feeds the `plan` skill, which writes `specifications.md` and the milestone plan. The user approves the plan explicitly.

## Exceptions

- Small fixes that change no requirement (a typo, a style adjustment, a bug with a clear reproduction) skip the interview.
- If the user explicitly asks for a quick pass, run the short mode of `grill-me` and list each default for confirmation.
