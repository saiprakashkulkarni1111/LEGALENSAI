"""
Pydantic Schemas for Legal Sources, Health Monitoring, and Sync Logs.
"""
from typing import List, Optional
from datetime import datetime
from pydantic import BaseModel


class SourceStatusResponse(BaseModel):
    source_id: str
    source_name: str
    authority: str
    source_type: str
    authority_tier: str
    official_url: str
    freshness_status: str  # LIVE, RECENT, STALE, UNAVAILABLE
    last_checked: datetime
    last_successful_sync: datetime
    content_hash: Optional[str] = None
    version: str
    automation_available: bool
    latency_ms: int = 45


class SourceHealthSummary(BaseModel):
    total_sources: int
    healthy_sources: int
    stale_sources: int
    unavailable_sources: int
    last_global_sync: datetime
    system_pulse: str  # OPERATIONAL, DEGRADED, SYNCING
    sources: List[SourceStatusResponse]


class SourceSyncLogItem(BaseModel):
    id: str
    source: str
    started_at: datetime
    completed_at: Optional[datetime]
    status: str
    documents_checked: int
    documents_added: int
    documents_changed: int
    documents_failed: int
    error_message: Optional[str] = None
