package io.iakanoe.github.dolarcito.domain

import io.iakanoe.github.dolarcito.data.ExchangeRateRepository
import io.iakanoe.github.dolarcito.data.SettingsRepository
import io.iakanoe.github.dolarcito.model.ExchangeRate
import io.iakanoe.github.dolarcito.model.ExchangeRateOrder
import io.iakanoe.github.dolarcito.model.Settings
import kotlinx.coroutines.flow.first

class GetOrderedExchangeRatesUseCase(
    private val settingsRepository: SettingsRepository,
    private val exchangeRateRepository: ExchangeRateRepository
) {
    suspend fun execute(): ExchangeRateOrder {
        val existingRates = exchangeRateRepository.getExchangeRates()

        val settings = settingsRepository.getSettings().first()

        val updatedSettings = settings.update(existingRates.map { it.name })
        if (settings != updatedSettings) settingsRepository.setSettings(updatedSettings)

        return ExchangeRateOrder(
            showing = updatedSettings.showingRatesNames
                .map { exchangeRateByName(existingRates, it) },
            hidden = updatedSettings.hiddenRatesNames
                .map { exchangeRateByName(existingRates, it) }
        )
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