package io.iakanoe.github.dolarcito.widget

import androidx.glance.appwidget.GlanceAppWidgetReceiver

class ExchangeRatesWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = ExchangeRatesWidget()
}