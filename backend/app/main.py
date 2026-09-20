"""
LEGALENS AI - Core FastAPI Application Server
Real-Data Legal Intelligence Platform for India.
Doctrine: "NO EVIDENCE -> NO CLAIM"
"""
import logging
from contextlib import asynccontextmanager
from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse

from backend.app.config import settings
from backend.app.models.database import init_db
from backend.app.api import (
    documents_router,
    research_router,
    cases_router,
    sources_router,
    compare_router,
    lawyer_prep_router,
    graph_router,
    health_router,
    auth_router,
    legal_router,
)
from backend.app.security.rate_limiter import rate_limiter

# Setup logging
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(name)s: %(message)s"
)
logger = logging.getLogger("legalens.main")


@asynccontextmanager
async def lifespan(app: FastAPI):
    logger.info("Initializing LEGALENS AI backend services...")
    await init_db()
    logger.info("Database initialized. Ready to serve legal intelligence requests.")
    yield
    logger.info("Shutting down LEGALENS AI backend.")


app = FastAPI(
    title="LEGALENS AI",
    description=(
        "Evidence-First AI for Legal Assistance & Access in India. "
        "Strict 'NO EVIDENCE -> NO CLAIM' architectural policy."
    ),
    version="1.0.0",
    lifespan=lifespan
)

# CORS Configuration for Next.js frontend
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:3000", "http://127.0.0.1:3000"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


@app.middleware("http")
async def rate_limiting_middleware(request: Request, call_next):
    # Apply sliding window rate limiter
    await rate_limiter.check_rate_limit(request)
    response = await call_next(request)
    # Secure HTTP response headers
    response.headers["X-Content-Type-Options"] = "nosniff"
    response.headers["X-Frame-Options"] = "DENY"
    response.headers["X-XSS-Protection"] = "1; mode=block"
    response.headers["Referrer-Policy"] = "strict-origin-when-cross-origin"
    return response


# Include API Routers
app.include_router(auth_router)
app.include_router(legal_router)
app.include_router(documents_router)
app.include_router(research_router)
app.include_router(cases_router)
app.include_router(sources_router)
app.include_router(compare_router)
app.include_router(lawyer_prep_router)
app.include_router(graph_router)
app.include_router(health_router)


@app.get("/")
async def root():
    return {
        "platform": "LEGALENS AI",
        "tagline": "Understand the document. Discover the law. Know what to verify next.",
        "jurisdiction": "India",
        "core_doctrine": "NO EVIDENCE -> NO CLAIM",
        "status": "OPERATIONAL",
        "docs_url": "/docs"
    }


if __name__ == "__main__":
    import uvicorn
    uvicorn.run("backend.app.main:app", host="0.0.0.0", port=settings.API_PORT, reload=settings.DEBUG)
