package io.iakanoe.github.dolarcito.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import io.iakanoe.github.dolarcito.data.ExchangeRateRepository
import io.iakanoe.github.dolarcito.data.SettingsRepository
import io.iakanoe.github.dolarcito.gateway.DolarcitoApiGateway
import io.iakanoe.github.dolarcito.gateway.SettingsGateway

@Module
@InstallIn(ActivityRetainedComponent::class)
object RepositoryModule {

    @Provides
    @ActivityRetainedScoped
    fun providesExchangeRateRepository(
        dolarcitoApiGateway: DolarcitoApiGateway
    ) = ExchangeRateRepository(dolarcitoApiGateway)

    @Provides
    @ActivityRetainedScoped
    fun providesSettingsRepository(
        settingsGateway: SettingsGateway
    ) = SettingsRepository(settingsGateway)
}