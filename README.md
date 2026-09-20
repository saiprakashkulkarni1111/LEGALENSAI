# LEGALENS AI

Evidence-first legal intelligence for India.

**Tagline:** Understand the document. Discover the law. Know what to verify next.

Legalens AI helps people understand legal documents, find official Indian legal sources, and prepare questions for a lawyer. It does **not** replace a lawyer and does **not** invent statutes, cases, or citations.

**Rule:** No evidence → no claim.

## What was already in this repo

The previous session left a working FastAPI core:

- Document upload/parse (PDF, DOCX, TXT)
- Clause and timeline extraction
- Hybrid retrieval over India Code / Supreme Court connectors
- Citation verification and hallucination tests
- eCourts official-search fallback (no CAPTCHA bypass)
- Comparison, lawyer prep, knowledge graph, health APIs

That was **not** enough for a complete product: there was no frontend, no packaging, and several APIs were missing.

This continuation keeps that backend and adds the workspace UI, remaining APIs, Docker, and evaluation docs.

## Quick start (local, zero-dependency database)

```bash
python -m venv .venv
.venv\Scripts\activate
pip install -r requirements.txt
copy .env.example .env
uvicorn backend.app.main:app --reload --port 8000
```

In another terminal:

```bash
cd frontend
npm install
npm run dev
```

Open:

- UI: http://localhost:3000
- API docs: http://localhost:8000/docs
- Health: http://localhost:8000/health

Demo login (optional JWT): `analyst@legalens.ai` / `analyst-demo`

Upload `data/demo/Software_Employment_Agreement.txt` (labelled `[SYNTHETIC DEMO]`).

## Demo vs real data

| Mode | Meaning |
| --- | --- |
| `SYNTHETIC_DEMO` | Synthetic contracts only. Always labelled in the UI. |
| `REAL` | User-uploaded documents plus official source connectors. |

Statutory snippets in adapters are taken from public India Code / reported judgment text and linked to official URLs. If a source requires CAPTCHA, the API returns `official_search_required` instead of scraping.

## Tests

```bash
pytest
```

Frontend:

```bash
cd frontend
npm run lint
npm run typecheck
npm run build
```

## Architecture

See `docs/ARCHITECTURE.md`.

Production target: PostgreSQL + pgvector. Local default: SQLite + NumPy cosine similarity, same interfaces.
