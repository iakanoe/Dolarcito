package io.iakanoe.github.dolarcito.data

import io.iakanoe.github.dolarcito.gateway.DolarcitoApiGateway

class ExchangeRateRepository(
    private val apiGateway: DolarcitoApiGateway
) {
    suspend fun getExchangeRates() =
        apiGateway.getRates().values.filterNotNull()
}