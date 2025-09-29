package eu.zeletrik.spring.ai.demo.tools

import eu.zeletrik.spring.ai.demo.transformer.MemoryAwareQueryRewriter
import eu.zeletrik.spring.ai.demo.web.SearchEngineDocumentRetriever
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor
import org.springframework.ai.tool.annotation.Tool

/**
 * The `WebSearchTool` class provides functionality to retrieve information from the internet
 * by leveraging a document retrieval system integrated with chat-based prompts. It uses a search
 * engine document retriever for fetching relevant data and enhances user queries with
 * memory-aware transformations.
 *
 * @constructor Creates an instance of `WebSearchTool` with a chat client builder and search engine document retriever.
 *
 * @property chatClientBuilder A builder instance for `ChatClient`, used for handling prompt-based operations.
 * @property searchEngineDocumentRetriever Used to fetch relevant documents by executing queries on a search engine.
 */
@Suppress("MagicNumber")
class WebSearchTool(
    private val chatClientBuilder: ChatClient.Builder,
    private val searchEngineDocumentRetriever: SearchEngineDocumentRetriever
) {

    /**
     * Retrieves information from the internet by performing a search based on the provided query.
     * The method utilizes a memory-aware query transformation system along with a search engine
     * document retriever to fetch relevant and useful content.
     *
     * @param query The query string to search the internet for. This input is processed to generate
     *              appropriate search results.
     * @return A string containing the retrieved content from the internet, or null if no results
     *         are found.
     */
    @Tool(description = "Retrieve information from searching the internet", returnDirect = false)
    fun getDetailsFromInternetSearch(query: String): String? {
        return chatClientBuilder.build().prompt()
            .advisors(
                RetrievalAugmentationAdvisor.builder()
                    .queryTransformers(MemoryAwareQueryRewriter(chatClientBuilder.clone()))
                    .documentRetriever(searchEngineDocumentRetriever)
                    .build()
            )
            .user(query)
            .call()
            .content()
    }
}
