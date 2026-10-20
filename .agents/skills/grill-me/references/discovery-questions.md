# Discovery Question Bank

Use this file with the `grill-me` skill. Ask one question at a time, in the order below. Each question needs a recommended answer. Mark each decision as `DECIDED` or `OPEN` in the decision log.

## 1. Vision and core objective

- [ ] **Core problem:** Which main problem or friction point does the app solve for the user?
- [ ] **Value proposition:** How is it better than the current alternatives (system reminders, spreadsheets, existing apps, pen and paper)?
- [ ] **Usage context:** Where, when and in which situations will the user open the app?

## 2. Scope and MVP

- [ ] **MVP:** What is the single essential action without which the app does not make sense?
- [ ] **Core entities:** Which main entities does the app manage (for example items, tasks, categories), and which attributes does each one need?
- [ ] **Business logic:** Are the flows linear and static (fixed steps, simple checklists) or dynamic (dependencies, conditional rules, automations)?
- [ ] **History:** Must the app keep a history of actions, notes, or aggregate statistics?
- [ ] **System integrations:** Does it need notifications, alarms, home-screen widgets, or hardware such as the camera, sensors or local files?
- [ ] **Secondary features:** Which items are welcome but not required for the first release (export, backup and restore, advanced filters, theme options)?

## 3. Data model and information lifecycle

- [ ] **Data source:** Does the user create all content, or must the app ship static catalogs, templates or predefined assets?
- [ ] **Persistence:** Must the app work fully offline with local storage, or does it need cloud sync across devices? (Default for this template: offline-first with Room.)
- [ ] **Lifecycle:** How are records archived, updated, completed and permanently deleted? Is deletion reversible?

## 4. UX, interface and flows

- [ ] **Structure:** How is the main data displayed (flat list, grid, board, grouped by tags or spaces)?
- [ ] **Interaction frequency:** Should the UI favor very fast entry (seconds per action) or rich review and exploration?
- [ ] **Density and style:** Ultra-minimal and dense, or expressive with rich visuals and animated feedback?
- [ ] **Errors and edge cases:** Which mistakes are likely in critical flows, and how should the UI guide the user or prevent data loss?
- [ ] **Search:** Is search a primary feature? If yes, it must sit in the first tab with a full-screen search view.

## 5. Name and icon

- [ ] **Name:** Propose 3 distinct names verified against existing apps and brands, plus a custom option.
- [ ] **Icon:** Propose the Material Symbol that best fits the domain (Filled, FILL = 1). No emoji.

## 6. Visual archetype (mandatory last question)

- [ ] **Archetype:** Classic (strict M3 Expressive, dense and sober) or Impact (dynamic typography, hero cards, Floating Pill Dock with at most 4 items). Present both with their ideal contexts and let the user choose. Never decide for the user.
