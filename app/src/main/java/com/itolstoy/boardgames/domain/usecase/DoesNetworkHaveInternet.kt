package com.itolstoy.boardgames.domain.usecase

import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress
import javax.inject.Inject
import javax.net.SocketFactory

class DoesNetworkHaveInternet @Inject constructor() {

    // Make sure to execute this on a background thread.
    suspend fun execute(/*socketFactory: SocketFactory*/): Boolean {
        return try {
//            val socket = socketFactory.createSocket() ?: throw IOException("Socket is null.")
//            socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
//            socket.close()
//            true
            val timeoutMs = 1500
            val sock = Socket()
            val sockaddr: SocketAddress = InetSocketAddress("8.8.8.8", 53)

            sock.connect(sockaddr, timeoutMs)
            sock.close()

            true
        } catch (e: IOException) {
            false
        }
    }
}