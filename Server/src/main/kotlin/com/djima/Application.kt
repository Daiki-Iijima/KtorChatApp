package com.djima

import com.djima.data.MessageDataSource
import com.djima.data.MessageDataSourceImpl
import com.djima.data.model.Message
import com.djima.plugins.*
import com.djima.room.RoomController
import com.djima.routes.chatSocket
import com.djima.routes.getAllMessage
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    //  データベースの設定
    val database = getDatabase()
    val messageCollection: MongoCollection<Message> = database.getCollection("messages")
    val messageDataSource: MessageDataSource = MessageDataSourceImpl(messageCollection)
    val roomController = RoomController(messageDataSource)

    configureSockets()
    configureSecurity()
    configureSerialization()
    configureRouting()
    configureRouting(roomController = roomController)
}

fun getDatabase(): MongoDatabase {
    val connectionString = System.getenv("MONGODB_CONNECTION_STRING")

    val logger = LoggerFactory.getLogger(Application::class.java)
    if(connectionString == null) {
        logger.info("環境変数からMongoDBへのアクセスURLが取得できませんでした")
    }else{
        logger.info("MongoDBへのアクセスURLを取得")
    }

    val settings = MongoClientSettings.builder()
        .applyConnectionString(ConnectionString(connectionString))
        .applyToSslSettings { builder -> builder.enabled(true) }  // SSLを有効にする設定
        .build()

    val client = MongoClient.create(settings)

    return client.getDatabase("chatApp")
}

fun Application.configureRouting(roomController: RoomController) {
    routing {
        chatSocket(roomController)
        getAllMessage(roomController)
    }
}