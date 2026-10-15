# ✨ Spark! • Modern Android Starter Template & Framework

Spark! è un framework e template starter per lo sviluppo di applicazioni Android in maniera facile e veloce, senza toccare una riga di codice!

Basato su **Jetpack Compose** utilizza il **Material 3 Expressive**, il nuovo design system di Google, per rendere dinamica e moderna la tua nuova applicazione, lasciando a te la scelta di quanto esagerare! 

È consigliato l'utilizzo di un server **Obtainium** per gestire gli aggiornamenti e l'installazione con un click

Il repository è configurato nativamente per lavorare in simbiosi con gli **Agenti AI di Antigravity**, includendo regole di sviluppo, interview di discovery, cicli iterativi con verifica visiva e skill specializzate.

---

## 🚀 Come Creare una Nuova Applicazione con Spark!

### Metodo 1: GitHub Template (Consigliato - Zero Copia-Incolla)
1. Clicca in alto a destra su **"Use this template"** ➔ **"Create a new repository"**.
2. Clona il nuovo repository sul tuo computer ed aprilo in **Antigravity IDE** o Android Studio.
3. Inizia subito in chat con un prompt di pianificazione (es. `/grill-me Progetta una nuova app per...` oppure `/plan`).
4. L'AI Agent leggerà automaticamente le regole in `.agents/`, condurrà la **Discovery Interview** (chiedendoti anche la scelta tra *Stile Classico* e *Stile d'Impatto*) e costruirà l'applicazione.

---

## 📦 Cosa Contiene il Template Spark!

| Cartella / File | Descrizione | Mantenere per Nuove App? |
|---|---|:---:|
| `.agents/rules/` | Regole tassative di architettura, interview e ciclo iterativo | **SÌ (Invariante)** |
| `.agents/skills/spark-jetpack/` | Linee guida M3 Expressive, token, forme e blueprint tipografici | **SÌ (Invariante)** |
| `.agents/skills/agent-builder/` | Automazione release e deployment Obtainium | **SÌ (Invariante)** |
| `.agents/skills-main/` | Moduli ufficiali Google Android (Navigation 3, Lifecycle, Performance) | **SÌ (Invariante)** |
| `scripts/release.sh` + `release-agent` | Script CLI di semantic versioning, build release e push al server | **SÌ (Invariante)** |
| `app/src/main/res/font/google_sans_flex.ttf` | Font variabile a 6 assi per rendering offline zero-latenza | **SÌ (Invariante)** |
| `app/src/main/java/.../presentation/theme/Type.kt` | Motore `flexFont` e stili tipografici `ExpressiveType` | **SÌ (Invariante)** |
| `app/src/main/java/.../presentation/common/` | Componenti standard (`MediumFAB`, `CircularBackButton`, `GroupedCard`) | **SÌ (Invariante)** |
| `.env` | Endpoint e token per il caricamento sul server Obtainium | **SÌ (Invariante)** |
| `version.properties` | Configurazione nome app, slug e versione iniziale | **Reimpostare per nuova app** |
| `app/src/main/java/.../{data,domain,today,...}` | Logica specifica dell'app precedente | **Sostituire con la nuova app** |

---

## 🚀 Rilascio Continuo & Self-Hosted (Obtainium)

Il repository include un **Agente di Release automatico** (`./release-agent` o `./scripts/release.sh`) per gestire semantic versioning, test unitari, firma crittografica dell'APK e upload istantaneo al server Tailscale:

```bash
# Rilascio Patch (es. 1.0.0 -> 1.0.1)
./release-agent patch -m "Descrizione release"

# Rilascio Minor (es. 1.0.1 -> 1.1.0)
./release-agent minor -m "Nuove funzionalità"

# Rilascio Major (es. 1.1.0 -> 2.0.0)
./release-agent major -m "Nuova architettura"
```

Gli artefatti generati vengono posizionati in:
- `apk/`: Consegna locale universale del file APK firmato (`<AppName>-v<versione>-b<build>-release.apk`).
- `dist/<appSlug>/`: Feed `version.json` per Obtainium, landing page `index.html` e binari.
- Server remoto HTTP: Deploy immediato tramite il server ricevente su Tailscale.

---

## 🛠️ Build e Test Rapidi

```bash
# Esecuzione dei test unitari
./gradlew testDebugUnitTest

# Compilazione debug
./gradlew assembleDebug

# Compilazione release firmata
./gradlew assembleRelease
```
