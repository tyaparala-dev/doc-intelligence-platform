# AI-Powered Document Intelligence Platform

A high-performance, multi-tenant enterprise backend architecture that delivers real-time intelligent document Q&A. Built using Spring Boot microservices, this platform combines traditional transactional data handling with an advanced Retrieval-Augmented Generation (RAG) pipeline running completely on localized open-source LLM infrastructure.

---

## 🚀 Key Technical Highlights
* **Zero-Trust Multi-Tenancy**: Strict data isolation enforced at the database level using unified tenant context anchors (`tenant_id`) embedded directly within JWT payloads.
* **Hardened Stateless Security**: Robust authentication layer utilizing short-lived JWT Access Tokens coupled with a database-backed **Refresh Token Rotation (RTR)** engine to eliminate replay attacks.
* **Hybrid Storage Architecture**: Dual-engine data tier leveraging PostgreSQL (`pgvector`) for high-dimensional semantic vector search alongside MongoDB for flexible, schema-less document metadata management.
* **Localized AI Infrastructure**: Native integration with Spring AI and containerized Ollama instances, allowing local execution of embedding models and conversational LLMs with zero cloud dependencies.

---

## 🏗️ System Architecture & Data Flow

```text
[ Client ] 
   │
   ├── (POST /register) ───► [ Auth Service ] ───► Generates unique tenant_id & hashes pwd
   ├── (POST /login)    ───► [ Auth Service ] ───► Issues short-lived Access + Rotated Refresh JWTs
   │
   └── (Bearer JWT) ───► [ Security Context Filter ] ───► Validates claims & extracts tenant_id
                               │
       ┌───────────────────────┴───────────────────────┐
       ▼                                               ▼
[ Ingestion Pipeline ]                         [ RAG Query Engine ]
 1. Multipart Document Upload                   1. User enters natural language prompt
 2. Parse text & metadata                       2. Vectorize query via nomic-embed-text
 3. Map tracking values inside MongoDB          3. Search pgvector scoped to tenant_id
 4. Chunk text via TokenTextSplitter            4. Fetch closest 3-5 contexts
 5. Compute vector embeddings                   5. Augment structured system prompts
 6. Persist matrix arrays to pgVector           6. Stream output via LLM (Llama 3)
```

---

## 🛠️ Technology Stack
* **Framework**: Spring Boot 3.x (Spring Security, Spring Data JPA, Spring Data MongoDB, Spring AI)
* **Databases**: PostgreSQL 16 (`pgvector/pgvector`), MongoDB 7.0
* **AI & Embeddings Engine**: Ollama (`llama3`, `nomic-embed-text`)
* **Security & Tokens**: JSON Web Tokens (JWT via `jjwt`), BCrypt Cryptographic Hashing
* **Containerization**: Docker, Docker Compose
* **Build Tool & Language**: Maven, Java 17+

---

## 🔧 Infrastructure Quickstart (Phase 1)

### Prerequisites
* Docker and Docker Compose installed locally.
* Java 17 or higher.

### 1. Boot up the Container Cluster
Spin up PostgreSQL with pgvector, MongoDB, and Ollama in a decoupled virtual network:
```bash
docker compose up -d
```

### 2. Provision Local AI Models
Pull the foundational embedding and conversational models directly into your running Ollama engine:
```bash
# Pull text embedding matrix generator
docker exec -it doc_platform_ollama ollama pull nomic-embed-text

# Pull conversational generation engine
docker exec -it doc_platform_ollama ollama pull llama3
```

### 3. Initialize SQL Schemas
Inject the authorization schema boundaries and activate the vector math extensions inside your Postgres container instance:
```bash
docker exec -it doc_platform_postgres psql -U platform_user -d doc_intelligence -c "CREATE SCHEMA IF NOT EXISTS auth_schema; CREATE EXTENSION IF NOT EXISTS vector;"
```

---

## 🛡️ API Specification: Authentication Core (Phase 2)

All authentication endpoints map under `/api/v1/auth/**` and accept global JSON payloads.


| Method | Endpoint | Access | Payload | Description |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/register` | Public | `AuthRequest` | Creates user, auto-assigns global `tenantId`, hashes password via BCrypt. |
| `POST` | `/login` | Public | `AuthRequest` | Authenticates user; returns an Access token (15m) and a tracking Refresh token. |
| `POST` | `/refresh` | Public | `RefreshRequest` | Destroys the old token, executes **Refresh Token Rotation**, and issues a new pair. |

---

## 📈 Ongoing Implementation Roadmap
- [x] **Phase 1**: Orchestrated Docker containers, multi-schema setup, and offline LLM provisioning.
- [x] **Phase 2**: Hardened JWT filter pipeline, tenant context injection, and stateful Refresh Token Rotation.
- [ ] **Phase 3**: Document Processing microservice, MongoDB file metadata mapping, and text-chunking pipelines.
- [ ] **Phase 4**: Vector DB ingestion (`pgvector`), embedding synthesis via Spring AI, and RAG execution threads.
