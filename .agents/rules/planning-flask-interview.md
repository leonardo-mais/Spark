# Antigravity & AI Agent Rule: Flask Webapp Planning & Discovery Interview (`planning-flask-interview.md`)

## Overview & Strict Activation Guard (Invariante Tassativa)
1. **Sviluppo Esclusivamente su Richiesta Esplicita**: L'AI Agent **NON DEVE MAI** generare codice, creare directory web o scaffoldare applicazioni Flask di propria iniziativa. Lo sviluppo di una webapp Flask parte **ESCLUSIVAMENTE se e quando l'utente lo richiede esplicitamente**.
2. **Intervista Obbligatoria Prima del Codice**: Quando l'utente richiede lo sviluppo di una webapp Flask con `spark-flask`, l'AI Agent **NON DEVE** fare assunzioni né scrivere codice prima di aver condotto un'intervista strutturata tramite il tool `ask_question` seguendo i punti di questo documento.

---

## Mandatory Flask Discovery Interview Process

Prima di formalizzare il piano o scrivere codice per l'applicazione web Flask, l'agente deve porre domande mirate (tramite box interattivi `ask_question`, una domanda alla volta con risposta consigliata) su queste 6 aree fondamentali:

### 1. Selezione delle Funzionalità & Obiettivo MVP
- Definire il problema principale risolto dalla webapp, il flusso d'uso primario e le funzioni essenziali per l'MVP (es. gestione liste/task, calendario/eventi, dashboard note o strumenti di produttività).
- Identificare l'azione rapida principale ("Create" FAB, shortcut `c`).

### 2. Strategia di Autenticazione e Controllo degli Accessi
Chiedere all'utente quale modello di autenticazione adottare:
- **Nessuna Autenticazione / Single-User (Accesso Protetto da Rete Tailscale)**: La webapp è privata e accessibile solo all'interno del proprio tailnet privato (Tailscale/Caddy). Zero schermate di login, zero complessità di token/password.
- **Autenticazione Classica Utente/Password (Flask-Login & Werkzeug)**: Form di login M3 con sessioni cifrate, hashing sicuro delle password, ideale per ambienti multiutente o server esposti.

### 3. Modello Dati, Persistenza & Volume Docker
- Definire le entità di dominio (modelli SQLAlchemy), le relazioni e i vincoli.
- Configurare la persistenza con SQLite locale montata su un volume Docker persistente (`./data:/app/data`), con supporto alle migrazioni tramite Alembic.

### 4. Layout Adattivo & Scene Multi-Pane (Google Calendar / Tasks Style)
Definire il pattern di layout dell'area di lavoro principale secondo le linee guida `adaptive`:
- **List-Detail (Stile Google Tasks)**: Lista master a sinistra e pannello di dettaglio/editing a destra su schermi medi e larghi; flusso sequenziale su schermi compatti (< 600px). Nessuna freccia back quando la vista è affiancata.
- **Supporting Pane (Stile Google Calendar)**: Vista primaria ampia (griglia calendario o tabella estesa) con pannello ausiliario collassabile a comparsa (agenda, note o widget laterali).

### 5. Scelta dell'Archetipo Tipografico & Visivo (A vs B)
Chiedere all'utente quale stile tipografico applicare all'applicazione:
- **Archetipo A — Spark Impact Flex**: Tipografia variabile dinamica a 6 assi con Google Sans Flex (`FlexHeroNumeric` fino a 72px con `wght` 820 e `ROND` 100% per metriche e contatori giganti), saluti espressivi ed elementi visivi fluidi.
- **Archetipo B — Google Standard M3**: I 15 ruoli tipografici standard di Material 3 (Display, Headline, Title, Body, Label) con Google Sans Flex, superfici pulite e massima sobrietà e densità per utility professionali.

### 6. Porta del Container Docker (Domanda Conclusiva Obbligatoria)
Come domanda finale obbligatoria prima di formulare il piano e avviare il primo ciclo di sviluppo, l'AI Agent **DEVE SEMPRE chiedere la porta host da esporre per il container Docker**:
- L'utente deve specificare la porta (es. `8080`, `3000`, `8000`, ecc.) che popolerà la variabile d'ambiente `SPARK_FLASK_PORT` nel file `docker-compose.yml` e nel `.env`.
- **Avviso importante**: Segnalare all'utente che su macOS la porta `5000` è frequentemente occupata dal servizio di sistema AirPlay Receiver, consigliando porte alternative (es. `8080` o `8000`).

---

## Execution Protocol

1. **Modalità Interattiva (`ask_question` tool)**:
   - Tutte le domande dell'intervista DEVONO essere presentate tramite il tool `ask_question` (una domanda alla volta).
2. **Aggiornamento Specifiche / Piano**:
   - Una volta completata l'intervista, documentare l'architettura della webapp in un piano prima di avviare il ciclo di sviluppo iterativo (`iterative-development-loop.md`).

