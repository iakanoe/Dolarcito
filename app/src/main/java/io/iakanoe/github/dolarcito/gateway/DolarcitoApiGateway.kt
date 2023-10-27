package io.iakanoe.github.dolarcito.gateway

import io.iakanoe.github.dolarcito.model.ExchangeRate
import retrofit2.http.GET

interface DolarcitoApiGateway {
    @GET("/api/frontend/quotations/dolar")
    suspend fun getRates(): Map<String, ExchangeRate?>
}