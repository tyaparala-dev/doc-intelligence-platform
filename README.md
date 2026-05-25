# AI-Powered Document Intelligence Platform (V1 - Core AI Engine)

A high-performance backend system built with **Spring Boot** and **Java 21**, leveraging **Spring AI** to implement an on-premise **RAG (Retrieval-Augmented Generation)** architecture. This service enables users to upload raw binary files (PDF, DOCX, TXT) and conduct real-time, context-aware semantic question answering over their contents using a locally hosted Large Language Model.

---

## 🚀 Key Features

* **Universal Document Ingestion**: Leverages Apache Tika to parse text dynamically from unstructured file formats via high-performance binary streams.
* **Algorithmic Text Chunking**: Implements the modern, token-aware `TokenTextSplitter` builder pattern to break documents into overlapping text fragments optimized for LLM context windows.
* **On-Premise Local AI Core**: Connects seamlessly with **Ollama** running local `llama3` and embedding models, avoiding cloud dependencies and ensuring complete data privacy.
* **Semantic Similarity Vector Search**: Utilizes an in-memory `SimpleVectorStore` to execute cosine similarity matching on embedded data structures.
* **Context-Injected AI Prompting**: Dynamically structures prompt templates to ensure the LLM generates answers grounded strictly within the context of the uploaded documents.
* **Java 21 Virtual Threads**: Engineered with Spring Boot's native support for virtual threads to handle resource-heavy document tokenization asynchronously without blocking user traffic.

---

## 🛠️ Tech Stack

* **Language & Runtime:** Java 21, Maven
* **Framework:** Spring Boot (Modularized Starter Setup)
* **AI Orchestration:** Spring AI (Ollama Integrations)
* **Document Processing:** Apache Tika Document Reader
* **Local LLM Engine:** Ollama (`llama3` & `all-minilm` embeddings)
* **Utilities:** Lombok

---

## 🚦 Getting Started

### Prerequisites
1. **Java 21 JDK** installed on your system.
2. **Ollama** downloaded and installed from [ollama.com](https://ollama.com).
3. Pull and run the local AI models via your command terminal:
   ```bash
   ollama run llama3
   ollama pull all-minilm
   ```

### Execution Steps
1. Clone this repository to your local directory:
   ```bash
   git clone <your-repository-url>
   cd doc-intelligence-platform
   ```
2. Build the project using Maven:
   ```bash
   mvn clean install
   ```
3. Boot up the Spring Boot server directly from your IDE or the terminal command line:
   ```bash
   mvn spring-boot:run
   ```
The application will spin up successfully on port `8080`.

---

## 🧪 API & Endpoint Specifications

### 1. Upload & Ingest Document
Parses raw files into token chunks, generates vector embeddings, and stores them in the vector engine.
* **Method:** `POST`
* **Endpoint:** `http://localhost:8080/api/docs/upload`
* **Body Type:** `form-data`
* **Payload Parameter:** `file` (Select type as **File** and upload any `.pdf`, `.docx`, or `.txt`)
* **Sample Response:**
  ```text
  Successfully indexed! Document broken down into 14 semantic chunks.
  ```

### 2. Semantic Document Query (RAG)
Searches the database for content matching the query and passes the context to the LLM for an analytical answer.
* **Method:** `GET`
* **Endpoint:** `http://localhost:8080/api/docs/query`
* **Query Parameter:** `question` (e.g., `What is the total revenue listed in this quarterly financial sheet?`)
* **Sample Response:**
  ```text
  Based on the uploaded financial document, the total revenue reported for Q3 is $4.2M, representing a 12% quarter-over-quarter growth.
  ```

---

## 🗺️ Roadmap to V2
To match the complete enterprise microservices ecosystem, upcoming iterations will introduce:
1. **User Authentication Service**: Standalone microservice utilizing PostgreSQL and JWT token-based verification filters.
2. **Persistent Storage Layer**: Swapping the transient in-memory store for a persistent **pgVector (PostgreSQL)** database container.
3. **Event-Driven Messaging**: Integration of **Apache Kafka** brokers to process binary extraction and chunk embeddings completely out-of-band.
