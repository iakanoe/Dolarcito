package io.iakanoe.github.dolarcito.widget

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.iakanoe.github.dolarcito.domain.GetOrderedExchangeRatesUseCase

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ExchangeRatesWidgetEntryPoint {
    fun getOrderedExchangeRatesUseCase(): GetOrderedExchangeRatesUseCase
}