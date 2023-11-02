package io.iakanoe.github.dolarcito.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.iakanoe.github.dolarcito.data.ExchangeRateRepository
import io.iakanoe.github.dolarcito.data.SettingsRepository
import io.iakanoe.github.dolarcito.gateway.DolarcitoApiGateway
import io.iakanoe.github.dolarcito.gateway.SettingsGateway

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun providesExchangeRateRepository(
        dolarcitoApiGateway: DolarcitoApiGateway
    ) = ExchangeRateRepository(dolarcitoApiGateway)

    @Provides
    fun providesSettingsRepository(
        settingsGateway: SettingsGateway
    ) = SettingsRepository(settingsGateway)
}