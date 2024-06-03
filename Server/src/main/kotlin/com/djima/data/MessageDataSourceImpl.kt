package com.djima.data

import com.djima.data.model.Message
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.toList
import org.bson.Document

class MessageDataSourceImpl(private val messagesCollection: MongoCollection<Message>) : MessageDataSource {

    override suspend fun getAllMessages(): List<Message> {
        return messagesCollection.find().sort(Document("timestamp", -1)).toList()
    }

    override suspend fun insertMessage(message: Message) {
        messagesCollection.insertOne(message)
    }
}