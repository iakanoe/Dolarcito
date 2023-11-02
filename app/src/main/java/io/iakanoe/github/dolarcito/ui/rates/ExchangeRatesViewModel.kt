package io.iakanoe.github.dolarcito.ui.rates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.iakanoe.github.dolarcito.domain.GetOrderedExchangeRatesUseCase
import io.iakanoe.github.dolarcito.model.ExchangeRate
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ExchangeRatesViewModel @Inject constructor(
    private val getOrderedExchangeRatesUseCase: GetOrderedExchangeRatesUseCase
) : ViewModel() {

    private val _viewState = MutableStateFlow<ExchangeRatesViewState>(ExchangeRatesViewState.Loading)
    val viewState get() = _viewState.asStateFlow()

    private var waitingJob: Job? = null

    init {
        update()
    }

    fun update() {
        waitingJob?.cancel()
        waitingJob = viewModelScope.launch {
            _viewState.value = ExchangeRatesViewState.Loading

            try {
                val order = getOrderedExchangeRatesUseCase.execute()
                _viewState.value = ExchangeRatesViewState.Loaded(
                    exchangeRates = order.showing,
                    hiddenExchangeRates = order.hidden,
                    updatedTime = Calendar.getInstance().timeInMillis
                )
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
                _viewState.value = ExchangeRatesViewState.Error
            }

            delay(5 * 60000L)
            update()
        }
    }
}

sealed class ExchangeRatesViewState {
    data object Loading : ExchangeRatesViewState()

    data object Error : ExchangeRatesViewState()

    data class Loaded(
        val exchangeRates: List<ExchangeRate>,
        val hiddenExchangeRates: List<ExchangeRate>,
        val updatedTime: Long
    ) : ExchangeRatesViewState()
}