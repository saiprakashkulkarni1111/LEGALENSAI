"""
LEGALENS AI - Case Explorer API Router
Searches landmark Supreme Court of India judgments and generates compliant eCourts search links.
"""
from typing import List, Optional
from fastapi import APIRouter, Query
from backend.app.connectors.registry import source_registry
from backend.app.schemas.research_schemas import CaseItemResponse

router = APIRouter(prefix="/api/cases", tags=["Cases"])


@router.get("/search", response_model=List[CaseItemResponse])
async def search_cases(
    query: str = Query(..., description="Party name, case title, section, or legal concept"),
    court: Optional[str] = Query(None),
    year: Optional[int] = Query(None)
):
    """
    Search case law from Supreme Court of India and eCourts registry.
    """
    filters = {}
    if court:
        filters["court"] = court
    if year:
        filters["year"] = year

    results = await source_registry.search_all(
        query=query,
        source_ids=["supreme_court", "ecourts"],
        filters=filters
    )

    response = []
    for r in results:
        response.append(CaseItemResponse(
            id=r.get("id", "case_res"),
            case_title=r.get("case_title") or r.get("title") or "Judicial Record",
            court=r.get("court", "Supreme Court of India"),
            case_number=r.get("case_number"),
            cnr_number=r.get("cnr_number"),
            decision_date=r.get("decision_date"),
            year=r.get("year"),
            parties=r.get("parties"),
            bench=r.get("bench"),
            acts_referred=r.get("acts_referred", []),
            relevant_excerpt=r.get("relevant_excerpt") or r.get("content", ""),
            official_url=r.get("official_url", "https://main.sci.gov.in"),
            retrieved_at=r.get("retrieved_at", ""),
            status=r.get("status", "Disposed"),
            requires_official_search=not r.get("automation_available", True),
            official_search_guidance=r.get("guidance") or r.get("reason")
        ))

    return response
