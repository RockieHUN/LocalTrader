package com.example.localtrader.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException


class DataStoreRepository(private val context : Context) {
    private val PREFERENCE_NAME = "preferences"

    private val Context.dataStore : DataStore<Preferences> by preferencesDataStore( name = PREFERENCE_NAME)

    private object PreferenceKeys{
        val locationNoticeDialog = booleanPreferencesKey("locationNoticeDialog")
    }

    suspend fun locationNoticeShown(){
        context.dataStore.edit { preference ->
            preference[PreferenceKeys.locationNoticeDialog] = true
        }
    }

    fun locationNoticeIsShowed() : Flow<Boolean> =
        context.dataStore.data.catch { exception ->

            if (exception is IOException)  {
                emit(emptyPreferences())
            }
            else{
                throw exception
            }
        }
        .map { preference ->
            val isShowed = preference[PreferenceKeys.locationNoticeDialog] ?: false
            isShowed
        }

}