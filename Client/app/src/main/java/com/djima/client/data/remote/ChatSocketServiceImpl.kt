package com.djima.client.data.remote

import com.djima.client.data.remote.dto.MessageDto
import com.djima.client.domain.model.Message
import com.djima.client.util.Resource
import io.ktor.client.*
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.headers
import io.ktor.client.request.url
import io.ktor.client.utils.EmptyContent.headers
import io.ktor.http.HttpHeaders
import io.ktor.websocket.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json
import java.net.URLEncoder

class ChatSocketServiceImpl(
    private val client: HttpClient
) : ChatSocketService {

    private var socket: WebSocketSession? = null

    override suspend fun initSession(username: String): Resource<Unit> {
        return try {
            //  接続
            socket = client.webSocketSession {
                //  usernameが日本語の場合は、UTF-8にエンコードして送信しないとだめ
                url("${ChatSocketService.Endpoints.ChatSocket.url}?username=${URLEncoder.encode(username, "UTF-8")}")
                //  日本語の文字化け対策
                headers {
                    append(HttpHeaders.AcceptCharset, Charsets.UTF_8.name())
                }
            }

            //  接続が確立していたら
            if (socket?.isActive == true) {
                Resource.Success(Unit)
            } else {
                //  失敗したら
                Resource.Error("WebSocketの接続を確立できませんでした")
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.localizedMessage ?: "Unknown error")
        }
    }

    override suspend fun sendMessage(message: String) {
        try {
            socket?.send(Frame.Text(message))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun observeMessages(): Flow<Message> {
        return try {
            socket?.incoming
                ?.receiveAsFlow()
                ?.filter { it is Frame.Text }
                ?.map {
                    val json = (it as? Frame.Text)?.readText() ?: ""
                    val messageDto = Json.decodeFromString<MessageDto>(json)
                    messageDto.toMessage()
                } ?: flow { }
        } catch (e: Exception) {
            e.printStackTrace()
            flow { }
        }
    }

    override suspend fun closeSession() {
        socket?.close()
    }
}