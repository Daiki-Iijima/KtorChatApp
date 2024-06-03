package com.djima.client.data.remote

import com.djima.client.data.remote.dto.MessageDto
import com.djima.client.domain.model.Message
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json

class MessageServiceImpl(
    private val client: HttpClient
) : MessageService {
    override suspend fun getAllMessages(): List<Message> {
        return try {
            val response = client.get(MessageService.Endpoints.GetAllMessages.url)
            if (response.status == HttpStatusCode.OK) {
                val responseBody: String = response.body<String>()
                Json.decodeFromString<List<MessageDto>>(responseBody).map { it.toMessage() }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}