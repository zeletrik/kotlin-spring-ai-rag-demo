package eu.zeletrik.spring.ai.demo.chat.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.stereotype.Service

/**
 * A service responsible for managing chat functionalities using an AI-powered chat client.
 *
 * This class leverages a `ChatClient` to handle user messages and produce responses. It supports
 * linking chats to specific conversations via a unique conversation ID, ensuring context persistence.
 *
 * @constructor Creates a `ChatService` instance configured with a prebuilt `ChatClient`.
 * @param chatClientBuilder A builder to configure and create the `ChatClient`.
 * @param chatMemory Memory management for chat conversations to maintain context and state across interactions.
 */
@Service
class ChatService(
    chatClientBuilder: ChatClient.Builder,
    chatMemory: ChatMemory
) {

    private val chatClient = chatClientBuilder
        .defaultAdvisors(
            MessageChatMemoryAdvisor.builder(chatMemory).build()
        )
        .build()

    suspend fun chat(userMessage: String, conversationId: String) =
        withContext(Dispatchers.IO) {
            chatClient.prompt()
                .advisors {
                    it.param(
                        ChatMemory.CONVERSATION_ID,
                        conversationId
                    )
                }
                .user(userMessage).call().content()
                ?: "Sorry, I don't know that."
        }
}
