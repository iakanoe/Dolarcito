package io.iakanoe.github.dolarcito.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import io.iakanoe.github.dolarcito.data.ExchangeRateRepository
import io.iakanoe.github.dolarcito.data.SettingsRepository
import io.iakanoe.github.dolarcito.domain.GetOrderedExchangeRatesUseCase
import io.iakanoe.github.dolarcito.domain.GetSettingsUseCase
import io.iakanoe.github.dolarcito.domain.SaveNewSettingsUseCase

@Module
@InstallIn(ActivityRetainedComponent::class)
object UseCaseModule {

    @Provides
    @ActivityRetainedScoped
    fun provideGetOrderedExchangeRatesUseCase(
        settingsRepository: SettingsRepository,
        exchangeRateRepository: ExchangeRateRepository
    ) = GetOrderedExchangeRatesUseCase(
        settingsRepository,
        exchangeRateRepository
    )

    @Provides
    @ActivityRetainedScoped
    fun provideGetSettingsUseCase(
        settingsRepository: SettingsRepository
    ) = GetSettingsUseCase(settingsRepository)

    @Provides
    @ActivityRetainedScoped
    fun provideSaveNewSettingsUseCase(
        settingsRepository: SettingsRepository
    ) = SaveNewSettingsUseCase(settingsRepository)
}