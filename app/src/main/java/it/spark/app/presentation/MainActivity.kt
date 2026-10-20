package it.spark.app.presentation

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import dagger.hilt.android.AndroidEntryPoint
import it.spark.app.presentation.home.HomeScreen
import it.spark.app.presentation.settings.SettingsScreen
import it.spark.app.presentation.theme.SparkTheme
import kotlinx.serialization.Serializable

/** Type-safe navigation routes. Each route is a serializable [NavKey]. */
@Serializable
data object HomeRoute : NavKey

@Serializable
data object SettingsRoute : NavKey

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SparkTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    val backStack = rememberNavBackStack(HomeRoute)
    val activity = LocalContext.current as Activity

    NavDisplay(
        backStack = backStack,
        onBack = {
            // On the root route, the back gesture closes the app
            if (backStack.size > 1) backStack.removeLastOrNull() else activity.finish()
        },
        entryProvider = entryProvider {
            entry<HomeRoute> {
                HomeScreen(onNavigateToSettings = { backStack.add(SettingsRoute) })
            }
            entry<SettingsRoute> {
                SettingsScreen(onNavigateBack = { backStack.removeLastOrNull() })
            }
        }
    )
}
