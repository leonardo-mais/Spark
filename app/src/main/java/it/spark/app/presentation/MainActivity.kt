package it.spark.app.presentation

import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        setContent {
            SparkTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    MainApp()
                }
            }
        }
    }
}

@Composable
fun MainApp() {
    val backStack = rememberNavBackStack(HomeRoute)
    val activity = LocalContext.current as Activity

    // Fast fade transition: 80ms (doubled speed compared to standard 160ms)
    val fastFadeIn = fadeIn(animationSpec = tween(durationMillis = 80))
    val fastFadeOut = fadeOut(animationSpec = tween(durationMillis = 80))

    NavDisplay(
        backStack = backStack,
        onBack = {
            // On the root route, the back gesture closes the app
            if (backStack.size > 1) backStack.removeLastOrNull() else activity.finish()
        },
        transitionSpec = { fastFadeIn togetherWith fastFadeOut },
        popTransitionSpec = { fastFadeIn togetherWith fastFadeOut },
        predictivePopTransitionSpec = { fastFadeIn togetherWith fastFadeOut },
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
