package io.iakanoe.github.dolarcito.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.iakanoe.github.dolarcito.data.ExchangeRateRepository
import io.iakanoe.github.dolarcito.data.SettingsRepository
import io.iakanoe.github.dolarcito.domain.GetOrderedExchangeRatesUseCase
import io.iakanoe.github.dolarcito.domain.GetSettingsUseCase
import io.iakanoe.github.dolarcito.domain.SaveNewSettingsUseCase

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideGetOrderedExchangeRatesUseCase(
        settingsRepository: SettingsRepository,
        exchangeRateRepository: ExchangeRateRepository
    ) = GetOrderedExchangeRatesUseCase(
        settingsRepository,
        exchangeRateRepository
    )

    @Provides
    fun provideGetSettingsUseCase(
        settingsRepository: SettingsRepository
    ) = GetSettingsUseCase(settingsRepository)

    @Provides
    fun provideSaveNewSettingsUseCase(
        settingsRepository: SettingsRepository
    ) = SaveNewSettingsUseCase(settingsRepository)
}