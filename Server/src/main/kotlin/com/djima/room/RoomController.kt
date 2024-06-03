package com.djima.room

import com.djima.data.MessageDataSource
import com.djima.data.model.Message
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

class RoomController(
    private val messageDataSource: MessageDataSource,
) {
    private val members = ConcurrentHashMap<String, Member>()

    //  メンバーに追加
    fun onJoin(
        username: String,
        sessionId: String,
        socket: WebSocketSession,
    ) {
        //  すでにメンバーに所属している場合、例外を発行
        if (members.containsKey(username)) {
            throw MemberAlreadyExistsException()
        }

        //  メンバーに追加
        members[username] = Member(username = username, sessionId = sessionId, socket = socket)
    }

    suspend fun sendMessage(senderUserName: String, message: String) {
        //  メッセージを生成
        val messageEntity = Message(
            text = message,
            username = senderUserName,
            timestamp = System.currentTimeMillis(),
        )

        //  メッセージをデータベースに保存
        messageDataSource.insertMessage(messageEntity)

        //  メンバー全員へWebSocketを通して送信
        members.values.forEach { member ->
            val parsedMessage = Json.encodeToString(messageEntity)
            member.socket.send(Frame.Text(parsedMessage))
        }
    }

    suspend fun getAllMessages(): List<Message> {
        return messageDataSource.getAllMessages()
    }

    suspend fun tryDisconnect(username: String) {
        //  WebSocketを閉じる
        members[username]?.socket?.close()
        //  メンバーリストにいたら、除名
        if(members.containsKey(username)) {
            members.remove(username)
        }
    }
}