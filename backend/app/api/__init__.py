from backend.app.api.documents import router as documents_router
from backend.app.api.research import router as research_router
from backend.app.api.cases import router as cases_router
from backend.app.api.sources import router as sources_router
from backend.app.api.compare import router as compare_router
from backend.app.api.lawyer_prep import router as lawyer_prep_router
from backend.app.api.graph import router as graph_router
from backend.app.api.health import router as health_router
from backend.app.api.auth import router as auth_router
from backend.app.api.legal import router as legal_router

__all__ = [
    "documents_router",
    "research_router",
    "cases_router",
    "sources_router",
    "compare_router",
    "lawyer_prep_router",
    "graph_router",
    "health_router",
    "auth_router",
    "legal_router",
]
