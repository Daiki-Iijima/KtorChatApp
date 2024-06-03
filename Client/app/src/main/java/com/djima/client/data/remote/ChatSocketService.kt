package com.djima.client.data.remote

import com.djima.client.domain.model.Message
import com.djima.client.util.Resource
import kotlinx.coroutines.flow.Flow

interface ChatSocketService {

    suspend fun initSession(
        username: String,
    ): Resource<Unit>

    suspend fun sendMessage(message: String)

    fun observeMessages(): Flow<Message>

    suspend fun closeSession()

    companion object {
        const val BASE_URL = "wss://ktor-chat.onrender.com"
    }

    sealed class Endpoints(val url: String) {
        object ChatSocket : Endpoints("$BASE_URL/chat-socket")
    }
}