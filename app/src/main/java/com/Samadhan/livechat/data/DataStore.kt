package com.Samadhan.livechat.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class DataStore(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("ChatApplicationData")
    }

    fun getIpsaDataStoreVal(key : String): Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[stringPreferencesKey(key)] ?: ""
        }

    suspend fun setIpsaDataStoreVal(key : String, dataVal : String) {
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = dataVal
        }
    }

    suspend fun clearIpsaDataStore() {
        context.dataStore.edit {
            it.clear()
        }
    }

}


