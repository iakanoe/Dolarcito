package io.iakanoe.github.dolarcito.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.iakanoe.github.dolarcito.ui.TopAppBarState

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    topAppBarState: TopAppBarState,
    snackbarHostState: SnackbarHostState,
    viewModel: SettingsViewModel = viewModel(),
) {
    val viewState by viewModel.viewState.collectAsState()

    LaunchedEffect(viewState) {
        when (viewState) {
            is SettingsViewState.SavingError -> {
                snackbarHostState.showSnackbar("Error al guardar configuración.")
                onNavigateBack()
            }

            is SettingsViewState.RetrievingError -> {
                snackbarHostState.showSnackbar("Error al leer configuración guardada.")
                onNavigateBack()
            }

            is SettingsViewState.Saved -> onNavigateBack()

            else -> Unit
        }
    }

    LaunchedEffect(Unit) {
        topAppBarState.update(
            title = "Ordenar",
            actions = listOf(
                TopAppBarState.IconButton(
                    Icons.Filled.Done,
                    contentDescription = "save button",
                    onClick = { viewModel.saveNewOrder() }
                )
            ),
            navigationButton = TopAppBarState.IconButton(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "back button",
                onClick = onNavigateBack
            )
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (viewState) {
            is SettingsViewState.Loading -> CircularProgressIndicator()
            is SettingsViewState.Loaded -> OrderableList()
            else -> Unit
        }
    }
}

@Composable
fun OrderableList(viewModel: SettingsViewModel = viewModel()) {
    val viewState by viewModel.viewState.collectAsState()

    val state = viewState as? SettingsViewState.Loaded ?: return

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        item { Header(text = "Visibles") }
        items(
            count = state.showingRates.size,
            key = { state.showingRates[it] }
        ) {
            val name = state.showingRates[it]

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.outline)
            )

            Item(
                text = name,
                canMoveUp = it > 0,
                canMoveDown = true,
                isHidden = false,
                onMoveUp = { viewModel.moveItem(name, true) },
                onMoveDown = { viewModel.moveItem(name, false) },
                onHide = { viewModel.hideItem(name) },
                onShow = { viewModel.showItem(name) }
            )
        }

        item {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.outline)
            )

            Header(text = "Ocultos")
        }

        items(
            count = state.hiddenRates.size,
            key = { state.hiddenRates[it] }
        ) {
            val name = state.hiddenRates[it]

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.outline)
            )

            Item(
                text = name,
                canMoveUp = true,
                canMoveDown = it < state.hiddenRates.size - 1,
                isHidden = true,
                onMoveUp = { viewModel.moveItem(name, true) },
                onMoveDown = { viewModel.moveItem(name, false) },
                onHide = { viewModel.hideItem(name) },
                onShow = { viewModel.showItem(name) }
            )
        }
    }
}

@Composable
fun Header(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = text)
    }
}

@Composable
fun Item(
    text: String,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    isHidden: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onHide: () -> Unit,
    onShow: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            modifier = Modifier.weight(1f)
        )

        if (isHidden) {
            IconButton(onClick = onShow) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "show button"
                )
            }
        } else {
            IconButton(onClick = onHide) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "hide button"
                )
            }
        }

        IconButton(
            onClick = onMoveUp,
            enabled = canMoveUp
        ) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowUp,
                contentDescription = "move up button"
            )
        }

        IconButton(
            onClick = onMoveDown,
            enabled = canMoveDown
        ) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "move down button"
            )
        }
    }
}