# Deployment

Suggested production mapping:

- Frontend: Vercel / Cloud Run
- Backend: Cloud Run (`uvicorn backend.app.main:app`)
- Database: Cloud SQL PostgreSQL + pgvector (`DATABASE_URL=postgresql+asyncpg://...`)
- Redis: managed Redis for rate limit/cache
- Workers: Cloud Run Jobs running `python -m workers.sync_worker`
- Secrets: Secret Manager, never committed

Local compose: `docker compose up --build`.
