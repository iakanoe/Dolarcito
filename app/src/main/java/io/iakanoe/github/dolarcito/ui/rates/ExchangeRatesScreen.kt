package io.iakanoe.github.dolarcito.ui.rates

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.iakanoe.github.dolarcito.model.ExchangeRate
import io.iakanoe.github.dolarcito.ui.TopAppBarState
import io.iakanoe.github.dolarcito.ui.common.currentTime
import io.iakanoe.github.dolarcito.ui.common.largeNumberText
import io.iakanoe.github.dolarcito.ui.common.minutesText
import io.iakanoe.github.dolarcito.ui.common.priceText
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import kotlin.math.floor
import kotlin.math.sign

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ExchangeRatesScreen(
    onNavigateToSettings: () -> Unit,
    topAppBarState: TopAppBarState,
    viewModel: ExchangeRatesViewModel = viewModel()
) {
    val viewState by viewModel.viewState.collectAsState()

    val refreshing by remember { derivedStateOf { viewState is ExchangeRatesViewState.Loading } }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = { viewModel.update() }
    )

    val now by currentTime.collectAsState(initial = Calendar.getInstance().timeInMillis)

    val lastUpdated by remember {
        derivedStateOf {
            viewState.let {
                if (it is ExchangeRatesViewState.Loaded) {
                    val millis = now - it.updatedTime
                    val minutes = floor(millis / 60000f)
                    minutes.toInt().coerceAtLeast(0)
                } else null
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.update()
    }

    LaunchedEffect(Unit, lastUpdated) {
        topAppBarState.update(
            title = "Dolarcito",
            subtitle = lastUpdated?.let { "Actualizado ${it.minutesText}" },
            actions = listOf(
                TopAppBarState.IconButton(
                    Icons.Filled.List,
                    contentDescription = "settings button",
                    onClick = onNavigateToSettings
                ),
                TopAppBarState.IconButton(
                    Icons.Filled.Refresh,
                    contentDescription = "refresh button",
                    onClick = { viewModel.update() }
                )
            )
        )
    }

    Surface {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (val state = viewState) {
                is ExchangeRatesViewState.Error -> ErrorContent(
                    onRetryClick = { viewModel.update() }
                )

                is ExchangeRatesViewState.Loaded -> ExchangeRateList(
                    pullRefreshState = pullRefreshState,
                    exchangeRates = state.exchangeRates,
                    hiddenExchangeRates = state.hiddenExchangeRates,
                )

                else -> {}
            }

            PullRefreshIndicator(
                refreshing = refreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
fun ErrorContent(onRetryClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Hubo un error.",
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = onRetryClick) {
            Text(text = "Volver a intentar")
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ExchangeRateList(
    pullRefreshState: PullRefreshState,
    exchangeRates: List<ExchangeRate>,
    hiddenExchangeRates: List<ExchangeRate>
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState),
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(
            count = exchangeRates.size,
            key = { exchangeRates[it].name }
        ) {
            if (it > 0) Spacer(modifier = Modifier.height(16.dp))

            ExchangeRateCard(exchangeRate = exchangeRates[it])
        }

        if (hiddenExchangeRates.isNotEmpty()) {
            item {
                TextButton(onClick = { isExpanded = !isExpanded }) {
                    Text(
                        text = if (isExpanded) "Colapsar ocultos" else "Expandir ocultos",
                        textAlign = TextAlign.Center
                    )
                }
            }

            if (isExpanded) {
                items(
                    count = hiddenExchangeRates.size,
                    key = { hiddenExchangeRates[it].name }
                ) {
                    if (it > 0) Spacer(modifier = Modifier.height(16.dp))

                    ExchangeRateCard(exchangeRate = hiddenExchangeRates[it])
                }
            }
        }
    }
}

@Composable
fun ExchangeRateCard(exchangeRate: ExchangeRate) {
    val borderColor = when (exchangeRate.variation?.sign) {
        -1f -> Color.Red
        1f -> Color.Green
        else -> MaterialTheme.colorScheme.outline
    }

    val now by currentTime.collectAsState(initial = Calendar.getInstance().timeInMillis)

    val lastUpdated by remember {
        derivedStateOf {
            val millis = now - exchangeRate.timestamp
            val minutes = floor(millis / 60000f)
            minutes.toInt()
        }
    }

    val lastUpdatedText =
        if (lastUpdated < 10) lastUpdated.minutesText
        else LocalDateTime.ofInstant(Instant.ofEpochMilli(exchangeRate.timestamp), ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))

    val secondaryText = when {
        exchangeRate.volume != null -> "- vol: ${exchangeRate.volume.largeNumberText}"
        exchangeRate.spread != null -> "- spread: ${exchangeRate.spread.priceText}"
        else -> null
    }

    OutlinedCard(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(width = 2.dp, color = borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = exchangeRate.name.uppercase(),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            Row {
                Text(
                    text = lastUpdatedText,
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.width(8.dp))

                secondaryText?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(
                modifier = Modifier
                    .padding(4.dp)
                    .height(1.dp)
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.onPrimaryContainer)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                exchangeRate.buy?.let {
                    Price(name = "COMPRA", value = it)
                }

                exchangeRate.sell?.let {
                    Price(name = "VENTA", value = it)
                }
            }
        }
    }
}

@Composable
fun Price(name: String, value: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelLarge
        )

        Text(
            text = value.priceText,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Preview
@Composable
fun ExchangeRateCardPreview() {
    val t = Calendar.getInstance()
        .apply { roll(Calendar.MINUTE, -4) }
        .timeInMillis

    ExchangeRateCard(
        exchangeRate = ExchangeRate(
            name = "dolar blue",
            buy = 930.1f,
            sell = 980f,
            timestamp = t,
            variation = null,
            spread = null,
            volume = null
        )
    )
}