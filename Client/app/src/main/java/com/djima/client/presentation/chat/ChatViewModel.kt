package com.djima.client.presentation.chat

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djima.client.data.remote.ChatSocketService
import com.djima.client.data.remote.ChatSocketServiceImpl
import com.djima.client.data.remote.MessageService
import com.djima.client.data.remote.MessageServiceImpl
import com.djima.client.util.Resource
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val _messageText = mutableStateOf("")
    val messageText: State<String> = _messageText

    private val _state = mutableStateOf(ChatState())
    val state: State<ChatState> = _state

    private val client = HttpClient(CIO) {
        install(Logging)
        install(WebSockets)
    }
    private val messageService: MessageService = MessageServiceImpl(client)
    private val chatSocketService: ChatSocketService = ChatSocketServiceImpl(client)

    fun onMessageChange(message: String) {
        _messageText.value = message
    }

    fun sendMessage() {
        viewModelScope.launch {
            if (messageText.value.isNotBlank()) {
                chatSocketService.sendMessage(messageText.value)
            }
        }
    }

    private fun getAllMessages() {
        viewModelScope.launch {
            //  ローディング状態に変更
            _state.value = state.value.copy(isLoading = true)

            //  メッセージをサーバーから取得
            val result = messageService.getAllMessages()

            //  取得したメッセージをstateに反映
            _state.value = state.value.copy(
                messages = result,
                isLoading = false
            )
        }
    }

    fun connectToChat(username: String) {
        getAllMessages()
        viewModelScope.launch {
            val result = chatSocketService.initSession(username)
            //  接続結果によって処理を変える
            when (result) {
                //  接続成功時
                is Resource.Success -> {
                    chatSocketService.observeMessages()
                        .onEach { messages ->
                            //  現在の状態のメッセージに追加でメッセージを追加
                            val newList = state.value.messages.toMutableList().apply {
                                add(0, messages)
                            }
                            //  stateに反映
                            _state.value = state.value.copy(messages = newList)
                        }.launchIn(viewModelScope)
                }

                is Resource.Error -> {
                    //  接続失敗をトーストで通知
                }
            }
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            chatSocketService.closeSession()
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }
}