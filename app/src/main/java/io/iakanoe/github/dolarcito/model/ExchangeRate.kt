package io.iakanoe.github.dolarcito.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ExchangeRate(
    val name: String,
    val buy: Float?,
    val sell: Float?,
    val timestamp: Long,
    val variation: Float?,
    val spread: Float?,
    @SerializedName("volumen") val volume: Long?
)
