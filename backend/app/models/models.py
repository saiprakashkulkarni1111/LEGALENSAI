"""
LEGALENS AI - Database Domain Models
Full relational schema supporting PostgreSQL/SQLite, versioning, knowledge graphs, and audit logs.
"""
from datetime import datetime, timezone
import uuid
from sqlalchemy import (
    Column, String, Integer, Float, Text, Boolean, DateTime,
    ForeignKey, JSON, Index
)
from sqlalchemy.orm import relationship
from backend.app.models.database import Base


def generate_uuid() -> str:
    return str(uuid.uuid4())


def get_utc_now():
    return datetime.now(timezone.utc)


class LegalSource(Base):
    __tablename__ = "legal_sources"

    id = Column(String(36), primary_key=True, default=generate_uuid)
    source_id = Column(String(50), unique=True, nullable=False, index=True)  # e.g. "india_code", "supreme_court"
    source_name = Column(String(100), nullable=False)
    authority = Column(String(100), nullable=False)  # e.g. "Government of India", "Supreme Court of India"
    source_type = Column(String(50), nullable=False)  # "legislation", "judgments", "court_status", "statistics"
    official_url = Column(String(500), nullable=False)
    authority_tier = Column(String(20), default="TIER 1")  # TIER 1, TIER 2, TIER 3, TIER 4
    retrieval_method = Column(String(50), default="official_repository")
    freshness_status = Column(String(30), default="RECENT")  # LIVE, RECENT, STALE, ARCHIVED, UNAVAILABLE
    last_checked = Column(DateTime(timezone=True), default=get_utc_now)
    last_successful_sync = Column(DateTime(timezone=True), default=get_utc_now)
    next_sync = Column(DateTime(timezone=True), nullable=True)
    content_hash = Column(String(64), nullable=True)
    version = Column(String(50), default="1.0")
    automation_available = Column(Boolean, default=True)
    notes = Column(Text, nullable=True)


class LegalDocument(Base):
    __tablename__ = "legal_documents"

    id = Column(String(36), primary_key=True, default=generate_uuid)
    source_id = Column(String(50), ForeignKey("legal_sources.source_id"), nullable=False, index=True)
    title = Column(String(300), nullable=False, index=True)
    short_title = Column(String(100), nullable=True)
    act_number = Column(String(50), nullable=True)
    year = Column(Integer, nullable=True, index=True)
    jurisdiction = Column(String(50), default="India")
    official_url = Column(String(500), nullable=False)
    created_at = Column(DateTime(timezone=True), default=get_utc_now)

    versions = relationship("LegalDocumentVersion", back_populates="document", cascade="all, delete-orphan")


class LegalDocumentVersion(Base):
    __tablename__ = "legal_document_versions"

    id = Column(String(36), primary_key=True, default=generate_uuid)
    document_id = Column(String(36), ForeignKey("legal_documents.id"), nullable=False, index=True)
    version = Column(String(50), nullable=False)
    title = Column(String(300), nullable=False)
    content = Column(Text, nullable=False)
    effective_from = Column(String(20), nullable=True, index=True)  # YYYY-MM-DD or year
    effective_until = Column(String(20), nullable=True, index=True)  # YYYY-MM-DD or "CURRENT"
    published_at = Column(String(20), nullable=True)
    retrieved_at = Column(DateTime(timezone=True), default=get_utc_now)
    content_hash = Column(String(64), nullable=False)
    source_url = Column(String(500), nullable=False)
    status = Column(String(30), default="ACTIVE")  # ACTIVE, SUPERSEDED, AMENDED, REPEALED

    document = relationship("LegalDocument", back_populates="versions")
    chunks = relationship("LegalChunk", back_populates="version_rel", cascade="all, delete-orphan")


class LegalChunk(Base):
    __tablename__ = "legal_chunks"

    id = Column(String(36), primary_key=True, default=generate_uuid)
    version_id = Column(String(36), ForeignKey("legal_document_versions.id"), nullable=False, index=True)
    act_title = Column(String(300), nullable=False, index=True)
    section_number = Column(String(50), nullable=True, index=True)
    section_title = Column(String(300), nullable=True)
    content = Column(Text, nullable=False)
    chunk_index = Column(Integer, default=0)
    embedding_json = Column(Text, nullable=True)  # Serialized float array for cross-DB vector search
    metadata_json = Column(JSON, nullable=True)

    version_rel = relationship("LegalDocumentVersion", back_populates="chunks")


class UserDocument(Base):
    __tablename__ = "documents"

    id = Column(String(36), primary_key=True, default=generate_uuid)
    title = Column(String(300), nullable=False)
    file_name = Column(String(255), nullable=False)
    file_type = Column(String(50), nullable=False)  # pdf, docx, txt, etc.
    file_size = Column(Integer, nullable=False)
    file_path = Column(String(500), nullable=False)
    content_hash = Column(String(64), nullable=False)
    mode = Column(String(20), default="REAL")  # REAL vs SYNTHETIC_DEMO
    status = Column(String(30), default="PENDING")  # PENDING, PROCESSING, ANALYZED, FAILED
    doc_type = Column(String(100), default="Contract / Agreement")
    jurisdiction_detected = Column(String(100), default="India")
    parties_detected = Column(JSON, default=list)
    effective_date_detected = Column(String(50), nullable=True)
    pii_detected_count = Column(Integer, default=0)
    pii_redacted = Column(Boolean, default=False)
    pii_types_found = Column(JSON, default=list)
    summary = Column(Text, nullable=True)
    created_at = Column(DateTime(timezone=True), default=get_utc_now)
    updated_at = Column(DateTime(timezone=True), default=get_utc_now, onupdate=get_utc_now)

    pages = relationship("DocumentPage", back_populates="document", cascade="all, delete-orphan")
    clauses = relationship("Clause", back_populates="document", cascade="all, delete-orphan")
    timeline_events = relationship("TimelineEvent", back_populates="document", cascade="all, delete-orphan")


class DocumentPage(Base):
    __tablename__ = "document_pages"

    id = Column(String(36), primary_key=True, default=generate_uuid)
    document_id = Column(String(36), ForeignKey("documents.id"), nullable=False, index=True)
    page_number = Column(Integer, nullable=False)
    raw_text = Column(Text, nullable=False)
    redacted_text = Column(Text, nullable=True)
    char_count = Column(Integer, default=0)

    document = relationship("UserDocument", back_populates="pages")


class Clause(Base):
    __tablename__ = "clauses"

    id = Column(String(36), primary_key=True, default=generate_uuid)
    document_id = Column(String(36), ForeignKey("documents.id"), nullable=False, index=True)
    clause_type = Column(String(80), nullable=False, index=True)  # Payment, Termination, Indemnity, etc.
    original_text = Column(Text, nullable=False)
    page_number = Column(Integer, default=1)
    section_reference = Column(String(100), nullable=True)
    plain_explanation = Column(Text, nullable=False)
    obligation = Column(Text, nullable=True)
    attention_category = Column(String(50), default="Informational")  # Informational, Review, Important review, Professional review recommended
    potential_concern = Column(Text, nullable=True)
    relevant_legal_concept = Column(String(200), nullable=True)
    suggested_verification = Column(Text, nullable=True)
    confidence = Column(Float, default=0.92)

    document = relationship("UserDocument", back_populates="clauses")
    timeline_events = relationship("TimelineEvent", back_populates="clause_rel")


class TimelineEvent(Base):
    __tablename__ = "timeline_events"

    id = Column(String(36), primary_key=True, default=generate_uuid)
    document_id = Column(String(36), ForeignKey("documents.id"), nullable=False, index=True)
    clause_id = Column(String(36), ForeignKey("clauses.id"), nullable=True)
    date_str = Column(String(100), nullable=False)
    event_description = Column(String(500), nullable=False)
    event_type = Column(String(50), default="Obligation")  # Effective Date, Deadline, Notice, Payment, Expiry
    page_number = Column(Integer, default=1)
    confidence = Column(Float, default=0.95)
    is_manual_override = Column(Boolean, default=False)
    created_at = Column(DateTime(timezone=True), default=get_utc_now)

    document = relationship("UserDocument", back_populates="timeline_events")
    clause_rel = relationship("Clause", back_populates="timeline_events")


class CaseRecord(Base):
    __tablename__ = "cases"

    id = Column(String(36), primary_key=True, default=generate_uuid)
    case_title = Column(String(300), nullable=False, index=True)
    court = Column(String(150), nullable=False, index=True)  # Supreme Court of India, Delhi High Court, etc.
    case_number = Column(String(100), nullable=True, index=True)
    cnr_number = Column(String(50), nullable=True, index=True)
    decision_date = Column(String(50), nullable=True)
    year = Column(Integer, nullable=True, index=True)
    parties = Column(String(300), nullable=True)
    bench = Column(String(200), nullable=True)
    acts_referred = Column(JSON, default=list)
    relevant_excerpt = Column(Text, nullable=False)
    official_url = Column(String(500), nullable=False)
    source_type = Column(String(50), default="court_record")
    retrieved_at = Column(DateTime(timezone=True), default=get_utc_now)
    status = Column(String(50), default="Disposed")
    requires_official_search = Column(Boolean, default=False)


class LegalConcept(Base):
    __tablename__ = "legal_concepts"

    id = Column(String(36), primary_key=True, default=generate_uuid)
    name = Column(String(150), unique=True, nullable=False, index=True)
    description = Column(Text, nullable=False)
    domain = Column(String(100), default="General Commercial Law")


class LegalRelationship(Base):
    __tablename__ = "legal_relationships"

    id = Column(String(36), primary_key=True, default=generate_uuid)
    source_type = Column(String(50), nullable=False)  # Act, Section, Case, Concept
    source_id = Column(String(100), nullable=False)
    target_type = Column(String(50), nullable=False)
    target_id = Column(String(100), nullable=False)
    relation_type = Column(String(50), nullable=False)  # CITES, INTERPRETS, AMENDS, REPEALS, REFERENCES, RELATED_TO
    notes = Column(Text, nullable=True)


class ResearchQuery(Base):
    __tablename__ = "research_queries"

    id = Column(String(36), primary_key=True, default=generate_uuid)
    query_text = Column(String(1000), nullable=False)
    jurisdiction = Column(String(100), default="India")
    date_context = Column(String(100), default="CURRENT")
    sources_used = Column(JSON, default=list)
    answer = Column(Text, nullable=False)
    claims_json = Column(JSON, default=list)
    evidence_json = Column(JSON, default=list)
    limitations_json = Column(JSON, default=list)
    lawyer_questions_json = Column(JSON, default=list)
    retrieved_at = Column(DateTime(timezone=True), default=get_utc_now)
    freshness_status = Column(String(50), default="RECENT")


class SourceSyncLog(Base):
    __tablename__ = "source_sync_logs"

    id = Column(String(36), primary_key=True, default=generate_uuid)
    source = Column(String(100), nullable=False, index=True)
    started_at = Column(DateTime(timezone=True), default=get_utc_now)
    completed_at = Column(DateTime(timezone=True), nullable=True)
    status = Column(String(50), default="IN_PROGRESS")  # IN_PROGRESS, COMPLETED, FAILED
    documents_checked = Column(Integer, default=0)
    documents_added = Column(Integer, default=0)
    documents_changed = Column(Integer, default=0)
    documents_failed = Column(Integer, default=0)
    error_message = Column(Text, nullable=True)


class AuditLog(Base):
    __tablename__ = "audit_logs"

    id = Column(String(36), primary_key=True, default=generate_uuid)
    action = Column(String(100), nullable=False)
    resource_type = Column(String(50), nullable=False)
    resource_id = Column(String(100), nullable=True)
    details = Column(JSON, default=dict)
    timestamp = Column(DateTime(timezone=True), default=get_utc_now)
