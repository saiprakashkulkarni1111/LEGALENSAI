"""
Pydantic Schemas for Legal Research, Case Explorer, and Provenance.
Strict evidence-first contracts ensuring "NO EVIDENCE -> NO CLAIM".
"""
from typing import List, Optional, Dict, Any
from pydantic import BaseModel, Field


class ClaimValidationItem(BaseModel):
    claim_text: str
    is_verified: bool
    evidence_ids: List[str]
    confidence: str  # HIGH, MEDIUM, UNVERIFIED
    entailment_reasoning: str


class LegalSourceReference(BaseModel):
    source_id: str
    source_name: str
    authority: str
    source_type: str
    authority_tier: str  # TIER 1, TIER 2, etc.
    official_url: str
    retrieved_at: str
    published_at: Optional[str] = None
    effective_from: Optional[str] = None
    effective_until: Optional[str] = None
    version: str
    content_hash: str
    retrieval_method: str
    freshness_status: str


class EvidenceItem(BaseModel):
    id: str
    source_title: str
    section_or_para: str
    exact_text: str
    official_url: str
    authority: str
    authority_tier: str
    effective_period: str
    relevance_score: float


class LiveResearchRequest(BaseModel):
    query: str
    jurisdiction: str = "India"
    date_context: str = Field(default="CURRENT", description="CURRENT or specific year e.g. '2022'")
    document_id: Optional[str] = None
    clause_id: Optional[str] = None
    sources: List[str] = ["india_code", "supreme_court", "ecourts", "njdg"]


class LiveResearchResponse(BaseModel):
    query: str
    answer: str
    what_document_says: Optional[str] = None
    evidence: List[EvidenceItem]
    claims: List[ClaimValidationItem]
    legal_sources: List[LegalSourceReference]
    what_this_means: str
    what_to_verify: List[str]
    possible_next_steps: List[str]
    questions_for_lawyer: List[str]
    retrieved_at: str
    freshness_status: str
    limitations: List[str]
    responsible_ai_notice: str = (
        "LEGALENS AI provides legal information assistance based on retrieved authoritative sources. "
        "It does not provide legal advice and does not substitute for an advocate or legal counsel."
    )


class CaseSearchRequest(BaseModel):
    query: str
    court: Optional[str] = None
    year: Optional[int] = None
    act: Optional[str] = None
    section: Optional[str] = None


class CaseItemResponse(BaseModel):
    id: str
    case_title: str
    court: str
    case_number: Optional[str] = None
    cnr_number: Optional[str] = None
    decision_date: Optional[str] = None
    year: Optional[int] = None
    parties: Optional[str] = None
    bench: Optional[str] = None
    acts_referred: List[str] = []
    relevant_excerpt: str
    official_url: str
    retrieved_at: str
    status: str
    requires_official_search: bool = False
    official_search_guidance: Optional[str] = None
