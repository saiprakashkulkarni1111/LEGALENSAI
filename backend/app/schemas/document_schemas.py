"""
Pydantic Schemas for Document Ingestion, Analysis, Comparison, and Lawyer Preparation.
"""
from typing import List, Optional, Dict, Any
from datetime import datetime
from pydantic import BaseModel, Field


class ClauseItem(BaseModel):
    id: str
    clause_type: str
    original_text: str
    page_number: int
    section_reference: Optional[str] = None
    plain_explanation: str
    obligation: Optional[str] = None
    attention_category: str = Field(
        default="Informational",
        description="Informational | Requires review | Important review | Professional review recommended"
    )
    potential_concern: Optional[str] = None
    relevant_legal_concept: Optional[str] = None
    suggested_verification: Optional[str] = None
    confidence: float = 0.95


class DocumentPageItem(BaseModel):
    page_number: int
    text: str
    char_count: int = 0


class TimelineEventItem(BaseModel):
    id: str
    date_str: str
    event_description: str
    event_type: str
    page_number: int
    confidence: float
    is_manual_override: bool = False


class DocumentDetail(BaseModel):
    id: str
    title: str
    file_name: str
    file_type: str
    file_size: int
    mode: str
    status: str
    doc_type: str
    jurisdiction_detected: str
    parties_detected: List[str]
    effective_date_detected: Optional[str]
    pii_detected_count: int
    pii_redacted: bool
    summary: Optional[str]
    created_at: datetime
    clauses: List[ClauseItem] = []
    timeline_events: List[TimelineEventItem] = []
    pages: List[DocumentPageItem] = []
    total_clauses: int = 0
    total_obligations: int = 0
    total_deadlines: int = 0
    items_requiring_review: int = 0


class DocumentUploadResponse(BaseModel):
    id: str
    title: str
    file_name: str
    status: str
    mode: str
    pii_detected_count: int
    pii_redacted: bool
    message: str


class DocumentCompareRequest(BaseModel):
    doc_a_id: str
    doc_b_id: str


class ClauseDiffItem(BaseModel):
    clause_type: str
    change_type: str  # ADDED, REMOVED, MODIFIED, UNCHANGED
    doc_a_text: Optional[str] = None
    doc_b_text: Optional[str] = None
    doc_a_page: Optional[int] = None
    doc_b_page: Optional[int] = None
    delta_summary: str
    potential_significance: str


class DocumentCompareResponse(BaseModel):
    doc_a_title: str
    doc_b_title: str
    total_differences: int
    added_count: int
    removed_count: int
    modified_count: int
    diffs: List[ClauseDiffItem]
    neutral_observations: List[str]


class LawyerPrepRequest(BaseModel):
    document_id: str
    user_notes: Optional[str] = None


class LawyerPrepResponse(BaseModel):
    document_title: str
    prepared_at: str
    executive_summary: str
    key_facts: List[str]
    timeline: List[TimelineEventItem]
    documents_provided: List[str]
    missing_information: List[str]
    key_clauses: List[ClauseItem]
    relevant_statutory_provisions: List[Dict[str, Any]]
    related_cases: List[Dict[str, Any]]
    questions_for_counsel: List[str]
    matters_for_professional_review: List[str]
    legal_disclaimer: str
