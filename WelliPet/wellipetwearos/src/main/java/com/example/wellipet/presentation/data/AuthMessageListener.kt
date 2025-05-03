// File: com/example/wellipet/presentation/data/AuthMessageListener.kt
package com.example.wellipet.presentation.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.wellipet.data.dataStore      // ← import 统一的 dataStore
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

// DataStore for simplicity
private val UID_KEY = stringPreferencesKey("user_uid")

class AuthMessageListener(private val context: Context) : MessageClient.OnMessageReceivedListener {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onMessageReceived(event: MessageEvent) {
        Log.d("WatchAuth", "👉 收到路径：${event.path}")
        if (event.path == "/auth-uid") {
            val uid = String(event.data)
            scope.launch {
                context.dataStore.edit { prefs ->
                    prefs[UID_KEY] = uid
                }
                Log.d("WatchAuth", "✅ uid 存入 DataStore")
            }
        }
    }
}