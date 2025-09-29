# Spring AI RAG Demo with Kotlin

A comprehensive demonstration project showcasing Retrieval Augmented Generation (RAG) capabilities using Spring AI
framework with Kotlin. This project demonstrates four different RAG approaches: basic chat, web search-based RAG, vector
store-based RAG, and intelligent tool selection.

## 🚀 Features

### RAG Capabilities

This demo implements four distinct chat modes to showcase different RAG approaches:

1. **DISABLED** - Basic AI chat without augmentation
2. **WEB_SEARCH** - Real-time web search integration using Tavily API
3. **VECTOR_STORE** - Document retrieval from PostgreSQL with PGVector
4. **TOOLS** - Intelligent tool selection combining both web search and vector store

### Core Components

- **Spring AI Integration** - Built on Spring AI framework with OpenAI GPT-4o-mini
- **Vector Database** - PostgreSQL with PGVector extension for similarity search
- **Web Search** - Tavily API integration for real-time information retrieval
- **Memory Management** - Conversation context preservation across interactions
- **Interactive Web UI** - HTMX-powered chat interface

## 🛠 Technology Stack

- **Backend**: Spring Boot 3.x with Kotlin
- **AI Framework**: Spring AI with OpenAI integration
- **Database**: PostgreSQL 17 with PGVector extension
- **Search API**: Tavily for web search capabilities
- **Frontend**: Server-side rendered HTML with HTMX
- **Documentation**: SpringDoc OpenAPI 3
- **Monitoring**: Spring Boot Actuator with Prometheus
- **Infrastructure**: Docker Compose

## 📋 Prerequisites

- Java 17 or higher
- Docker and Docker Compose
- OpenAI API key
- Tavily Search API key

## 🚦 Quick Start

### 1. Environment Setup

Make sure to set the following environment variables:

```bash
OPENAI_API_KEY=your_openai_api_key_here
TAVILY_SEARCH_API_KEY=your_tavily_api_key_here
```

### 2. Database Setup

Start the PostgreSQL database with PGVector:

```bash
docker-compose up -d
```

This will start:

- PostgreSQL 17 with PGVector extension
- Database: `demo`
- Port: `5432`
- Credentials: `postgres/postgres`

### 3. Application Startup

Run the Spring Boot application:

```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

## 🎮 Usage

### Web Interface

1. Navigate to http://localhost:8080
2. Choose a chat type from the available options:
    - `/chat/DISABLED` - Basic chat
    - `/chat/WEB_SEARCH` - Web search enhanced chat
    - `/chat/VECTOR_STORE` - Vector store enhanced chat
    - `/chat/TOOLS` - AI-powered tool selection

#### Data Ingestion

```http
# Ingest coffee data into vector store
POST /ingest
Content-Type: application/json

{
  "origin": "Ethiopia",
  "name": "Yirgacheffe",
  "tasteNotes": ["floral", "citrus", "bright acidity"],
  "roastDate": "2024-08-25",
  "roaster": "Local Roasters"
}
```

### Sample Data Ingestion

To demonstrate vector store capabilities, ingest some coffee data:

```bash
curl -X POST http://localhost:5000/ingest \
  -H "Content-Type: application/json" \
  -d '{
    "origin": "Colombia",
    "name": "Huila",
    "tasteNotes": ["chocolate", "caramel", "nutty"],
    "roastDate": "2024-08-20",
    "roaster": "Specialty Coffee Co"
  }'
```

## 🏗 Architecture

### RAG Implementation Details

#### 1. Basic Chat (DISABLED)

- Direct OpenAI GPT-4o-mini integration
- No document retrieval or augmentation
- Conversation memory maintained

#### 2. Web Search RAG (WEB_SEARCH)

- Real-time web search using Tavily API
- Memory-aware query rewriting
- Search results augment AI responses
- Contextual conversation history

#### 3. Vector Store RAG (VECTOR_STORE)

- Document storage in PostgreSQL with PGVector
- Semantic similarity search (threshold: 0.4, top-K: 3)
- Text embedding using OpenAI text-embedding-3-small
- 768-dimensional vectors with HNSW indexing

#### 4. Tools Mode (TOOLS)

- AI-powered tool selection
- Combines both web search and vector store capabilities
- Dynamic tool invocation based on query context
- **VectorStoreTool**: Retrieves coffee information from vector store
- **WebSearchTool**: Performs internet searches for current information

### Key Components

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Web UI        │────│ ConversationFacade│────│ RAG Services    │
│ (HTMX + HTML)   │    │                  │    │                 │
└─────────────────┘    └──────────────────┘    └─────────────────┘
                                │                        │
                                │                        ├── ChatService
                                │                        ├── WebSearchRagService
                                │                        ├── VectorStoreRagService
                                │                        └── ToolsService
                                │
                       ┌──────────────────┐
                       │ External APIs    │
                       ├── OpenAI API     │
                       ├── Tavily API     │
                       └── PostgreSQL     │
                           (PGVector)     │
```

### Configuration

The application uses the following AI models and settings:

- **Chat Model**: GPT-4o-mini (max 1500 tokens)
- **Embedding Model**: text-embedding-3-small (768 dimensions)
- **Vector Store**: PostgreSQL PGVector with HNSW indexing
- **Similarity Threshold**: 0.4
- **Retrieval Count**: Top 3 most similar documents

## 🔧 Configuration

### Application Properties

Key configuration in `application.yaml`:

```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: gpt-4o-mini
          max-tokens: 1500
      embedding:
        options:
          model: text-embedding-3-small
          dimensions: 768
    vector-store:
      pgvector:
        dimensions: 768
        index-type: hnsw

tavily:
  api-key: ${TAVILY_SEARCH_API_KEY}
```

### Environment Variables

| Variable                | Description                           | Required                          |
|-------------------------|---------------------------------------|-----------------------------------|
| `OPENAI_API_KEY`        | OpenAI API key for GPT and embeddings | Yes                               |
| `TAVILY_SEARCH_API_KEY` | Tavily API key for web search         | Yes                               |
| `POSTGRES_DB_URI`       | PostgreSQL connection URL             | No (default: localhost:5432/demo) |
| `POSTGRES_DB_USERNAME`  | Database username                     | No (default: postgres)            |
| `POSTGRES_DB_PASSWORD`  | Database password                     | No (default: postgres)            |

## 🧪 Testing RAG Capabilities

### Test Vector Store RAG

1. Ingest sample coffee data
2. Navigate to `/chat/VECTOR_STORE`
3. Ask questions about coffee: "Tell me about Ethiopian coffee"

### Test Web Search RAG

1. Navigate to `/chat/WEB_SEARCH`
2. Ask current events questions: "What's the latest news about AI?"

### Test Tools Mode

1. Navigate to `/chat/TOOLS`
2. Ask mixed questions that might need both sources:
    - "Compare my Ethiopian coffee with current coffee market trends"
    - "What coffee regions are trending now, and do I have any from those areas?"

## 🔍 Development

### Project Structure

```
src/main/kotlin/eu/zeletrik/spring/ai/
├── chat/
│   ├── ConversationFacade.kt          # Main orchestration component
│   └── service/
│       ├── ChatService.kt             # Basic chat service
│       ├── VectorStoreRagService.kt   # Vector store RAG implementation
│       ├── WebSearchRagService.kt     # Web search RAG implementation
│       └── ToolsService.kt            # Tool-based RAG implementation
├── data/
│   ├── CoffeeDetails.kt               # Sample data model
│   └── RagTypes.kt                    # RAG mode enumeration
├── tools/
│   ├── VectorStoreTool.kt             # Vector store function tool
│   └── WebSearchTool.kt               # Web search function tool
├── transformer/
│   └── MemoryAwareQueryRewriter.kt    # Query enhancement
├── ui/
│   ├── ChatUiController.kt            # Web interface controller
│   └── HTML.kt                        # HTML rendering utilities
└── web/
    └── SearchEngineDocumentRetriever.kt # Web search integration
```
