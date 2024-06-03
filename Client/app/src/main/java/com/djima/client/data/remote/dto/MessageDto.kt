package com.djima.client.data.remote.dto

import com.djima.client.domain.model.Message
import kotlinx.serialization.Serializable
import java.text.DateFormat
import java.util.Date

@Serializable
data class MessageDto(
    val id: String,
    val text: String,
    val username: String,
    val timestamp: Long,
) {
    //  ドメインモデルのMessageクラス用のデータを生成して返す
    fun toMessage(): Message {
        val date = Date(timestamp)
        val formattedDate = DateFormat.getDateInstance(DateFormat.DEFAULT).format(date)
        return Message(text, username, formattedDate)
    }
}