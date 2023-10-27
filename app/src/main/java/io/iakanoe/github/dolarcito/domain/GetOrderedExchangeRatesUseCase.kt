package io.iakanoe.github.dolarcito.domain

import android.util.Log
import io.iakanoe.github.dolarcito.data.ExchangeRateRepository
import io.iakanoe.github.dolarcito.data.SettingsRepository
import io.iakanoe.github.dolarcito.model.ExchangeRate
import io.iakanoe.github.dolarcito.model.ExchangeRateOrder
import io.iakanoe.github.dolarcito.model.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class GetOrderedExchangeRatesUseCase(
    private val settingsRepository: SettingsRepository,
    private val exchangeRateRepository: ExchangeRateRepository
) {
    suspend fun execute(): Flow<ExchangeRateOrder> {
        val existingRates = exchangeRateRepository.getExchangeRates()

        val actualSettings = settingsRepository.getSettings()
            .map { settings ->
                val updated = settings.update(existingRates.map { it.name })

                Log.d("UseCase", "map\nsettings=$settings\nupdated=$updated")
                if (settings != updated) settingsRepository.setSettings(updated)
                updated
            }
            .onEach { Log.d("onEach", "ONEACH 1 $it") }
            .map { settings ->
                ExchangeRateOrder(
                    showing = settings.showingRatesNames
                        .map { exchangeRateByName(existingRates, it) },
                    hidden = settings.hiddenRatesNames
                        .map { exchangeRateByName(existingRates, it) }
                )
            }
            .onEach { Log.d("onEach", "ONEACH $it") }

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