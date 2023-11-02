package io.iakanoe.github.dolarcito.domain

import io.iakanoe.github.dolarcito.data.ExchangeRateRepository
import io.iakanoe.github.dolarcito.data.SettingsRepository
import io.iakanoe.github.dolarcito.model.ExchangeRate
import io.iakanoe.github.dolarcito.model.ExchangeRateOrder
import io.iakanoe.github.dolarcito.model.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class GetOrderedExchangeRatesUseCase(
    private val settingsRepository: SettingsRepository,
    private val exchangeRateRepository: ExchangeRateRepository
) {
    fun execute(): Flow<ExchangeRateOrder> {
        val existingRates = flow { emit(exchangeRateRepository.getExchangeRates()) }

        val actualSettings = settingsRepository.getSettings()
            .combine(existingRates) { a, b -> a to b }
            .map { (settings, existingRates) ->
                val updated = settings.update(existingRates.map { it.name })
                if (settings != updated) settingsRepository.setSettings(updated)
                updated to existingRates
            }
            .map { (settings, existingRates) ->
                ExchangeRateOrder(
                    showing = settings.showingRatesNames
                        .map { exchangeRateByName(existingRates, it) },
                    hidden = settings.hiddenRatesNames
                        .map { exchangeRateByName(existingRates, it) }
                )
            }

        return actualSettings
    }

    private fun exchangeRateByName(rates: List<ExchangeRate>, name: String) =
        rates.first { it.name == name }

    private fun Settings.update(existingRatesNames: List<String>): Settings {
        var showing = showingRatesNames
        var hidden = hiddenRatesNames

        // delete no longer existing rates
        showing = showing.filter { it in existingRatesNames }
        hidden = hidden.filter { it in existingRatesNames }

        // add new rates to showing by default
        val newRates = existingRatesNames
            .filterNot { it in showing }
            .filterNot { it in hidden }

        showing = showing + newRates

        // return updated settings
        return Settings(showing, hidden)
    }
}