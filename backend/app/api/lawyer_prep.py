"""
LEGALENS AI - Lawyer Preparation & Checklist API Router
Generates exportable briefing dossiers and interactive verification checklists.
"""
from typing import Dict, Any, List
from fastapi import APIRouter, Depends, HTTPException, Body
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select

from backend.app.models.database import get_db
from backend.app.models.models import UserDocument, Clause, TimelineEvent
from backend.app.schemas.document_schemas import LawyerPrepRequest, LawyerPrepResponse, ClauseItem, TimelineEventItem
from backend.app.agents.orchestrator import orchestrator

router = APIRouter(prefix="/api", tags=["Lawyer Prep & Checklists"])


@router.post("/lawyer-prep/generate", response_model=LawyerPrepResponse)
async def generate_lawyer_prep_pack(
    payload: LawyerPrepRequest,
    db: AsyncSession = Depends(get_db)
):
    """
    Generate a comprehensive, structured briefing pack for professional legal consultation.
    """
    result = await db.execute(select(UserDocument).where(UserDocument.id == payload.document_id))
    doc = result.scalar_one_or_none()
    if not doc:
        raise HTTPException(status_code=404, detail="Document not found.")

    clauses_res = await db.execute(select(Clause).where(Clause.document_id == doc.id))
    clauses = clauses_res.scalars().all()
    timeline_res = await db.execute(select(TimelineEvent).where(TimelineEvent.document_id == doc.id))
    timeline = timeline_res.scalars().all()

    doc_data = {
        "title": doc.title,
        "file_name": doc.file_name,
        "doc_type": doc.doc_type,
        "parties_detected": doc.parties_detected or [],
        "effective_date_detected": doc.effective_date_detected,
        "jurisdiction_detected": doc.jurisdiction_detected,
        "clauses": [
            ClauseItem(
                id=c.id,
                clause_type=c.clause_type,
                original_text=c.original_text,
                page_number=c.page_number,
                section_reference=c.section_reference,
                plain_explanation=c.plain_explanation,
                obligation=c.obligation,
                attention_category=c.attention_category,
                potential_concern=c.potential_concern,
                relevant_legal_concept=c.relevant_legal_concept,
                suggested_verification=c.suggested_verification,
                confidence=c.confidence
            ).model_dump() for c in clauses
        ],
        "timeline_events": [
            TimelineEventItem(
                id=t.id,
                date_str=t.date_str,
                event_description=t.event_description,
                event_type=t.event_type,
                page_number=t.page_number,
                confidence=t.confidence,
                is_manual_override=t.is_manual_override
            ).model_dump() for t in timeline
        ]
    }

    prep_pack = await orchestrator.generate_lawyer_prep(doc_data, user_notes=payload.user_notes)

    return LawyerPrepResponse(
        document_title=prep_pack["document_title"],
        prepared_at=prep_pack["prepared_at"],
        executive_summary=prep_pack["executive_summary"],
        key_facts=prep_pack["key_facts"],
        timeline=[TimelineEventItem(**t) for t in prep_pack["timeline"]],
        documents_provided=prep_pack["documents_provided"],
        missing_information=prep_pack["missing_information"],
        key_clauses=[ClauseItem(**c) for c in prep_pack["key_clauses"]],
        relevant_statutory_provisions=prep_pack["relevant_statutory_provisions"],
        related_cases=prep_pack["related_cases"],
        questions_for_counsel=prep_pack["questions_for_counsel"],
        matters_for_professional_review=prep_pack["matters_for_professional_review"],
        legal_disclaimer=prep_pack["legal_disclaimer"]
    )


@router.post("/checklists/generate")
async def generate_action_checklist(
    document_id: str = Body(..., embed=True),
    db: AsyncSession = Depends(get_db)
):
    """
    Generate an actionable pre-signing / verification checklist for the user.
    """
    result = await db.execute(select(UserDocument).where(UserDocument.id == document_id))
    doc = result.scalar_one_or_none()
    if not doc:
        raise HTTPException(status_code=404, detail="Document not found.")

    clauses_res = await db.execute(select(Clause).where(Clause.document_id == doc.id))
    clauses = clauses_res.scalars().all()

    checklist_items = [
        {"task": "Verify Legal Identity of Signatories", "category": "Execution", "completed": False, "note": "Check MCA portal for CIN / Director DIN if corporate."},
        {"task": "Review Payment Milestones & Invoicing Window", "category": "Commercial", "completed": False, "note": "Ensure bank details match official invoices."},
        {"task": "Evaluate Termination Notice Timelines", "category": "Risk", "completed": False, "note": "Confirm whether notice period provides reasonable transition time."},
        {"task": "Audit Intellectual Property Assignment Carve-outs", "category": "IP", "completed": False, "note": "Exclude pre-existing code and third-party OSS tools."},
        {"task": "Cross-check Dispute Resolution Venue & Seat", "category": "Legal", "completed": False, "note": "Ensure territorial jurisdiction is mutually acceptable."}
    ]

    return {
        "document_id": document_id,
        "document_title": doc.title,
        "total_items": len(checklist_items),
        "items": checklist_items
    }
