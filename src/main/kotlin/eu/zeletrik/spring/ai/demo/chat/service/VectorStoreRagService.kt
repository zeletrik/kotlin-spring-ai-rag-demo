package eu.zeletrik.spring.ai.demo.chat.service

import com.fasterxml.jackson.databind.ObjectMapper
import eu.zeletrik.spring.ai.demo.data.CoffeeDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever
import org.springframework.ai.reader.JsonReader
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.core.io.ByteArrayResource
import org.springframework.stereotype.Service

/**
 * Service for managing chat interactions with retrieval-augmented generation (RAG) capabilities
 * backed by a vector store. This service integrates AI-driven chat functionalities with document
 * retrieval based on vector similarity.
 *
 * The service provides methods for:
 * - Ingesting data into the vector store for use in document retrieval.
 * - Handling user messages in a conversation context, incorporating retrieval-augmented advisors.
 *
 * The chat capability ensures conversation context is maintained using an AI-powered chat client.
 * This enables intelligent responses enriched by retrieval from the vector store.
 *
 * @property vectorStore The vector store used for document storage and retrieval based on similarity search.
 * @property objectMapper The object mapper used for serializing and deserializing data during ingestion.
 * @constructor Initializes `VectorStoreRagService` with required dependencies, setting up the chat client
 * and advisors for RAG capabilities.
 * @param chatClientBuilder Builder for configuring and creating the `ChatClient` used for handling chat interactions.
 * @param chatMemory Memory management for maintaining conversation context across interactions.
 */
@Service
class VectorStoreRagService(
    chatClientBuilder: ChatClient.Builder,
    chatMemory: ChatMemory,
    private val vectorStore: VectorStore,
    private val objectMapper: ObjectMapper
) {

    @Suppress("MagicNumber")
    private val chatClient = chatClientBuilder
        .defaultAdvisors(
            MessageChatMemoryAdvisor.builder(chatMemory).build(),
            RetrievalAugmentationAdvisor.builder()
                .documentRetriever(
                    VectorStoreDocumentRetriever
                        .builder()
                        .vectorStore(vectorStore)
                        .similarityThreshold(0.4)
                        .topK(3)
                        .build()
                )
                .build()
        )
        .build()

    suspend fun ingest(data: CoffeeDetails) = withContext(Dispatchers.IO) {
        val json = objectMapper.writeValueAsString(data)
        val resource = ByteArrayResource(json.toByteArray())
        val documents = JsonReader(resource).get()
        vectorStore.add(documents)
    }

    suspend fun chat(userMessage: String, conversationId: String) = withContext(Dispatchers.IO) {
        chatClient.prompt()
            .advisors {
                it.param(ChatMemory.CONVERSATION_ID, conversationId)
            }
            .user(userMessage).call().content() ?: "Sorry, I don't know that."
    }
}
