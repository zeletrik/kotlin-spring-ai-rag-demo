package eu.zeletrik.spring.ai.demo.chat

import eu.zeletrik.spring.ai.demo.chat.service.ChatService
import eu.zeletrik.spring.ai.demo.chat.service.ToolsService
import eu.zeletrik.spring.ai.demo.chat.service.VectorStoreRagService
import eu.zeletrik.spring.ai.demo.chat.service.WebSearchRagService
import eu.zeletrik.spring.ai.demo.data.CoffeeDetails
import eu.zeletrik.spring.ai.demo.data.RagTypes
import org.springframework.stereotype.Component

/**
 * A facade responsible for managing conversations across multiple processing types.
 * It centralizes the chat functionality using different strategies based on the specified type
 * and supports data ingestion for certain strategies.
 *
 * @param chatService Handles basic conversational functionality without additional data augmentation.
 * @param webSearchRagService Supports web search-based retrieval-augmented generation (RAG).
 * @param vectorStoreRagService Handles vector store-based RAG and data ingestion capabilities.
 * @param toolsService Manages advanced tools for augmented interactions, combining multiple techniques.
 */
@Component
class ConversationFacade(
    private val chatService: ChatService,
    private val webSearchRagService: WebSearchRagService,
    private val vectorStoreRagService: VectorStoreRagService,
    private val toolsService: ToolsService,
) {

    /**
     * Handles a user query by delegating it to the appropriate service based on the specified type.
     * Supports different strategies for processing the query, such as basic chat, web-based search,
     * vector store retrieval-augmented generation, or advanced tools.
     *
     * @param question The user query or question to process.
     * @param type The processing strategy to be used. Supported values are defined in the `RagTypes` enum:
     *             - `DISABLED` for basic chat.
     *             - `WEB_SEARCH` for web-based retrieval-augmented generation.
     *             - `VECTOR_STORE` for vector store-based retrieval-augmented generation.
     *             - `TOOLS` for advanced, multi-technique augmented interactions.
     * @param conversationId A unique identifier for the conversation to maintain context across interactions.
     */
    suspend fun ask(question: String, type: String, conversationId: String) =
        when (RagTypes.valueOf(type)) {
            RagTypes.DISABLED -> chatService.chat(question, conversationId)
            RagTypes.WEB_SEARCH -> webSearchRagService.chat(question, conversationId)
            RagTypes.VECTOR_STORE -> vectorStoreRagService.chat(question, conversationId)
            RagTypes.TOOLS -> toolsService.chat(question, conversationId)
        }

    /**
     * Ingests coffee details into the vector store for further processing or retrieval.
     *
     * @param coffeeDetails The details of the coffee, including origin, name, taste notes, roast date, and roaster.
     *
     * Note: This method is only placed here for demonstration purposes.
     * It is not intended to be used in production but populating the vector store when actually needed.
     */
    suspend fun ingest(coffeeDetails: CoffeeDetails) = vectorStoreRagService.ingest(coffeeDetails)
}
