---
trigger: always_on
---

# Antigravity & AI Agent Rule: Planning Mode Discovery Interview

## Overview
This rule mandates that whenever the user enters **Planning Mode**, proposes creating a new app, screen, or major feature, the AI Agent must **NOT** make assumptions or jump directly into implementation without first conducting a structured **Discovery Interview**.

The goal is to gather functional, logical, and experiential requirements before finalizing the implementation plan or writing code.

---

## Mandatory Interview Process

Before generating the final implementation plan or writing code for a new application or significant feature, the agent must ask targeted clarifying questions across these key areas:

### 1. Vision & Core Objective
- **Core Problem:** What primary problem or friction point (*pain point*) does the app aim to solve for the user?
- **Value Proposition:** How does the proposed solution improve upon current alternatives (e.g., system reminders, spreadsheets, competitor apps, pen and paper)?
- **Usage Context:** In what situations, locations, or times of day will the user predominantly engage with the application?

### 2. Scope & Core Features (MVP)
- **Minimum Viable Product (MVP):** What is the single essential action or feature without which the application cannot exist?
- **Core Data Handling:** What primary entities does the app manage (e.g., items, tasks, categories), and what specific attributes do they require?
- **Business Logic Complexity:**
  - Are the flows linear and static (e.g., fixed intervals, simple checklists) or dynamic (e.g., dependencies, conditional rules, automations)?
  - Is it necessary to track action history, retroactive notes, or aggregate statistics?
- **System Integrations:** Are system-level capabilities required, such as push/local notifications, alarms, home screen widgets, or device hardware access (camera, sensors, local file storage)?
- **Secondary Features:** Which aspects (e.g., data export, backup/restore, advanced filtering, dark mode) are desirable but non-essential for the initial release?

### 3. Data Model & Information Lifecycle
- **Data Sourcing:** Is content generated entirely by the user from scratch, or must the app preload static catalogs, templates, or predefined assets?
- **Persistence Strategy:** Must the app function entirely offline with local storage, or does it require cloud synchronization across multiple devices?
- **Lifecycle Rules:** How are archiving, updating, completing, or permanently deleting records handled?

### 4. User Experience, Interface & Flows (UX/UI)
- **Structure & Hierarchy:** How should primary data be structured and displayed (e.g., flat list, grid, board, categorized by tags/spaces)?
- **Interaction Frequency:** Should the UI optimize for ultra-fast data entry (*quick actions* taking seconds) or rich, detailed review and exploration?
- **Visual Style & Density:** Is the target an ultra-minimal, high-density schematic layout, or an expressive interface rich in visual assets, icons, and animated feedback?
- **Error Handling & Edge Cases:** What common mistakes might a user make during critical flows, and how should the interface guide them or prevent data loss?

### 5. Name of the app and icon
- **Name Uniqueness & Competitor Check:**
  - Before presenting proposals, verify via web search that none of the proposed names conflict with or already exist on similar, competitor, or popular apps on the market/Play Store.
  - Ask the user the name of the app by presenting 3 verified, distinct proposals along with the option to type a custom name.
- **Icon:** Without prompting the user, search Material symbols / icons for the best semantic and aesthetic match for the chosen app domain.

### 6. Direzione Visiva & Stile Estetico: Classica vs D'Impatto (Domanda Conclusiva Obbligatoria)
Come domanda finale del processo di Discovery prima di formalizzare `specifications.md`, l'AI Agent **DEVE SEMPRE** formulare tramite il tool `ask_question` la scelta dell'archetipo estetico, presentando entrambe le opzioni alla pari con i rispettivi contesti d'uso ideali:
- **Stile Classico (Material 3 Expressive Rigoroso)**: Basato sui 15 ruoli tipografici standard di Google Sans Flex (`display`, `headline`, `title`, `body`, `label`), superfici tonali bilanciate e card raggruppate standard con divisori discreti (`outlineVariant`). Ideale per app utility, gestionali complessi, strumenti enterprise, schermate dense di form e contesti in cui la sobrietà e l'alta densità informativa sono prioritarie.
- **Stile d'Impatto (Expressive Dynamic)**: Tipografia dinamica a 6 assi con Google Sans Flex (`wght` 400–880, `wdth` 100–115%, `ROND` 0–100% per terminali a pillola organici, `GRAD`), Bento Hero Cards con numeri giganti (fino a 48sp con `FlexHeroNumericFont`), Quick Metrics Bento Strips a 3 colonne, saluti orari contestuali ("Buongiorno", "Buon pomeriggio", "Buonasera"), micro-pill tags dedicate con icone per orari e categorie, micro-interazioni spring animate (`scale(0.92f)`). Ideale per consumer apps, personal tracking, salute & benessere, lifestyle, habit tracker e dashboard con forte effetto WOW visivo.

---

## Execution Protocol

1. **Interactive Interview via Modal Boxes (`ask_question` tool):**
   - The AI Agent **MUST ALWAYS** use the `ask_question` tool to present clarifying questions as interactive modal boxes with selectable options and custom write-in capability. Never ask discovery questions solely as raw markdown text in chat.
   - Configure clear multiple-choice options with `is_multi_select` when applicable, allowing the user to select answers directly or type in their custom response.
2. **Recommend `/grill-me`:**
   - Suggest the `/grill-me` command if the user prefers an iterative, deep-dive clarification session.
3. **Formalize Detailed Specifications (`specifications.md`):**
   - Once the interview is complete, formalize all gathered requirements, flows, data models, UI contracts, and system integrations into a comprehensive specifications document (`specifications.md`).
   - This document acts as the immutable single source of truth for the subsequent **Iterative Development Loop** (`iterative-development-loop.md`).