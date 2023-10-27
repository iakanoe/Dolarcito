package io.iakanoe.github.dolarcito.domain

import io.iakanoe.github.dolarcito.data.SettingsRepository
import io.iakanoe.github.dolarcito.model.Settings

class SaveNewSettingsUseCase(
    private val settingsRepository: SettingsRepository
) {
    suspend fun execute(newSettings: Settings) =
        settingsRepository.setSettings(newSettings)
}