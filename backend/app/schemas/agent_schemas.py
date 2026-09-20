"""
Pydantic Schemas for Agent Communication.
Ensures typed, structured messages between agents rather than unstructured text prompts.
"""
from typing import List, Optional, Dict, Any
from pydantic import BaseModel, Field


class AgentMessage(BaseModel):
    sender: str
    receiver: str
    timestamp: str
    payload: Dict[str, Any]


class IntakeOutput(BaseModel):
    document_type: str
    detected_parties: List[str]
    effective_date: Optional[str] = None
    jurisdiction: str = "India"
    pii_counts: Dict[str, int] = {}
    is_safe_to_process: bool = True
    security_flags: List[str] = []


class ClauseIntelligenceOutput(BaseModel):
    clauses: List[Dict[str, Any]]
    total_clauses: int
    obligations_count: int
    review_items_count: int


class TimelineExtractionOutput(BaseModel):
    events: List[Dict[str, Any]]
    has_critical_deadlines: bool


class CitationVerificationResult(BaseModel):
    claim: str
    verified: bool
    evidence_text: Optional[str] = None
    source_url: Optional[str] = None
    reasoning: str
