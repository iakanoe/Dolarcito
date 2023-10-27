package io.iakanoe.github.dolarcito.domain

import io.iakanoe.github.dolarcito.data.SettingsRepository

class GetSettingsUseCase(
    private val settingsRepository: SettingsRepository,
) {
    fun execute() =
        settingsRepository.getSettings()
}