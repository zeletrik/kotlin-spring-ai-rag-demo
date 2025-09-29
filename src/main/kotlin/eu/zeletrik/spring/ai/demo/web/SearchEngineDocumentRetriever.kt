package eu.zeletrik.spring.ai.demo.web

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import org.slf4j.LoggerFactory
import org.springframework.ai.document.Document
import org.springframework.ai.rag.Query
import org.springframework.ai.rag.retrieval.search.DocumentRetriever
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

private const val MAX_RESULTS = 5

/**
 * SearchEngineDocumentRetriever is a component responsible for retrieving documents from an external
 * search engine API based on a given query. It implements the `DocumentRetriever` interface, providing
 * the ability to fetch relevant search results and transform them into a list of documents.
 *
 * @constructor Initializes the SearchEngineDocumentRetriever with the specified API key and WebClient.Builder.
 * Configures a WebClient instance to send requests to the search engine API with the required authentication.
 *
 * @param apiKey The API key used for authenticating requests to the search engine.
 * @param webClientBuilder Builder for creating a WebClient configured for communication with the search engine API.
 *
 * Functions:
 * - `retrieve(query: Query)`: Given a query, sends a request to the search engine API and fetches a list of documents.
 *   Maps the API response to a list of `Document` instances containing relevant content, metadata, and a score.
 *
 * Logging:
 * Logs information about the query being processed and the results retrieved, including document titles and URLs.
 *
 * Companion Object:
 * - Contains a logger instance (`LOG`) for logging query and result details.
 */
@Component
class SearchEngineDocumentRetriever(
    @param:Value("\${tavily.api-key}") private val apiKey: String,
    private val webClientBuilder: WebClient.Builder
) : DocumentRetriever {

    private val webClient = webClientBuilder.baseUrl("https://api.tavily.com/search")
        .defaultHeaders { it.setBearerAuth(apiKey) }
        .build()

    override fun retrieve(query: Query): List<Document?> {
        LOG.info("Retrieving documents for query: $query")
        val request = TavilySearchRequest(
            query = query.text,
            "advanced",
            MAX_RESULTS
        )

        val response = webClient.post()
            .bodyValue(request)
            .retrieve()
            .bodyToMono<TavilySearchResponse>()
            .block()

        return response?.results?.map { result ->
            LOG.info("Found document: ${result.title} (${result.url})")
            Document.builder()
                .text(result.content)
                .metadata("title", result.title)
                .metadata("url", result.url)
                .score(result.score)
                .build()
        } ?: emptyList()
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(SearchEngineDocumentRetriever::class.java)
    }
}

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class TavilySearchRequest(val query: String?, val searchDepth: String?, val maxResults: Int)

data class TavilySearchResponse(val results: List<Result>) {
    data class Result(val title: String, val url: String, val content: String, val score: Double)
}
