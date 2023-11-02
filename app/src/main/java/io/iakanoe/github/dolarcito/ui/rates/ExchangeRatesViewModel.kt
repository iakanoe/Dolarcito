package io.iakanoe.github.dolarcito.ui.rates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.iakanoe.github.dolarcito.domain.GetOrderedExchangeRatesUseCase
import io.iakanoe.github.dolarcito.model.ExchangeRate
import io.iakanoe.github.dolarcito.model.ExchangeRateOrder
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
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
        viewModelScope.launch {
            getOrderedExchangeRatesUseCase.execute()
                .map<ExchangeRateOrder, ExchangeRatesViewState> {
                    ExchangeRatesViewState.Loaded(
                        exchangeRates = it.showing,
                        hiddenExchangeRates = it.hidden,
                        updatedTime = Calendar.getInstance().timeInMillis
                    )
                }
                .onStart { emit(ExchangeRatesViewState.Loading) }
                .catch {
                    it.printStackTrace()
                    emit(ExchangeRatesViewState.Error)
                }
                .onCompletion {
                    waitingJob = launch {
                        delay(5 * 60000L)
                        update()
                    }
                }
                .collect { _viewState.emit(it) }
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