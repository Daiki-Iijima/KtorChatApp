package com.djima.client.data.remote

import com.djima.client.domain.model.Message

interface MessageService {

    suspend fun getAllMessages(): List<Message>

    companion object {
        const val BASE_URL = "https://ktor-chat.onrender.com"
    }

    sealed class Endpoints(val url: String) {
        object GetAllMessages : Endpoints("$BASE_URL/messages")
    }
}