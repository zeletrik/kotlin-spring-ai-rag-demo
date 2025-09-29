package eu.zeletrik.spring.ai.demo.chat.service

import eu.zeletrik.spring.ai.demo.transformer.MemoryAwareQueryRewriter
import eu.zeletrik.spring.ai.demo.web.SearchEngineDocumentRetriever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor
import org.springframework.stereotype.Service

/**
 * WebSearchRagService is a service that enables AI-powered chat interactions augmented with web search capabilities.
 *
 * This class integrates a `ChatClient` with retrieval-augmented generation (RAG) functionality,
 * utilizing a search engine document retriever and memory-aware query rewriting. It facilitates contextual,
 * conversation-aware responses by combining conversation memory and real-time information retrieval.
 *
 * Key Functionality:
 * - Leverages a `ChatClient` with two default advisors:
 *   1. `MessageChatMemoryAdvisor`: Ensures conversation memory is maintained across user messages.
 *   2. `RetrievalAugmentationAdvisor`: Augments responses by rewriting queries with memory context and
 *      retrieving relevant documents from external search engines.
 * - Handles chat interactions asynchronously, using the `Dispatchers.IO` coroutine context.
 *
 * Dependencies:
 * - `ChatClient.Builder`: Configures and builds the `ChatClient` with custom advisors.
 * - `ChatMemory`: Provides storage and retrieval for conversation context.
 * - `SearchEngineDocumentRetriever`: Retrieves relevant documents from an external search engine to augment responses.
 *
 * @constructor Initializes the `WebSearchRagService` with the required dependencies.
 * @param chatClientBuilder A builder used to configure and build the `ChatClient` instance with RAG capabilities.
 * @param chatMemory A memory implementation for managing conversation state across messages.
 * @param searchEngineDocumentRetriever A document retriever responsible for fetching external information.
 */
@Service
class WebSearchRagService(
    chatClientBuilder: ChatClient.Builder,
    chatMemory: ChatMemory,
    searchEngineDocumentRetriever: SearchEngineDocumentRetriever
) {

    private val chatClient = chatClientBuilder
        .defaultAdvisors(
            MessageChatMemoryAdvisor.builder(chatMemory).build(),
            RetrievalAugmentationAdvisor.builder()
                .queryTransformers(MemoryAwareQueryRewriter(chatClientBuilder.clone()))
                .documentRetriever(searchEngineDocumentRetriever)
                .build()
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
