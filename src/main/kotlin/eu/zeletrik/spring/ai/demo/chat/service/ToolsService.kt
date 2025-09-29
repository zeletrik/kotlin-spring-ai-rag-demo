package eu.zeletrik.spring.ai.demo.chat.service

import eu.zeletrik.spring.ai.demo.tools.VectorStoreTool
import eu.zeletrik.spring.ai.demo.tools.WebSearchTool
import eu.zeletrik.spring.ai.demo.web.SearchEngineDocumentRetriever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.stereotype.Service

/**
 * The `ToolsService` class provides a service layer for handling chat interactions that leverage
 * various tools and advisory systems for enhanced conversational capabilities. This service integrates
 * chat-based operations with advanced tools such as a vector-based retrieval system and an internet search tool.
 *
 * @constructor Creates an instance of `ToolsService` with the required dependencies for initial setup.
 *
 * @param chatClientBuilder A builder instance for constructing the `ChatClient`, which is responsible
 * for managing chat prompts and tool integrations.
 * @param chatMemory An instance of `ChatMemory` used for managing conversation context across interactions.
 * @param vectorStore An instance of `VectorStore`, which serves as the basis for vector similarity
 * computation in retrieval operations.
 * @param searchEngineDocumentRetriever An instance of `SearchEngineDocumentRetriever` used for fetching
 * documents and data from external internet searches.
 */
@Service
class ToolsService(
    chatClientBuilder: ChatClient.Builder,
    chatMemory: ChatMemory,
    vectorStore: VectorStore,
    searchEngineDocumentRetriever: SearchEngineDocumentRetriever
) {

    private val chatClient = chatClientBuilder
        .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
        .defaultTools(
            VectorStoreTool(chatClientBuilder.clone(), vectorStore),
            WebSearchTool(chatClientBuilder.clone(), searchEngineDocumentRetriever)
        )
        .build()

    suspend fun chat(userMessage: String, conversationId: String) = withContext(Dispatchers.IO) {
        chatClient.prompt()
            .advisors {
                it.param(ChatMemory.CONVERSATION_ID, conversationId)
            }
            .user(userMessage).call().content() ?: "Sorry, I don't know that."
    }
}
