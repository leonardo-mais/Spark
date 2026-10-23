# Spark! Wear OS Discovery Interview (`planning-wear-interview.md`)

> [!IMPORTANT]
> **Regola Tassativa di Attivazione**: L'AI Agent **NON DEVE MAI** sviluppare un'app Wear OS autonomamente. Questo processo di intervista e il successivo sviluppo partono **ESCLUSIVAMENTE se e quando l'utente chiede esplicitamente di sviluppare la versione per smartwatch / Wear OS**.

---

## Flusso Obbligatorio di Intervista (tramite `ask_question`)

Quando l'utente richiede lo sviluppo dell'applicazione Wear OS, prima di scrivere codice o modificare i file di build, conduci questa intervista strutturata tramite il tool `ask_question`:

### 1. Funzionalità dell'App Orologio (Dall'App Base + Proposte AI)
- Analizza le funzioni esistenti nell'app principale (`:app`) e proponi funzioni companion pensate per l'uso al polso (es. modalità lettura step-by-step a mani libere, timer con vibrazione sul polso, checklist/lista della spesa spuntabile con un tocco, controlli rapidi).
- Usa `ask_question` (`is_multi_select: true`) per far scegliere all'utente quali funzioni includere.

### 2. Grado di Semplicità dell'Interfaccia (UX al Polso)
Chiedi il livello di semplicità desiderato:
- **Ultra-Essenziale (Glance & Tap)**: 1–2 schermate immediate, testo minimo, pulsanti grandi per l'uso con mani occupate.
- **Companion Bilanciato (Consigliato)**: Liste `TransformingLazyColumn` con `EdgeButton`, 2–3 viste principali, supporto completo alla ghiera rotante (*Rotary Input*).
- **Standalone Completo**: Navigazione autonoma completa con cache locale sull'orologio anche senza smartphone vicino.

### 3. Dialogo Telefono ⇄ Orologio (Google Play Services Wearable)
Chiedi come sincronizzare telefono e smartwatch tramite `com.google.android.gms:play-services-wearable`:
- **Ibrido Completo (Consigliato)**: `DataClient` per avere liste e dati sincronizzati offline sull'orologio + `MessageClient` per azioni istantanee e timer in tempo reale.
- **Solo Sincronizzazione Dati Offline-First (`DataClient`)**: Replicazione automatica di liste e contenuti in cache locale.
- **Solo Controllo Live in Tempo Reale (`MessageClient`)**: L'orologio agisce come secondo schermo/telecomando live dell'app aperta sul telefono.

### 4. Funzioni Hardware & Sistema Wear OS
Chiedi quali integrazioni native attivare (`is_multi_select: true`):
- **Ambient Mode (Always-On Display)** tramite `LocalAmbientModeManager` per non spegnere lo schermo durante le sessioni attive.
- **Feedback Aptico & Vibrazione al Polso** allo scadere dei timer o al cambio step.
- **Tile / Complicazioni Quadrante** per accesso rapido con uno swipe.

### 5. Deploy Obtainium sullo Smartwatch (Punto 4)
Conferma il metodo di distribuzione Obtainium preferito:
- **Opzione A (Consigliata) — Obtainium Diretto sullo Smartwatch (`<appSlug>-wear/version.json`)**: Deploy automatico in `dist/<appSlug>-wear/` sul server self-hosted (Tailscale/Caddy) per aggiornamenti diretti dall'app Obtainium sull'orologio.
- **Opzione B — Sideload Assistito da Telefono**: Download dell'APK Wear sul telefono dal server Obtainium e invio all'orologio tramite *Wear Installer 2* o ADB Wireless.

### 6. Direzione Visiva & Stile Estetico: Standard vs D'Impatto (Domanda Conclusiva Obbligatoria)
Presenta sempre alla pari i due archetipi estetici di `spark-wear` (speculari a `spark-jetpack`):
- **Stile Standard / Classico (Wear Compose M3 Rigoroso)**: Scala tipografica standard di Wear M3 con Google Sans Flex, `TransformingLazyColumn` con `ListHeader`, `TitleCard` e `CheckboxButton`, superfici tonali pulite su fondo OLED `#000000`, enfasi su sobrietà e densità informativa.
- **Stile d'Impatto (Wear Expressive Dynamic)**: Tipografia dinamica a 6 assi con Google Sans Flex (`FlexHeroNumericFont` fino a 40sp con `wght` 820 e `ROND` 100% per contatori/timer giganti), indicatori circolari radiali lungo la cassa (`ArcProgressIndicator` / `SegmentedCircularProgressIndicator`), testo curvo (`curvedText`), `EdgeButton` espressivo sagomato sul fondo curvo, morphing dinamico delle card allo scorrimento (`SurfaceTransformation` + `transformedHeight`) e micro-interazioni aptiche.
