---
name: spark-wear
description: Official Spark! guidelines and agent workflow for developing Wear OS smartwatch applications (Standalone & Companion) using Wear Compose Material 3 (Standard & High-Impact Expressive styles), bidirectional Phone-Watch synchronization via Google Play Services Wearable Data Layer API, and automated Obtainium self-hosted deployment.
---

# Spark! Wear OS Development, Data Layer & Obtainium Release Skill (`spark-wear`)

Questo documento definisce le regole architetturali, il processo di intervista preliminare, i pattern di **Wear Compose Material 3** (nelle due varianti **Standard** e **D'Impatto**), la comunicazione bidirezionale **Telefono ⇄ Orologio** tramite **Google Play Services Wearable** e il deploy automatizzato su **Obtainium** per le applicazioni smartwatch dell'ecosistema Spark!.

---

## 🛑 1. Regola Tassativa di Attivazione & Intervista Preliminare

1. **Sviluppo Esclusivamente su Richiesta Esplicita**:
   - L'AI Agent **NON DEVE MAI** creare il modulo `:wear` o sviluppare codice per smartwatch di propria iniziativa.
   - Lo sviluppo dell'app Wear OS si attiva **ESCLUSIVAMENTE quando l'utente chiede esplicitamente di svilupparla**.
2. **Intervista Obbligatoria (`planning-wear-interview.md`)**:
   - Prima di scrivere qualsiasi riga di codice o modificare la configurazione Gradle per Wear OS, l'AI Agent **DEVE SEMPRE** leggere e condurre l'intervista interattiva definita in:
     - `.agents/rules/planning-wear-interview.md` (e `./planning-wear-interview.md`)
   - Tutte le domande devono essere poste usando il tool `ask_question` per definire:
     1. **Funzionalità da includere** (selezionate dall'app base `:app` + proposte proattive dell'AI specifiche per il polso).
     2. **Grado di semplicità dell'interfaccia** (*Ultra-Essenziale Glance & Tap*, *Companion Bilanciato*, o *Standalone Completo*).
     3. **Modalità di sincronizzazione Telefono ⇄ Orologio** (`DataClient`, `MessageClient`, o *Ibrido*).
     4. **Integrazioni sistema Wear OS** (*Ambient Mode*, *Haptics/Vibrazione*, *Tile / Complicazioni*).
     5. **Strategia di deploy Obtainium** (*Opzione A: Obtainium diretto su Watch* vs *Opzione B: Sideload assistito da Telefono*).
     6. **Direzione Visiva & Stile Estetico**: **Stile Standard / Classico** vs **Stile d'Impatto (Wear Expressive Dynamic)**.

---

## 🏛️ 2. Architettura del Modulo `:wear` & Configurazione Gradle

### 2.1 Struttura Multi-Modulo
```
<ProjectRoot>/
├── app/                        # Applicazione Smartphone principale
├── wear/                       # Applicazione Smartwatch (Wear OS)
├── core/model/                 # (Opzionale) Modelli dati e costanti Data Layer condivisi
├── apk/                        # Output universale APK (Phone + Wear)
└── dist/
    ├── <appSlug>/              # Namespace Obtainium per Smartphone
    └── <appSlug>-wear/         # Namespace Obtainium dedicato per Smartwatch
```

### 2.2 Invariante di Autenticazione Data Layer (`applicationId` e Firma)
> [!IMPORTANT]
> Affinché **Google Play Services Wearable** (`DataClient`, `MessageClient`, `ChannelClient`) consenta la comunicazione tra l'app telefono (`:app`) e l'app orologio (`:wear`), entrambi i moduli **DEVONO OBBLIGATORIAMENTE**:
> 1. Avere lo **stesso identico `applicationId`** (es. `it.spark.cookbook`).
> 2. Essere firmati con lo **stesso identico keystore crittografico** sia in debug che in release.
>
> Se `applicationId` o certificato SHA-1/SHA-256 differiscono, Play Services blocca silenziosamente tutti i messaggi e gli eventi dati tra telefono e orologio.

### 2.3 Dipendenze Ufficiali (`wear/build.gradle.kts`)
Utilizzare esclusivamente **Wear Compose Material 3** (`androidx.wear.compose.material3`) ed evitare librerie deprecate (Material 2.5 o Horologist Composables):

```kotlin
dependencies {
    // Wear OS Compose Material 3 & Foundation
    implementation("androidx.wear.compose:compose-material3:1.5.0-beta01") // o ultima stabile in libs.versions.toml
    implementation("androidx.wear.compose:compose-foundation:1.5.0-beta01")
    implementation("androidx.wear.compose:compose-navigation:1.5.0-beta01")
    implementation("androidx.wear.compose:compose-ui-tooling:1.5.0-beta01")

    // Google Play Services Wearable (Comunicazione Telefono <-> Orologio)
    implementation("com.google.android.gms:play-services-wearable:19.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.9.0")

    // Activity, Lifecycle & Hilt
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
}
```

### 2.4 Configurazione `wear/src/main/AndroidManifest.xml`
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <uses-feature android:name="android.hardware.type.watch" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    <uses-permission android:name="android.permission.VIBRATE" />

    <application
        android:name=".WearApp"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@android:style/Theme.DeviceDefault">

        <!-- Specifica se l'app può operare anche senza telefono connesso -->
        <meta-data
            android:name="com.google.android.wearable.standalone"
            android:value="true" />

        <activity
            android:name=".presentation.WearMainActivity"
            android:exported="true"
            android:taskAffinity=""
            android:theme="@android:style/Theme.DeviceDefault">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <!-- Servizio in background per ricevere dati e comandi dal telefono -->
        <service
            android:name=".data.datalayer.WearDataListenerService"
            android:exported="true">
            <intent-filter>
                <action android:name="com.google.android.gms.wearable.DATA_CHANGED" />
                <action android:name="com.google.android.gms.wearable.MESSAGE_RECEIVED" />
                <data android:scheme="wear" android:host="*" />
            </intent-filter>
        </service>
    </application>
</manifest>
```

---

## 🎨 3. Design System su Wear OS: Stile Standard vs Stile d'Impatto

Come su smartphone con `spark-jetpack`, ogni app Wear OS deve rispettare i vincoli fondamentali:
- **Zero Colori Hardcoded**: tutti i colori provengono da `androidx.wear.compose.material3.MaterialTheme.colorScheme.*` su fondo OLED nero puro (`background = Color.Black`) per massimizzare contrasto e autonomia.
- **Zero Emoji Policy**: divieto assoluto di emoji nella UI o in `strings.xml`; usare esclusivamente icone vettoriali Material Symbols.
- **Zero Stringhe Hardcoded**: doppia lingua obbligatoria (`values/strings.xml` in inglese e `values-it/strings.xml` in italiano) gestita dalle impostazioni di sistema.
- **Gerarchia Scaffold**: un unico `AppScaffold` radice (che gestisce `TimeText` globale) e uno `ScreenScaffold` per ciascuna schermata (che gestisce `ScrollIndicator` ed `EdgeButton`).
- **Liste**: usare sempre `TransformingLazyColumn` (mai la vecchia `ScalingLazyColumn`).

### 3.1 Archetipo 1: Stile Standard / Classico (Wear Compose M3 Rigoroso)
Ideale per liste dense, consultazione rapida, utility e interfacce sobrie ad altissima leggibilità:
- **Tipografia**: ruoli standard di `androidx.wear.compose.material3.Typography` con `GoogleSansFlexFontFamily`.
- **Struttura Lista**:
  - `ListHeader` in cima per il titolo della sezione.
  - `TitleCard` o `AppCard` con `containerColor = MaterialTheme.colorScheme.surfaceContainer` per gli elementi informativi.
  - `Button` (a larghezza piena), `CheckboxButton` o `SplitCheckboxButton` per le checklist (es. Lista della Spesa).
- **Sobrietà Visiva**: transizioni standard, indicatori di scorrimento laterali puliti, focus sulla leggibilità immediata del testo.

### 3.2 Archetipo 2: Stile d'Impatto (Wear Expressive Dynamic)
Ideale per modalità attiva a mani libere (es. Cooking Mode passo-passo), timer da polso, fitness, salute e dashboard ad alto effetto WOW:
- **Tipografia Parametrica a 6 Assi (`Google Sans Flex`)**:
  - Numeri e contatori giganti al centro del quadrante con `FlexHeroNumericFont` (`wght = 820`, `wdth = 115f`, `ROND = 100f`, dimensione 34sp–44sp).
  - Intestazioni espressive con `FlexHeadlineFont` (`wght = 750`, `wdth = 108f`, `ROND = 80f`).
- **Morphing Geometrico in Scroll (`SurfaceTransformation`)**:
  - Ogni elemento dentro `TransformingLazyColumn` applica `Modifier.transformedHeight(this, transformationSpec)` e `transformation = SurfaceTransformation(transformationSpec)` per rimpicciolirsi e curvarsi organicamente seguendo il bordo circolare dello smartwatch.
- **Indicatori Radiali & Testo Curvo**:
  - Uso di `ArcProgressIndicator` o `SegmentedCircularProgressIndicator` lungo la lunetta per mostrare l'avanzamento degli step o il tempo residuo del timer.
  - Uso di `curvedText` per etichette contestuali lungo la circonferenza.
- **`EdgeButton` Sagomato sul Bordo Inferiore**:
  - Azione primaria posizionata nello slot `edgeButton` di `ScreenScaffold` (che segue la curvatura inferiore della cassa dell'orologio) con effetto overscroll collegato alla lista.
- **Fisica a Molla & Feedback Aptico**:
  - Pagine a scorrimento (`HorizontalPagerScaffold` / `VerticalPagerScaffold` con `AnimatedPage`), snapping sulla ghiera rotante (`RotaryScrollableDefaults.snapBehavior(columnState)`) e micro-vibrazioni sincronizzate.

#### Blueprint Codice: Schermata Wear M3 Expressive (`TransformingLazyColumn` + `EdgeButton`)
```kotlin
@Composable
fun WearExpressiveListScreen(
    title: String,
    items: List<WearChecklistItem>,
    onToggleItem: (WearChecklistItem) -> Unit,
    onPrimaryAction: () -> Unit
) {
    val columnState = rememberTransformingLazyColumnState()
    val transformationSpec = rememberTransformationSpec()

    ScreenScaffold(
        scrollState = columnState,
        edgeButton = {
            EdgeButton(
                onClick = onPrimaryAction,
                buttonSize = EdgeButtonSize.Medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.scrollable(
                    state = columnState,
                    orientation = Orientation.Vertical,
                    reverseDirection = true,
                    overscrollEffect = rememberOverscrollEffect()
                )
            ) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = stringResource(R.string.action_done)
                )
            }
        }
    ) { contentPadding ->
        TransformingLazyColumn(
            state = columnState,
            contentPadding = contentPadding,
            flingBehavior = TransformingLazyColumnDefaults.snapFlingBehavior(columnState),
            rotaryScrollableBehavior = RotaryScrollableDefaults.snapBehavior(columnState)
        ) {
            item(key = "header") {
                ListHeader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .transformedHeight(this, transformationSpec)
                        .minimumVerticalContentPadding(ListHeaderDefaults.minimumTopListContentPadding),
                    transformation = SurfaceTransformation(transformationSpec)
                ) {
                    Text(
                        text = title,
                        fontFamily = FlexHeadlineFont,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            items(items = items, key = { it.id }) { entry ->
                CheckboxButton(
                    checked = entry.isChecked,
                    onCheckedChange = { onToggleItem(entry) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .transformedHeight(this, transformationSpec)
                        .minimumVerticalContentPadding(ButtonDefaults.minimumVerticalListContentPadding),
                    transformation = SurfaceTransformation(transformationSpec),
                    label = {
                        Text(
                            text = entry.label,
                            fontFamily = FlexSubheadingFont,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
        }
    }
}
```

---

## 🔄 4. Dialogo Telefono ⇄ Orologio (Google Play Services Wearable Data Layer)

La comunicazione tra `:app` (smartphone) e `:wear` (smartwatch) deve utilizzare le API coroutines-friendly di `com.google.android.gms.wearable.Wearable`.

### 4.1 Schema dei Path Standardizzati
Definire sempre un oggetto costante identico tra `:app` e `:wear`:
```kotlin
object WearDataLayerPaths {
    // DataClient Paths (Stato persistente sincronizzato offline-first)
    const val SHOPPING_LIST_PATH = "/sync/shopping_list"
    const val ACTIVE_RECIPE_PATH = "/sync/active_recipe"
    const val RECIPES_CATALOG_PATH = "/sync/recipes_catalog"

    // MessageClient Paths (Comandi istantanei RPC a bassa latenza)
    const val MSG_TOGGLE_SHOPPING_ITEM = "/action/toggle_shopping_item"
    const val MSG_START_TIMER = "/action/start_timer"
    const val MSG_CANCEL_TIMER = "/action/cancel_timer"
    const val MSG_STEP_COMPLETED = "/action/step_completed"
    const val MSG_REQUEST_SYNC = "/action/request_sync"
}
```

### 4.2 Invio Stato Persistente con `DataClient` (Offline-First)
Usare `PutDataMapRequest` con `.setUrgent()` per sincronizzare dati che devono restare disponibili sull'orologio anche quando il Bluetooth si disconnette:
```kotlin
suspend fun syncShoppingListToWatch(context: Context, itemsJson: String) {
    val dataClient = Wearable.getDataClient(context)
    val putDataReq = PutDataMapRequest.create(WearDataLayerPaths.SHOPPING_LIST_PATH).apply {
        dataMap.putString("items_json", itemsJson)
        dataMap.putLong("updated_at", System.currentTimeMillis())
        setUrgent()
    }.asPutDataRequest()
    dataClient.putDataItem(putDataReq).await()
}
```

### 4.3 Invio Comandi Istantanei con `MessageClient`
Usare `MessageClient` per azioni immediate (es. avvio timer sul polso o spunta di un elemento):
```kotlin
suspend fun sendInstantMessageToConnectedNodes(
    context: Context,
    path: String,
    payload: ByteArray = ByteArray(0)
) {
    val nodeClient = Wearable.getNodeClient(context)
    val messageClient = Wearable.getMessageClient(context)
    val nodes = nodeClient.connectedNodes.await()
    for (node in nodes) {
        messageClient.sendMessage(node.id, path, payload).await()
    }
}
```

### 4.4 Ricezione in Background (`WearableListenerService`)
Sia `:app` che `:wear` implementano un `WearableListenerService` che intercetta `onDataChanged` e `onMessageReceived` anche ad applicazione chiusa, aggiornando il database locale / DataStore o avviando timer e notifiche locali:
```kotlin
class WearDataListenerService : WearableListenerService() {
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val path = event.dataItem.uri.path
                val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                when (path) {
                    WearDataLayerPaths.SHOPPING_LIST_PATH -> {
                        val json = dataMap.getString("items_json").orEmpty()
                        // Aggiorna repository locale su Watch
                    }
                }
            }
        }
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        when (messageEvent.path) {
            WearDataLayerPaths.MSG_START_TIMER -> {
                val seconds = String(messageEvent.data).toIntOrNull() ?: return
                // Avvia timer locale con vibrazione sul polso
            }
        }
    }
}
```

---

## 📦 5. Deploy Automatizzato su Obtainium per Wear OS (Punto 4)

La distribuzione dell'applicazione Wear OS sfrutta l'infrastruttura self-hosted multi-app di Spark! (Caddy + Obtainium su Tailscale, es. `http://<SERVER_IP>:3200`).

### 5.1 Isolamento Namespace `<appSlug>-wear`
Mentre i pacchetti `:app` e `:wear` condividono lo stesso `applicationId` nel codice Kotlin per autenticare il Data Layer di Google Play Services, sul server di distribuzione Obtainium l'artefatto Wear OS viene isolato nel proprio namespace dedicato:
- **Namespace Smartphone**: `dist/<appSlug>/` -> `http://<SERVER_IP>:3200/<appSlug>/version.json`
- **Namespace Smartwatch**: `dist/<appSlug>-wear/` -> `http://<SERVER_IP>:3200/<appSlug>-wear/version.json`
- **Copia Universale in `/apk`**: ogni build Wear copia l'APK firmato in `<projectRoot>/apk/<AppName>-Wear-v<versionName>-b<wearVersionCode>-release.apk`.
- **`versionCode` Distinct Scheme**: per evitare conflitti di versione, il `versionCode` del modulo `:wear` può seguire lo schema `20000 + versionCode` (oppure lo stesso `versionCode` sincronizzato), garantendo aggiornamenti puliti su Obtainium.

### 5.2 Le 2 Modalità di Installazione e Aggiornamento sullo Smartwatch

#### Opzione A: Obtainium Installato Direttamente sullo Smartwatch (Consigliata)
1. **Setup Iniziale (Una Tantum)**:
   - Installare Obtainium sullo smartwatch Wear OS (tramite ADB Wi-Fi, *Bugjaeger* o *Easy Fire Tools*).
2. **Configurazione Feed Obtainium su Watch**:
   - Aggiungere in Obtainium sull'orologio l'URL del feed dedicato:
     `http://<SERVER_IP>:3200/<appSlug>-wear/version.json`
     *(oppure la landing page `http://<SERVER_IP>:3200/<appSlug>-wear/`)*.
3. **Aggiornamenti Continui Over-The-Air**:
   - Quando l'orologio è connesso in Wi-Fi o tramite Bluetooth al telefono con Tailscale attivo, Obtainium rileva la nuova versione e installa l'aggiornamento direttamente sul polso.

#### Opzione B: Aggiornamento Assistito da Smartphone (Phone-Assisted Sideload)
1. L'utente apre la landing page `http://<SERVER_IP>:3200/<appSlug>-wear/` (o aggiunge `<appSlug>-wear` su Obtainium in modalità solo download sullo smartphone).
2. Invia l'APK scaricato allo smartwatch tramite **Wear Installer 2** o connessione **ADB Wireless** integrata.

### 5.3 Estensione Pipeline `./release-agent` per Progetti con Modulo `:wear`
Quando il modulo `:wear` è presente nel progetto:
1. `./release-agent [patch|minor|major] -m "..."` (o `./release-agent --wear ...`) esegue:
   - Test unitari (`./gradlew :app:testDebugUnitTest :wear:testDebugUnitTest`).
   - Compilazione firmata (`./gradlew :app:assembleRelease :wear:assembleRelease`).
   - Copia di entrambi gli APK in `<projectRoot>/apk/`.
   - Generazione dei metadati Obtainium sia in `dist/<appSlug>/version.json` che in `dist/<appSlug>-wear/version.json` (con relativa landing page `index.html`).
   - Upload atomico via HTTP streaming verso `${SERVER_URL}/upload`.
   - Verifica pre-flight `HTTP 200 OK` su entrambi gli endpoint (`/<appSlug>/version.json` e `/<appSlug>-wear/version.json`).
