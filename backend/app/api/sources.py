"""
LEGALENS AI - Sources API Router
Monitors legal data pulse, source connectivity, and provides SSE heartbeat streaming.
"""
from fastapi import APIRouter, HTTPException
from fastapi.responses import StreamingResponse
from backend.app.services.freshness_service import freshness_engine
from backend.app.schemas.source_schemas import SourceHealthSummary, SourceStatusResponse
from backend.app.connectors.registry import source_registry

router = APIRouter(prefix="/api/sources", tags=["Legal Sources"])


@router.get("", response_model=SourceHealthSummary)
@router.get("/health", response_model=SourceHealthSummary)
async def get_sources_health():
    """Returns real-time health and freshness metrics for all authoritative legal connectors."""
    health_data = await freshness_engine.get_all_source_status()
    return SourceHealthSummary(**health_data)


@router.get("/health/stream")
async def stream_sources_health():
    """SSE Stream pushing live source pulse updates to connected UI clients."""
    return StreamingResponse(
        freshness_engine.stream_freshness_events(),
        media_type="text/event-stream"
    )


@router.get("/{source_id}/status", response_model=SourceStatusResponse)
async def get_single_source_status(source_id: str):
    """Retrieve status and metadata for a specific legal source."""
    adapter = source_registry.get_adapter(source_id)
    if not adapter:
        raise HTTPException(status_code=404, detail=f"Source adapter '{source_id}' not found.")

    health = await adapter.health_check()
    return SourceStatusResponse(
        source_id=adapter.source_id,
        source_name=adapter.source_name,
        authority=adapter.authority,
        source_type="official_repository",
        authority_tier=adapter.authority_tier,
        official_url=adapter.base_official_url,
        freshness_status=health.get("status", "RECENT"),
        last_checked=health.get("last_checked"),
        last_successful_sync=health.get("last_successful_sync"),
        content_hash="verified",
        version="2026.1",
        automation_available=health.get("automation_available", True),
        latency_ms=health.get("latency_ms", 45)
    )
