"""
LEGALENS AI - Contract Comparison API Router
Compares Document A vs Document B, returning structured clause diffs and numerical deltas.
"""
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select

from backend.app.models.database import get_db
from backend.app.models.models import UserDocument, Clause
from backend.app.schemas.document_schemas import (
    DocumentCompareRequest,
    DocumentCompareResponse,
    ClauseDiffItem
)
from backend.app.agents.orchestrator import orchestrator

router = APIRouter(prefix="/api/documents", tags=["Comparison"])


@router.post("/compare", response_model=DocumentCompareResponse)
async def compare_documents(
    payload: DocumentCompareRequest,
    db: AsyncSession = Depends(get_db)
):
    """
    Compare two uploaded documents clause by clause.
    """
    res_a = await db.execute(select(UserDocument).where(UserDocument.id == payload.doc_a_id))
    doc_a = res_a.scalar_one_or_none()
    res_b = await db.execute(select(UserDocument).where(UserDocument.id == payload.doc_b_id))
    doc_b = res_b.scalar_one_or_none()

    if not doc_a or not doc_b:
        raise HTTPException(status_code=404, detail="One or both documents not found for comparison.")

    clauses_a_res = await db.execute(select(Clause).where(Clause.document_id == doc_a.id))
    clauses_a = clauses_a_res.scalars().all()
    clauses_b_res = await db.execute(select(Clause).where(Clause.document_id == doc_b.id))
    clauses_b = clauses_b_res.scalars().all()

    data_a = {
        "title": doc_a.title,
        "clauses": [{"clause_type": c.clause_type, "original_text": c.original_text, "page_number": c.page_number} for c in clauses_a]
    }
    data_b = {
        "title": doc_b.title,
        "clauses": [{"clause_type": c.clause_type, "original_text": c.original_text, "page_number": c.page_number} for c in clauses_b]
    }

    diff_result = await orchestrator.compare_documents(data_a, data_b)

    return DocumentCompareResponse(
        doc_a_title=diff_result["doc_a_title"],
        doc_b_title=diff_result["doc_b_title"],
        total_differences=diff_result["total_differences"],
        added_count=diff_result["added_count"],
        removed_count=diff_result["removed_count"],
        modified_count=diff_result["modified_count"],
        diffs=[ClauseDiffItem(**d) for d in diff_result["diffs"]],
        neutral_observations=diff_result["neutral_observations"]
    )
