package com.djima.plugins

import com.djima.session.ChatSession
import io.ktor.server.application.*
import io.ktor.server.application.ApplicationCallPipeline.ApplicationPhase.Plugins
import io.ktor.server.sessions.*
import io.ktor.util.*

fun Application.configureSecurity() {
    install(Sessions) {
        cookie<ChatSession>("SESSION")
    }

    intercept(Plugins) {
        //  セッションがまだ確立していない場合
        if (call.sessions.get<ChatSession>() == null) {
            //  リクエストパラメータからusername情報を抽出
            val username = call.parameters["username"] ?: "Guest"
            call.sessions.set(
                ChatSession(
                    username = username,
                    generateNonce()
                )
            )
        }
    }
}
