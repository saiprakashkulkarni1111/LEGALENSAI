"""Universal legal search (acts, sections, notifications)."""
from typing import Optional
from fastapi import APIRouter, Query
from backend.app.connectors.registry import source_registry

router = APIRouter(prefix="/api/legal", tags=["Legal Search"])


@router.get("/search")
async def legal_search(
    query: str = Query(...),
    jurisdiction: str = Query("India"),
    year: Optional[int] = Query(None),
    document_type: Optional[str] = Query(None),
):
    results = await source_registry.search_all(
        query=query,
        source_ids=["india_code", "supreme_court", "high_courts"],
        filters={"year": year} if year else None,
    )
    if document_type:
        results = [
            r
            for r in results
            if document_type.lower() in str(r.get("source_type", "")).lower()
            or document_type.lower() in str(r.get("title", "")).lower()
        ]
    return {
        "query": query,
        "jurisdiction": jurisdiction,
        "total_results": len(results),
        "results": results,
        "mode": "REAL",
        "notice": "Results are retrieved from indexed official sources or official-search fallbacks. Unverified claims are not invented.",
    }
