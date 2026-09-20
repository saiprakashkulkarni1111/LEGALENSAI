"""
LEGALENS AI - Legal Research API Router
Implements the live research and universal search endpoints with the mandatory "NO EVIDENCE -> NO CLAIM" policy.
"""
from typing import List, Optional
from fastapi import APIRouter, Depends, Query
from sqlalchemy.ext.asyncio import AsyncSession

from backend.app.models.database import get_db
from backend.app.schemas.research_schemas import (
    LiveResearchRequest,
    LiveResearchResponse,
    EvidenceItem,
    LegalSourceReference,
    ClaimValidationItem
)
from backend.app.agents.orchestrator import orchestrator
from backend.app.connectors.registry import source_registry

router = APIRouter(prefix="/api/research", tags=["Research"])


@router.post("/live", response_model=LiveResearchResponse)
@router.post("/query", response_model=LiveResearchResponse)
async def conduct_live_research(
    payload: LiveResearchRequest,
    db: AsyncSession = Depends(get_db)
):
    """
    Real-time / verified legal research endpoint.
    Retrieves evidence from authoritative Indian sources (India Code, Supreme Court, etc.),
    executes citation validation, and returns evidence-backed findings.
    """
    result = await orchestrator.research_agent.process({
        "query": payload.query,
        "jurisdiction": payload.jurisdiction,
        "date_context": payload.date_context,
        "document_context": None,
        "sources": payload.sources
    })

    return LiveResearchResponse(
        query=result["query"],
        answer=result["answer"],
        what_document_says=result.get("what_document_says"),
        evidence=[EvidenceItem(**e) for e in result["evidence"]],
        claims=[ClaimValidationItem(**c) for c in result["claims"]],
        legal_sources=[LegalSourceReference(**s) for s in result["legal_sources"]],
        what_this_means=result["what_this_means"],
        what_to_verify=result["what_to_verify"],
        possible_next_steps=result["possible_next_steps"],
        questions_for_lawyer=result["questions_for_lawyer"],
        retrieved_at=result["retrieved_at"],
        freshness_status=result["freshness_status"],
        limitations=result["limitations"]
    )


@router.get("/search")
async def quick_legal_search(
    query: str = Query(..., description="Legal term, Act title, or section"),
    jurisdiction: str = Query("India"),
    year: Optional[int] = Query(None)
):
    """Universal search across authoritative statutory repositories."""
    results = await source_registry.search_all(
        query=query,
        source_ids=["india_code", "supreme_court", "high_courts"],
        filters={"year": year} if year else None
    )
    return {"query": query, "total_results": len(results), "results": results}
