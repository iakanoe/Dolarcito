package io.iakanoe.github.dolarcito.data

import io.iakanoe.github.dolarcito.gateway.SettingsGateway
import io.iakanoe.github.dolarcito.model.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class SettingsRepository(
    private val settingsGateway: SettingsGateway
) {
    fun getSettings(): Flow<Settings> {
        val showingRatesNames = settingsGateway.retrieveShowingRatesNames()
        val hiddenRatesNames = settingsGateway.retrieveHiddenRatesNames()
        return combine(showingRatesNames, hiddenRatesNames) { showing, hidden ->
            Settings(showing, hidden)
        }
    }

    suspend fun setSettings(settings: Settings) =
        settingsGateway.saveBothRatesNames(settings.showingRatesNames, settings.hiddenRatesNames)
}