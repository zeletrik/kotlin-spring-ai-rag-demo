package eu.zeletrik.spring.ai.demo.tools

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever
import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.vectorstore.VectorStore

/**
 * The `VectorStoreTool` class provides functionality to retrieve information related to coffees
 * owned by the user. It leverages a Vector Store for enhanced document retrieval based on query
 * similarity. This tool integrates with a chat client to enable retrieval-augmented operations.
 *
 * @constructor Creates an instance of `VectorStoreTool` with a chat client builder and a vector store.
 *
 * @property chatClientBuilder Builder instance for constructing a `ChatClient`, used to handle prompt-based operations.
 * @property vectorStore Instance of `VectorStore`, used to retrieve documents with similarity-based filtering.
 */
@Suppress("MagicNumber")
class VectorStoreTool(
    private val chatClientBuilder: ChatClient.Builder,
    private val vectorStore: VectorStore
) {

    /**
     * Retrieves information about coffees that the user owns by processing the input query
     * and utilizing a vector-based document retrieval system.
     *
     * @param query The user input specifying the details to be retrieved about their coffees.
     *              This query is used to generate results based on similarity metrics.
     * @return A string containing the retrieved information about the coffees, or null if no results are found.
     */
    @Tool(description = "Retrieve information about coffees that the user owns", returnDirect = false)
    fun getDetailsFromCoffees(query: String): String? {
        return chatClientBuilder.build().prompt()
            .advisors(
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
            .user(query)
            .call()
            .content()
    }
}
