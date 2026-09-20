# API

See OpenAPI at `/docs`.

- POST `/api/auth/login`
- POST `/api/documents/upload`
- POST `/api/documents/{id}/analyze`
- POST `/api/documents/{id}/ask`
- POST `/api/documents/{id}/impact`
- GET `/api/documents`, `/api/documents/{id}`, clauses, timeline, pages
- POST `/api/documents/compare`
- POST `/api/research/query` and `/api/research/live`
- GET `/api/legal/search`
- GET `/api/cases/search`
- GET `/api/sources`, `/health`, `/health/sources`
- GET `/api/sources/health/stream` (SSE)
- POST `/api/checklists/generate`
- POST `/api/lawyer-prep/generate`
- GET `/api/graph`
- DELETE `/api/documents/{id}`
