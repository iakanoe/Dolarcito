package io.iakanoe.github.dolarcito.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import io.iakanoe.github.dolarcito.ui.common.navigation.Destination
import io.iakanoe.github.dolarcito.ui.common.navigation.NavHost
import io.iakanoe.github.dolarcito.ui.common.navigation.composable
import io.iakanoe.github.dolarcito.ui.common.navigation.navigate
import io.iakanoe.github.dolarcito.ui.rates.ExchangeRatesScreen
import io.iakanoe.github.dolarcito.ui.settings.SettingsScreen

enum class Screen : Destination {
    RATES,
    SETTINGS;
}

class TopAppBarState {
    var title by mutableStateOf("")
        private set

    var subtitle by mutableStateOf<String?>(null)
        private set

    var actions by mutableStateOf(emptyList<IconButton>())
        private set

    var navigationButton by mutableStateOf<IconButton?>(null)
        private set

    fun update(
        title: String,
        subtitle: String? = null,
        actions: List<IconButton> = emptyList(),
        navigationButton: IconButton? = null
    ) {
        this.title = title
        this.subtitle = subtitle
        this.actions = actions
        this.navigationButton = navigationButton
    }

    data class IconButton(
        val imageVector: ImageVector,
        val contentDescription: String?,
        val onClick: () -> Unit,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DolarcitoNavigation() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val topAppBarState = remember { TopAppBarState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = topAppBarState.title)

                        topAppBarState.subtitle?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                },
                actions = {
                    topAppBarState.actions.forEach {
                        IconButton(onClick = { it.onClick() }) {
                            Icon(
                                imageVector = it.imageVector,
                                contentDescription = it.contentDescription
                            )
                        }
                    }
                },
                navigationIcon = {
                    topAppBarState.navigationButton?.let {
                        IconButton(onClick = { it.onClick() }) {
                            Icon(
                                imageVector = it.imageVector,
                                contentDescription = it.contentDescription
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = Screen.RATES,
            enterTransition = { slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Start) + fadeIn() },
            exitTransition = { slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.End) + fadeOut() }
        ) {
            composable(Screen.RATES) {
                ExchangeRatesScreen(
                    onNavigateToSettings = { navController.navigate(Screen.SETTINGS) },
                    topAppBarState = topAppBarState,
                    viewModel = hiltViewModel()
                )
            }

            composable(Screen.SETTINGS) {
                SettingsScreen(
                    onNavigateBack = { navController.navigateUp() },
                    topAppBarState = topAppBarState,
                    snackbarHostState = snackbarHostState,
                    viewModel = hiltViewModel()
                )
            }
        }
    }
}