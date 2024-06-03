package com.djima.data

import com.djima.data.model.Message

interface MessageDataSource {
    //  すべてのメッセージを取得
    suspend fun getAllMessages(): List<Message>
    //  メッセージを追加
    suspend fun insertMessage(message: Message)
}