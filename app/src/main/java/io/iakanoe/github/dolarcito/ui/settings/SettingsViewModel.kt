package io.iakanoe.github.dolarcito.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.iakanoe.github.dolarcito.domain.GetSettingsUseCase
import io.iakanoe.github.dolarcito.domain.SaveNewSettingsUseCase
import io.iakanoe.github.dolarcito.model.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
    val tmp = this[index1]
    this[index1] = this[index2]
    this[index2] = tmp
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val saveNewSettingsUseCase: SaveNewSettingsUseCase
) : ViewModel() {

    private val _viewState = MutableStateFlow<SettingsViewState>(SettingsViewState.Loading)
    val viewState get() = _viewState.asStateFlow()

    init {
        getData()
    }

    private fun getData() {
        viewModelScope.launch {
            try {
                val actualSettings = getSettingsUseCase.execute().first()
                _viewState.value = SettingsViewState.Loaded(
                    showingRates = actualSettings.showingRatesNames,
                    hiddenRates = actualSettings.hiddenRatesNames
                )
            } catch (exception: Exception) {
                exception.printStackTrace()
                _viewState.value = SettingsViewState.RetrievingError
            }
        }
    }

    fun moveItem(name: String, up: Boolean) {
        val order = _viewState.value as? SettingsViewState.Loaded ?: throw RuntimeException()
        val showing = order.showingRates.toMutableList()
        val hidden = order.hiddenRates.toMutableList()

        if (name in showing) {
            val index = showing.indexOf(name)

            if (index == 0 && up) {
                return
            } else if (index == (showing.size - 1) && !up) {
                hidden.add(0, showing.removeAt(index))
            } else {
                showing.swap(index, index + (if (up) -1 else 1))
            }
        } else if (name in hidden) {
            val index = hidden.indexOf(name)

            if (index == 0 && up) {
                showing.add(hidden.removeAt(index))
            } else if (index == (hidden.size - 1) && !up) {
                return
            } else {
                hidden.swap(index, index + (if (up) -1 else 1))
            }
        } else throw RuntimeException()

        _viewState.value = SettingsViewState.Loaded(showing, hidden)
    }

    fun hideItem(name: String) {
        val order = _viewState.value as? SettingsViewState.Loaded ?: throw RuntimeException()
        val showing = order.showingRates.toMutableList()
        val hidden = order.hiddenRates.toMutableList()

        if (name in showing) hidden.add(showing.removeAt(showing.indexOf(name)))
        else throw RuntimeException()

        _viewState.value = SettingsViewState.Loaded(showing, hidden)
    }

    fun showItem(name: String) {
        val order = _viewState.value as? SettingsViewState.Loaded ?: throw RuntimeException()
        val showing = order.showingRates.toMutableList()
        val hidden = order.hiddenRates.toMutableList()

        if (name in hidden) showing.add(hidden.removeAt(hidden.indexOf(name)))
        else throw RuntimeException()

        _viewState.value = SettingsViewState.Loaded(showing, hidden)
    }

    fun saveNewOrder() {
        viewModelScope.launch {
            try {
                val newOrder = _viewState.value as? SettingsViewState.Loaded ?: throw RuntimeException()
                saveNewSettingsUseCase.execute(
                    Settings(newOrder.showingRates, newOrder.hiddenRates)
                )

                _viewState.value = SettingsViewState.Saved
            } catch (exception: Exception) {
                exception.printStackTrace()
                _viewState.value = SettingsViewState.SavingError
            }
        }
    }
}

sealed class SettingsViewState {
    data object Loading : SettingsViewState()

    data object SavingError : SettingsViewState()

    data object RetrievingError : SettingsViewState()

    data object Saved : SettingsViewState()

    data class Loaded(
        val showingRates: List<String>,
        val hiddenRates: List<String>,
    ) : SettingsViewState()
}