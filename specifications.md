# Specifications: Spark Application Blueprint

> [!NOTE]
> Questo documento rappresenta il Single Source of Truth (SSOT) architetturale e funzionale per l'applicazione da realizzare con **Spark**.
> Viene compilato e formalizzato dall'AI Agent al termine del **Planning Mode Discovery Interview** (`.agents/rules/planning-interview.md`).

---

## 1. Vision & Architecture Overview
- **Nome Applicazione:** Spark App (Da definire durante il Discovery)
- **Slug Applicazione:** `spark-app`
- **Obiettivo Principale:** Risolvere il problema chiave dell'utente con un'esperienza nativa offline-first.
- **Pattern Architetturale:** Modular Clean Architecture + MVI (Model-View-Intent) con Unidirectional Data Flow (UDF).
- **Design System:** Material 3 Expressive (`@OptIn(ExperimentalMaterial3ExpressiveApi::class)`) con Google Sans Flex.
- **Persistenza:** Room DAO `Flow<T>` come Single Source of Truth locale.

---

## 2. Navigazione & Gerarchia Schermate
- **Navigazione:** Multi-Scaffold architecture (lo Scaffold root ospita unicamente la NavigationBar e SnackbarHost).
- **Schermate Principali:**
  - Schermata Home / Dashboard
  - Schermate di Dettaglio / Editor
  - Schermata Impostazioni obbligatoria con sezione "Realizzata con Spark!"

---

## 3. Modello Dati & Entità di Dominio
*(Verranno definite durante la sessione di pianificazione)*

---

## 4. Integrazioni di Sistema & Permessi
- Permessi contestuali richiesti esclusivamente all'azione dell'utente (runtime permissions).
- Supporto notifiche locali e gestione del ciclo di vita con salvataggio dello stato (`rememberSaveable`).

---

## 5. Criteri di Accettazione & Iterative Verification
1. Compilazione priva di errori e warning con `./gradlew assembleDebug`.
2. Esecuzione dei test unitari preliminari con `./gradlew testDebugUnitTest`.
3. Consegna dell'APK versionato in `apk/` con checksum SHA-256 e script di release `./release-agent`.
