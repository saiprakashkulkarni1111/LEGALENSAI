# Architecture

LEGALENS AI is a monorepo:

- `frontend/` Next.js App Router workspace
- `backend/` FastAPI services, agents, connectors, RAG
- `workers/` background source-sync jobs
- `data/demo/` synthetic documents only
- `tests/` unit, integration, security, RAG, accessibility, e2e

## Request path

```
User → Next.js workspace → FastAPI
  → DocumentParser / Agents / LegalSourceRegistry
  → Hybrid retriever + citation verifier
  → Evidence-first JSON
```

## Layers

API → Service → Agent → Repository/ORM → SQLite or PostgreSQL

Business logic is not duplicated per database. Vector search uses `LegalVectorIndex` (NumPy cosine locally).

## Evidence doctrine

Connectors may return official URLs or `official_search_required`. Agents must not invent citations. Empty retrieval yields: “Unable to verify this claim from the available authoritative sources.”
