---
trigger: always_on
---

# Antigravity & AI Agent Rule: Wear OS Planning & Discovery Interview (`planning-wear-interview.md`)

## Overview & Strict Activation Guard (Invariante Tassativa)
1. **Sviluppo Esclusivamente su Richiesta Esplicita**: L'AI Agent **NON DEVE MAI** sviluppare, generare codice o creare il modulo `:wear` di propria iniziativa. Lo sviluppo di un'applicazione Wear OS parte **ESCLUSIVAMENTE se e quando l'utente chiede esplicitamente di svilupparla**.
2. **Intervista Obbligatoria Prima del Codice**: Quando l'utente richiede lo sviluppo dell'app per smartwatch (Wear OS), l'AI Agent **NON DEVE** fare assunzioni né scrivere codice prima di aver condotto un'intervista strutturata tramite il tool `ask_question` seguendo i punti di questo documento.

---

## Mandatory Wear OS Discovery Interview Process

Prima di formalizzare il piano o scrivere codice per il modulo `:wear`, l'agente deve porre domande mirate (tramite box interattivi `ask_question`) su queste 6 aree fondamentali:

### 1. Selezione delle Funzionalità (Dall'App Base + Proposte AI)
- **Analisi App Base + Proposte AI**: L'agente analizza le funzionalità presenti nell'applicazione smartphone principale (`:app`) e formula proattivamente proposte di funzioni native da polso (es. modalità passo-passo a mani libere, timer con vibrazione al polso, checklist/lista della spesa spuntabile con un tap, comandi rapidi).
- **Domanda (`is_multi_select: true`)**: Chiedere all'utente quali funzioni dell'app base portare sull'orologio e quali funzioni companion proposte dall'AI includere nell'MVP.

### 2. Grado di Semplicità dell'Interfaccia e Profondità dei Flussi
Chiedere all'utente il livello di semplicità e immediatezza desiderato per l'esperienza al polso:
- **Ultra-Essenziale (Glance & Single-Tap)**: 1–2 schermate rapide, testo ridotto al minimo indispensabile, pulsanti grandi per l'uso con mani occupate o in movimento (zero distrazioni).
- **Companion Bilanciato (Consigliato)**: Liste verticali adattive (`TransformingLazyColumn`), `EdgeButton` sul fondo curvo, navigazione tra 2–3 sezioni principali con supporto completo alla corona rotante (*Rotary Input*).
- **Autonomo Completo (Standalone-Ready)**: Navigazione completa delle categorie/elementi salvati in cache locale sull'orologio, utilizzabile anche con telefono distante o disconnesso.

### 3. Dialogo Telefono ⇄ Orologio (Google Play Services Wearable Data Layer)
Definire come comunicano `:app` (telefono) e `:wear` (smartwatch) tramite la libreria ufficiale `com.google.android.gms:play-services-wearable`:
- **Sincronizzazione Offline-First (`DataClient` + Cache Locale)**: I dati (es. liste, ricette, stato) vengono replicati automaticamente sull'orologio e restano consultabili/modificabili anche offline, sincronizzandosi appena i dispositivi si riconnettono.
- **Controllo Live in Tempo Reale (`MessageClient`)**: L'orologio agisce in simbiosi istantanea con la schermata aperta sul telefono (es. avvio automatico della modalità attiva sul polso, sincronizzazione live di step e timer).
- **Ibrido Completo (Consigliato)**: `DataClient` per avere i dati sempre disponibili offline sul polso + `MessageClient` per azioni istantanee e sincronizzazione live dei timer/sessioni attive.

### 4. Integrazioni Native Wear OS (Ambient Mode, Tile, Complicazioni, Haptics)
Chiedere quali capacità hardware/sistema di Wear OS attivare (`is_multi_select: true`):
- **Ambient Mode (Always-On Display)**: Mantiene la schermata attiva a basso consumo (`LocalAmbientModeManager`) durante le sessioni attive (es. mentre si cucina o si consulta una lista) senza tornare al quadrante.
- **Feedback Aptico e Sonoro al Polso**: Vibrazione e allarme diretto sullo smartwatch allo scadere dei timer o al completamento delle azioni.
- **Wear OS Tile / Complicazioni**: Accesso rapido con uno swipe dal quadrante principale.

### 5. Strategia di Deploy su Obtainium (Punto 4)
Confermare la modalità di distribuzione continua verso lo smartwatch:
- **Opzione A (Consigliata) — Obtainium Diretto sullo Smartwatch (`<appSlug>-wear/version.json`)**: La pipeline `./release-agent --wear` pubblica l'APK Wear isolato in `dist/<appSlug>-wear/` con il proprio feed `version.json` e landing page sul server self-hosted (Tailscale/Caddy), permettendo a Obtainium sull'orologio di aggiornare l'app autonomamente.
- **Opzione B — Sideload Assistito da Smartphone**: Pubblicazione dell'APK Wear sul server + invio dallo smartphone all'orologio tramite *Wear Installer 2* o ADB Wireless.

### 6. Direzione Visiva & Stile Estetico Wear OS: Standard vs D'Impatto (Domanda Conclusiva Obbligatoria)
Come domanda finale obbligatoria prima di aggiornare `specifications.md` e iniziare lo sviluppo, l'AI Agent **DEVE SEMPRE** far scegliere tramite `ask_question` l'archetipo estetico per lo smartwatch (in parallelo a quanto avviene con `spark-jetpack`):
- **Stile Standard / Classico (Wear Compose Material 3 Rigoroso)**: Basato sui ruoli tipografici standard di Wear M3 con Google Sans Flex, `TransformingLazyColumn` strutturata con `ListHeader`, `TitleCard`, `Button` e `SplitCheckboxButton`, superfici tonali pulite su fondo OLED nero (`#000000`) e focus sulla massima densità di lettura e sobrietà.
- **Stile d'Impatto (Wear Expressive Dynamic)**: Tipografia dinamica a 6 assi con Google Sans Flex (`FlexHeroNumericFont` fino a 40sp con `wght` 820 e `ROND` 100% per timer e metriche giganti al centro del quadrante), indicatori circolari radiali lungo la lunetta (`ArcProgressIndicator` / `SegmentedCircularProgressIndicator`), testo curvo (`curvedText` / `TimeText`), `EdgeButton` espressivo sagomato sul bordo inferiore dello schermo tondo, morphing geometrico delle card allo scorrimento (`SurfaceTransformation` + `transformedHeight`), `HorizontalPagerScaffold` con fisica a molla (`spring`) e micro-interazioni aptiche sulla corona rotante.

---

## Execution Protocol

1. **Modalità Interattiva (`ask_question` tool)**:
   - Tutte le domande dell'intervista Wear OS DEVONO essere presentate tramite il tool `ask_question` (mai come semplice elenco testuale in chat).
2. **Aggiornamento Specifiche (`specifications.md`)**:
   - Una volta completata l'intervista, documentare l'architettura `:wear`, il protocollo Data Layer (path `/sync/...`, `/action/...`) e i requisiti UI in `specifications.md` prima di avviare il ciclo di sviluppo iterativo (`iterative-development-loop.md`).
