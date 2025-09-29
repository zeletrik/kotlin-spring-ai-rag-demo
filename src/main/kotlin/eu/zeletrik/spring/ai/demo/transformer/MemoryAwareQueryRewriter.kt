package eu.zeletrik.spring.ai.demo.transformer

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.rag.Query
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer

/**
 * MemoryAwareQueryRewriter is responsible for transforming user queries into self-contained,
 * search-optimized queries by leveraging conversation history and adhering to specific rewriting rules.
 * This transformation ensures that the resulting query is well-formed and ready to be processed
 * by external systems like search engines.
 *
 * The rewriter utilizes a chat client to process and rewrite the user's query based on the context
 * provided by the conversation history, resolving references and pronouns as necessary. The output
 * is designed to incorporate precise entity names, geographic qualifiers, and keywords that are
 * beneficial for search purposes.
 *
 * @constructor Creates an instance of MemoryAwareQueryRewriter using a ChatClient.Builder.
 *
 * @param chatClientBuilder A builder for constructing a ChatClient instance, which facilitates
 *                          the generation of the rewritten query through prompt-based operations.
 */
class MemoryAwareQueryRewriter(
    chatClientBuilder: ChatClient.Builder
) : QueryTransformer {

    private val chatClient = chatClientBuilder
        .build()

    /**
     * Transforms the given query into a rewritten query string that is self-contained and optimized
     * for a web search. Pronouns and references in the query are resolved using the conversation history.
     *
     * @param query the query object containing the original user question, conversation history,
     * and additional context.
     * @return a new query object containing the rewritten query string, along with the original
     * history and context.
     */
    override fun transform(query: Query): Query {
        val historyText = query.history.joinToString("\n") { msg ->
            "${msg.messageType}: ${msg.text}"
        }

        val prompt = """
            You rewrite user questions into one, self-contained web search query.
            - Resolve pronouns and references using the provided conversation history.
            - Include exact entity names and geo qualifiers when known.
            - Prefer keywords useful for search (e.g., "address", "headquarters", "location", "official site").
            - Output only the final query string. No explanations.

            Conversation history:
            $historyText

            Current user question:
            ${query.text}
        """.trimIndent()

        val rewritten = chatClient
            .prompt()
            .user(prompt)
            .call()
            .content()
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?: query.text

        return Query(rewritten, query.history, query.context)
    }
}
