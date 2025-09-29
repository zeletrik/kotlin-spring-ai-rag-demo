package eu.zeletrik.spring.ai.demo.data

/**
 * Represents the types of Retrieval-Augmented Generation (RAG) mechanisms or states
 * supported by the application.
 *
 * RAG helps enhance AI chat systems by integrating memory, context, or external information
 * for producing more meaningful and relevant responses.
 *
 * Enum Constants:
 * - `DISABLED`: Indicates that RAG functionality is turned off.
 * - `WEB_SEARCH`: Enables integration of web search to augment AI responses.
 * - `VECTOR_STORE`: Utilizes vector-based retrieval systems for context-aware responses.
 * - `TOOLS`: Activates the use of additional tools for enhanced conversational capabilities.
 */
enum class RagTypes {
    DISABLED,
    WEB_SEARCH,
    VECTOR_STORE,
    TOOLS
}
