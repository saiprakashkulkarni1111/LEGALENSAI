"""
LEGALENS AI - Documents API Router
Handles file upload, parsing, clause intelligence, timeline extraction, and document deletion.
"""
import os
import hashlib
from typing import List, Optional
from fastapi import APIRouter, UploadFile, File, Form, HTTPException, Depends, status
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select

from backend.app.config import settings
from backend.app.models.database import get_db
from backend.app.models.models import UserDocument, DocumentPage, Clause, TimelineEvent
from backend.app.document_processing.parser import DocumentParser
from backend.app.agents.orchestrator import orchestrator
from backend.app.schemas.document_schemas import (
    DocumentUploadResponse,
    DocumentDetail,
    DocumentPageItem,
    ClauseItem,
    TimelineEventItem
)
from backend.app.security.auth import get_current_user
from backend.app.services.impact_service import impact_engine

router = APIRouter(prefix="/api/documents", tags=["Documents"])


def _clause_item(c: Clause) -> ClauseItem:
    return ClauseItem(
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
    )


def _timeline_item(t: TimelineEvent) -> TimelineEventItem:
    return TimelineEventItem(
        id=t.id,
        date_str=t.date_str,
        event_description=t.event_description,
        event_type=t.event_type,
        page_number=t.page_number,
        confidence=t.confidence,
        is_manual_override=t.is_manual_override
    )


async def _document_detail(db: AsyncSession, doc: UserDocument) -> DocumentDetail:
    clauses_res = await db.execute(select(Clause).where(Clause.document_id == doc.id))
    clauses = clauses_res.scalars().all()
    timeline_res = await db.execute(select(TimelineEvent).where(TimelineEvent.document_id == doc.id))
    timeline = timeline_res.scalars().all()
    pages_res = await db.execute(select(DocumentPage).where(DocumentPage.document_id == doc.id).order_by(DocumentPage.page_number))
    pages = pages_res.scalars().all()
    clause_items = [_clause_item(c) for c in clauses]
    return DocumentDetail(
        id=doc.id,
        title=doc.title,
        file_name=doc.file_name,
        file_type=doc.file_type,
        file_size=doc.file_size,
        mode=doc.mode,
        status=doc.status,
        doc_type=doc.doc_type,
        jurisdiction_detected=doc.jurisdiction_detected,
        parties_detected=doc.parties_detected or [],
        effective_date_detected=doc.effective_date_detected,
        pii_detected_count=doc.pii_detected_count,
        pii_redacted=doc.pii_redacted,
        summary=doc.summary,
        created_at=doc.created_at,
        clauses=clause_items,
        timeline_events=[_timeline_item(t) for t in timeline],
        pages=[
            DocumentPageItem(
                page_number=p.page_number,
                text=p.redacted_text or p.raw_text,
                char_count=p.char_count,
            )
            for p in pages
        ],
        total_clauses=len(clause_items),
        total_obligations=sum(1 for c in clause_items if c.obligation),
        total_deadlines=len(timeline),
        items_requiring_review=sum(
            1 for c in clause_items
            if c.attention_category in ["Review", "Important review", "Professional review recommended"]
        )
    )


@router.post("/upload", response_model=DocumentUploadResponse)
async def upload_document(
    file: UploadFile = File(...),
    mode: str = Form(default="REAL"),  # REAL or SYNTHETIC_DEMO
    auto_redact: bool = Form(default=True),
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user)
):
    """
    Upload and register a legal document (PDF, DOCX, TXT).
    Validates file extension, performs security scan, runs PII detection, and indexes structure.
    """
    ext = file.filename.split(".")[-1].lower() if "." in file.filename else ""
    if ext not in settings.ALLOWED_EXTENSIONS:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Unsupported file format '.{ext}'. Supported formats: {', '.join(settings.ALLOWED_EXTENSIONS)}"
        )

    # Read content
    contents = await file.read()
    file_size = len(contents)
    if file_size > settings.MAX_UPLOAD_SIZE_MB * 1024 * 1024:
        raise HTTPException(
            status_code=status.HTTP_413_REQUEST_ENTITY_TOO_LARGE,
            detail=f"File exceeds maximum permissible size of {settings.MAX_UPLOAD_SIZE_MB}MB."
        )

    content_hash = hashlib.sha256(contents).hexdigest()

    # Save to disk
    safe_filename = f"{content_hash[:12]}_{file.filename}"
    save_path = os.path.join(settings.STORAGE_DIR, safe_filename)
    with open(save_path, "wb") as f:
        f.write(contents)

    # Parse pages
    try:
        pages = DocumentParser.parse_file(save_path)
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
            detail=f"Error parsing document structure: {str(e)}"
        )

    # Run Multi-Agent Analysis Pipeline
    analysis = await orchestrator.analyze_document_pipeline(pages, auto_redact=auto_redact)

    # Save to database
    user_doc = UserDocument(
        title=file.filename.rsplit(".", 1)[0].replace("_", " ").title(),
        file_name=file.filename,
        file_type=ext,
        file_size=file_size,
        file_path=save_path,
        content_hash=content_hash,
        mode=mode,
        status="ANALYZED",
        doc_type="Contract / Agreement",
        pii_detected_count=analysis["pii_detected_count"],
        pii_redacted=analysis["pii_redacted"],
        pii_types_found=analysis["pii_types_found"],
        summary=analysis["summary"]
    )
    db.add(user_doc)
    await db.flush()

    # Save pages
    for p in analysis["processed_pages"]:
        page_rec = DocumentPage(
            document_id=user_doc.id,
            page_number=p["page_number"],
            raw_text=p["raw_text"],
            redacted_text=p["text"],
            char_count=len(p["text"])
        )
        db.add(page_rec)

    # Save clauses
    for c in analysis["clauses"]:
        clause_rec = Clause(
            document_id=user_doc.id,
            clause_type=c["clause_type"],
            original_text=c["original_text"],
            page_number=c["page_number"],
            section_reference=c.get("section_reference"),
            plain_explanation=c["plain_explanation"],
            obligation=c.get("obligation"),
            attention_category=c["attention_category"],
            potential_concern=c.get("potential_concern"),
            relevant_legal_concept=c.get("relevant_legal_concept"),
            suggested_verification=c.get("suggested_verification"),
            confidence=c.get("confidence", 0.95)
        )
        db.add(clause_rec)

    # Save timeline events
    for ev in analysis["timeline_events"]:
        ev_rec = TimelineEvent(
            document_id=user_doc.id,
            date_str=ev["date_str"],
            event_description=ev["event_description"],
            event_type=ev["event_type"],
            page_number=ev["page_number"],
            confidence=ev.get("confidence", 0.95),
            is_manual_override=False
        )
        db.add(ev_rec)

    await db.commit()
    await db.refresh(user_doc)

    return DocumentUploadResponse(
        id=user_doc.id,
        title=user_doc.title,
        file_name=user_doc.file_name,
        status=user_doc.status,
        mode=user_doc.mode,
        pii_detected_count=user_doc.pii_detected_count,
        pii_redacted=user_doc.pii_redacted,
        message="Document uploaded, sanitized, and analyzed successfully."
    )


@router.get("", response_model=List[DocumentDetail])
async def list_documents(db: AsyncSession = Depends(get_db)):
    """List all analyzed documents with summary metrics."""
    result = await db.execute(select(UserDocument).order_by(UserDocument.created_at.desc()))
    docs = result.scalars().all()
    return [await _document_detail(db, doc) for doc in docs]


@router.get("/{document_id}", response_model=DocumentDetail)
async def get_document(document_id: str, db: AsyncSession = Depends(get_db)):
    """Retrieve full analysis, clauses, and timeline for a document."""
    result = await db.execute(select(UserDocument).where(UserDocument.id == document_id))
    doc = result.scalar_one_or_none()
    if not doc:
        raise HTTPException(status_code=404, detail="Document not found.")
    return await _document_detail(db, doc)


@router.post("/{document_id}/analyze", response_model=DocumentDetail)
async def analyze_document(document_id: str, db: AsyncSession = Depends(get_db)):
    """Re-run the analysis pipeline on an already uploaded file."""
    result = await db.execute(select(UserDocument).where(UserDocument.id == document_id))
    doc = result.scalar_one_or_none()
    if not doc:
        raise HTTPException(status_code=404, detail="Document not found.")
    pages = DocumentParser.parse_file(doc.file_path)
    analysis = await orchestrator.analyze_document_pipeline(pages, auto_redact=True)

    existing_clauses = await db.execute(select(Clause).where(Clause.document_id == doc.id))
    for row in existing_clauses.scalars().all():
        await db.delete(row)
    existing_events = await db.execute(select(TimelineEvent).where(TimelineEvent.document_id == doc.id))
    for row in existing_events.scalars().all():
        await db.delete(row)

    for c in analysis["clauses"]:
        db.add(Clause(
            document_id=doc.id,
            clause_type=c["clause_type"],
            original_text=c["original_text"],
            page_number=c["page_number"],
            section_reference=c.get("section_reference"),
            plain_explanation=c["plain_explanation"],
            obligation=c.get("obligation"),
            attention_category=c["attention_category"],
            potential_concern=c.get("potential_concern"),
            relevant_legal_concept=c.get("relevant_legal_concept"),
            suggested_verification=c.get("suggested_verification"),
            confidence=c.get("confidence", 0.95)
        ))
    for ev in analysis["timeline_events"]:
        db.add(TimelineEvent(
            document_id=doc.id,
            date_str=ev["date_str"],
            event_description=ev["event_description"],
            event_type=ev["event_type"],
            page_number=ev["page_number"],
            confidence=ev.get("confidence", 0.95),
            is_manual_override=False
        ))
    doc.summary = analysis["summary"]
    doc.status = "ANALYZED"
    await db.commit()
    await db.refresh(doc)
    return await _document_detail(db, doc)


@router.get("/{document_id}/pages", response_model=List[DocumentPageItem])
async def get_document_pages(document_id: str, db: AsyncSession = Depends(get_db)):
    pages_res = await db.execute(
        select(DocumentPage).where(DocumentPage.document_id == document_id).order_by(DocumentPage.page_number)
    )
    pages = pages_res.scalars().all()
    return [
        DocumentPageItem(page_number=p.page_number, text=p.redacted_text or p.raw_text, char_count=p.char_count)
        for p in pages
    ]


@router.post("/{document_id}/ask")
async def ask_document_question(document_id: str, payload: dict, db: AsyncSession = Depends(get_db)):
    """Evidence-first Q&A grounded in the uploaded document plus official sources."""
    result = await db.execute(select(UserDocument).where(UserDocument.id == document_id))
    doc = result.scalar_one_or_none()
    if not doc:
        raise HTTPException(status_code=404, detail="Document not found.")
    query = (payload or {}).get("query") or ""
    clause_id = (payload or {}).get("clause_id")
    clauses_res = await db.execute(select(Clause).where(Clause.document_id == document_id))
    clauses = clauses_res.scalars().all()
    selected = next((c for c in clauses if c.id == clause_id), None)
    doc_context = selected.original_text if selected else " ".join(c.original_text[:400] for c in clauses[:4])
    research = await orchestrator.research_query(
        query=query,
        jurisdiction=doc.jurisdiction_detected or "India",
        date_context="CURRENT",
        doc_context=doc_context,
    )
    research["what_document_says"] = selected.original_text if selected else doc.summary
    research["document_mode"] = doc.mode
    return research


@router.post("/{document_id}/impact")
async def document_to_law_impact(document_id: str, db: AsyncSession = Depends(get_db)):
    result = await db.execute(select(UserDocument).where(UserDocument.id == document_id))
    doc = result.scalar_one_or_none()
    if not doc:
        raise HTTPException(status_code=404, detail="Document not found.")
    clauses_res = await db.execute(select(Clause).where(Clause.document_id == document_id))
    clauses = clauses_res.scalars().all()
    impacts = await impact_engine.analyze([
        {"id": c.id, "clause_type": c.clause_type, "relevant_legal_concept": c.relevant_legal_concept}
        for c in clauses
    ])
    return {
        "document_id": document_id,
        "mode": doc.mode,
        "impacts": impacts,
        "disclaimer": "Potential issue requiring review. This is not a finding of illegality.",
    }


@router.get("/{document_id}/clauses", response_model=List[ClauseItem])
async def get_document_clauses(document_id: str, db: AsyncSession = Depends(get_db)):
    """Fetch structured clauses for interactive document viewer highlighting."""
    clauses_res = await db.execute(select(Clause).where(Clause.document_id == document_id))
    clauses = clauses_res.scalars().all()
    return [_clause_item(c) for c in clauses]


@router.get("/{document_id}/timeline", response_model=List[TimelineEventItem])
async def get_document_timeline(document_id: str, db: AsyncSession = Depends(get_db)):
    """Fetch structured chronological milestones."""
    timeline_res = await db.execute(select(TimelineEvent).where(TimelineEvent.document_id == document_id))
    timeline = timeline_res.scalars().all()
    return [_timeline_item(t) for t in timeline]


@router.delete("/{document_id}")
async def delete_document(document_id: str, db: AsyncSession = Depends(get_db)):
    """Secure document deletion, removing local files and database entries."""
    result = await db.execute(select(UserDocument).where(UserDocument.id == document_id))
    doc = result.scalar_one_or_none()
    if not doc:
        raise HTTPException(status_code=404, detail="Document not found.")

    if os.path.exists(doc.file_path):
        try:
            os.remove(doc.file_path)
        except Exception:
            pass

    await db.delete(doc)
    await db.commit()
    return {"status": "success", "message": f"Document '{doc.title}' and associated records permanently deleted."}
