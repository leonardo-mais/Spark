package it.spark.app.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import it.spark.app.presentation.home.HomeScreen
import it.spark.app.presentation.settings.SettingsScreen
import it.spark.app.presentation.theme.SparkTheme

enum class Screen {
    HOME,
    SETTINGS
}

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
    var currentScreen by rememberSaveable { mutableStateOf(Screen.HOME) }

    BackHandler(enabled = currentScreen != Screen.HOME) {
        currentScreen = Screen.HOME
    }

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "screen_transition",
        modifier = Modifier.fillMaxSize()
    ) { screen ->
        when (screen) {
            Screen.HOME -> HomeScreen(
                onNavigateToSettings = { currentScreen = Screen.SETTINGS }
            )
            Screen.SETTINGS -> SettingsScreen(
                onNavigateBack = { currentScreen = Screen.HOME }
            )
        }
    }
}
