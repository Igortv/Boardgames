package com.itolstoy.boardgames.data.remote

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import com.itolstoy.boardgames.domain.error.NoConnectivityException
import com.itolstoy.boardgames.domain.usecase.DoesNetworkHaveInternet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import javax.inject.Inject

class FirebaseCallExecutor @Inject constructor(
    val context: Context,
    val doesNetworkHaveInternet: DoesNetworkHaveInternet) {
    @Throws(Exception::class)
    suspend fun <T> firebaseCall(call: suspend () -> T): T {
        if (!isConnected()) {
            throw NoConnectivityException()
        } else {
            return call()
        }
    }

    private suspend fun isConnected(): Boolean {
//        val connectivityManager =
//            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val netInfo = connectivityManager.activeNetworkInfo
//        return netInfo != null && netInfo.isConnected
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false

        // Representation of the capabilities of an active network.
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        val hasInternetCapability = activeNetwork?.hasCapability(NET_CAPABILITY_INTERNET)
        if (hasInternetCapability) {
            val hasConnection = CoroutineScope(Dispatchers.IO).async {
                doesNetworkHaveInternet.execute()
            }
            return hasConnection.await()
        } else {
            return false
        }
    }
}

