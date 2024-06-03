package com.djima.client.presentation.chat

import com.djima.client.domain.model.Message

data class ChatState(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
)