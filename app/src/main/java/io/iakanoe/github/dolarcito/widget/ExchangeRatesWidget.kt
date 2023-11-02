package io.iakanoe.github.dolarcito.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import dagger.hilt.android.EntryPointAccessors
import io.iakanoe.github.dolarcito.MainActivity
import io.iakanoe.github.dolarcito.model.ExchangeRate
import io.iakanoe.github.dolarcito.model.ExchangeRateOrder
import io.iakanoe.github.dolarcito.ui.common.priceText
import io.iakanoe.github.dolarcito.ui.common.theme.DolarcitoGlanceTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import java.lang.Float.max

class ExchangeRatesWidget : GlanceAppWidget() {

    sealed class State {
        data object Loading : State()
        data object Error : State()
        data class Loaded(val exchangeRateOrder: ExchangeRateOrder) : State()
    }

    private fun getExchangeRateOrder(context: Context): Flow<State> {
        val appContext = context.applicationContext ?: error("No app context")
        val entryPoint: ExchangeRatesWidgetEntryPoint = EntryPointAccessors.fromApplication(appContext)
        val getOrderedExchangeRatesUseCase = entryPoint.getOrderedExchangeRatesUseCase()
        return getOrderedExchangeRatesUseCase.execute()
            .map<ExchangeRateOrder, State> { State.Loaded(it) }
            .onStart { emit(State.Loading) }
            .catch {
                it.printStackTrace()
                emit(State.Error)
            }
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val viewState by getExchangeRateOrder(context).collectAsState(State.Loading)

            DolarcitoGlanceTheme {
                Content(viewState)
            }
        }
    }
}

val largeTextStyle
    @Composable get() = TextStyle(
        color = GlanceTheme.colors.onSurface,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    )

val smallTextStyle
    @Composable get() = TextStyle(
        color = GlanceTheme.colors.onSurface,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    )

@Composable
private fun Content(state: ExchangeRatesWidget.State) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.surface)
            .clickable(actionStartActivity<MainActivity>())
    ) {
        when (state) {
            is ExchangeRatesWidget.State.Loading -> LoadingContent()
            is ExchangeRatesWidget.State.Error -> ErrorContent()
            is ExchangeRatesWidget.State.Loaded -> LoadedContent(state)
        }
    }
}

@Composable
fun LoadingContent() {
    CircularProgressIndicator()
}

@Composable
fun ErrorContent() {
    Text(
        text = "Ocurrió un error.",
        style = largeTextStyle
    )
}

@Composable
private fun LoadedContent(state: ExchangeRatesWidget.State.Loaded) {
    val list = state.exchangeRateOrder.showing.take(3)

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        for (item in list) {
            ExchangeRateItem(
                exchangeRate = item,
                modifier = GlanceModifier.defaultWeight()
            )
        }
    }
}

@Composable
private fun ExchangeRateItem(
    modifier: GlanceModifier = GlanceModifier,
    exchangeRate: ExchangeRate,
) {
    val higherPrice = max(exchangeRate.buy ?: 0f, exchangeRate.sell ?: 0f)
    val type = if (higherPrice == exchangeRate.buy) "c" else "v"
    val color = (exchangeRate.variation ?: 0f).let {
        when {
            it < 0 -> ColorProvider(Color.Red)
            it > 0 -> ColorProvider(Color.Green)
            else -> GlanceTheme.colors.onSurface
        }
    }

    Row(modifier = modifier.fillMaxWidth()) {
        Text(
            text = exchangeRate.name.removePrefix("dolar ").uppercase(),
            style = largeTextStyle,
            modifier = GlanceModifier.defaultWeight(),
            maxLines = 1
        )

        Spacer(modifier = GlanceModifier.size(8.dp))

        Text(
            text = type,
            style = smallTextStyle
        )

        Spacer(modifier = GlanceModifier.size(4.dp))

        Text(
            text = higherPrice.priceText,
            style = largeTextStyle.copy(color = color)
        )
    }
}