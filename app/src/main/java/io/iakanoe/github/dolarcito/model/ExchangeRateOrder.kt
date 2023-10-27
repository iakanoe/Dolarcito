package io.iakanoe.github.dolarcito.model

data class ExchangeRateOrder(
    val showing: List<ExchangeRate>,
    val hidden: List<ExchangeRate>
)
