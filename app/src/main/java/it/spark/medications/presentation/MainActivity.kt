package it.spark.medications.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import it.spark.medications.R
import it.spark.medications.presentation.editor.MedicineEditorScreen
import it.spark.medications.presentation.editor.MedicineEditorViewModel
import it.spark.medications.presentation.history.HistoryScreen
import it.spark.medications.presentation.history.HistoryViewModel
import it.spark.medications.presentation.inventory.InventoryScreen
import it.spark.medications.presentation.inventory.InventoryViewModel
import it.spark.medications.presentation.settings.SettingsScreen
import it.spark.medications.presentation.settings.SettingsViewModel
import it.spark.medications.presentation.theme.MedicationsTheme
import it.spark.medications.presentation.today.TodayScreen
import it.spark.medications.presentation.today.TodayViewModel

enum class MainTab(val titleRes: Int, val icon: ImageVector) {
    TODAY(R.string.nav_today, Icons.Rounded.CalendarToday),
    HISTORY(R.string.nav_history, Icons.Rounded.History),
    INVENTORY(R.string.nav_inventory, Icons.Rounded.Inventory2)
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Handled
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            MedicationsTheme {
                MainAppContainer()
            }
        }
    }
}

@Composable
fun MainAppContainer() {
    var currentTab by rememberSaveable { mutableStateOf(MainTab.TODAY) }
    var activeSubScreen by rememberSaveable { mutableStateOf<String?>(null) } // "settings", "editor_<id>"
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {
            if (activeSubScreen == null) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ) {
                    MainTab.values().forEach { tab ->
                        val selected = currentTab == tab
                        NavigationBarItem(
                            selected = selected,
                            onClick = { currentTab = tab },
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = stringResource(tab.titleRes)
                                )
                            },
                            label = { Text(stringResource(tab.titleRes)) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = activeSubScreen to currentTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screen_transition"
            ) { (subScreen, tab) ->
                when {
                    subScreen == "settings" -> {
                        val settingsViewModel: SettingsViewModel = hiltViewModel()
                        SettingsScreen(
                            viewModel = settingsViewModel,
                            onNavigateBack = { activeSubScreen = null }
                        )
                    }
                    subScreen?.startsWith("editor") == true -> {
                        val rawId = subScreen.substringAfter("editor_").toLongOrNull()
                        val editorViewModel: MedicineEditorViewModel = hiltViewModel()
                        MedicineEditorScreen(
                            viewModel = editorViewModel,
                            medicineId = rawId,
                            onNavigateBack = { activeSubScreen = null }
                        )
                    }
                    tab == MainTab.TODAY -> {
                        val todayViewModel: TodayViewModel = hiltViewModel()
                        TodayScreen(
                            viewModel = todayViewModel,
                            onNavigateToSettings = { activeSubScreen = "settings" }
                        )
                    }
                    tab == MainTab.HISTORY -> {
                        val historyViewModel: HistoryViewModel = hiltViewModel()
                        HistoryScreen(
                            viewModel = historyViewModel
                        )
                    }
                    tab == MainTab.INVENTORY -> {
                        val inventoryViewModel: InventoryViewModel = hiltViewModel()
                        InventoryScreen(
                            viewModel = inventoryViewModel,
                            onNavigateToEditor = { medId ->
                                activeSubScreen = "editor_${medId ?: 0}"
                            }
                        )
                    }
                }
            }
        }
    }
}
