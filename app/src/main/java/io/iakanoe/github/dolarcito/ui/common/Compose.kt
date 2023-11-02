package io.iakanoe.github.dolarcito.ui.common

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import java.text.DecimalFormat
import java.util.Calendar

val Int.minutesText
    get() = when (this) {
        0 -> "hace menos de un minuto"
        1 -> "hace un minuto"
        else -> "hace $this minutos"
    }

val Float.priceText: String
    get() = DecimalFormat("$#.##")
        .format(this)

val Long.largeNumberText: String
    get() = DecimalFormat("#,###")
        .format(this)

val currentTime = flow {
    while (true) {
        delay(1000)
        emit(Calendar.getInstance().timeInMillis)
    }
}