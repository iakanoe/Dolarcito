package io.iakanoe.github.dolarcito.gateway

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.map

class SettingsGateway(
    private val context: Context,
    private val gson: Gson
) {
    private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private val showingRatesNamesKey = stringPreferencesKey("showingRates")
    private val hiddenRatesNamesKey = stringPreferencesKey("hiddenRates")

    private val stringListTypeToken get() = object : TypeToken<List<String>>() {}.type

    fun retrieveShowingRatesNames() = retrieveRatesNames(showingRatesNamesKey)
    fun retrieveHiddenRatesNames() = retrieveRatesNames(hiddenRatesNamesKey)

    suspend fun saveBothRatesNames(showingRatesNames: List<String>, hiddenRatesNames: List<String>) {
        context.settingsDataStore.edit {
            it[showingRatesNamesKey] = gson.toJson(showingRatesNames)
            it[hiddenRatesNamesKey] = gson.toJson(hiddenRatesNames)
        }
    }

    private fun retrieveRatesNames(key: Preferences.Key<String>) =
        context.settingsDataStore.data
            .map { it[key] ?: "[]" }
            .map { runCatching { gson.fromJson<List<String>>(it, stringListTypeToken) } }
            .map { it.getOrDefault(emptyList()) }
}