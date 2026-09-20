"""
Background freshness worker.

Large ingestion never runs inside HTTP request handlers.
If Redis/Celery is unavailable, this module can still run as a periodic asyncio job.
"""
import asyncio
import logging
from datetime import datetime, timezone
from backend.app.connectors.registry import source_registry
from backend.app.models.database import AsyncSessionLocal
from backend.app.models.models import SourceSyncLog

logger = logging.getLogger("legalens.worker")


async def run_source_sync_cycle() -> None:
    async with AsyncSessionLocal() as session:
        for adapter in source_registry.list_adapters():
            log = SourceSyncLog(source=adapter.source_id, status="IN_PROGRESS")
            session.add(log)
            await session.flush()
            try:
                health = await adapter.health_check()
                log.status = "COMPLETED" if health.get("status") != "UNAVAILABLE" else "FAILED"
                log.documents_checked = health.get("provisions_indexed") or health.get("judgments_indexed") or 0
                log.completed_at = datetime.now(timezone.utc)
                if log.status == "FAILED":
                    log.error_message = "Source reported unavailable during health check."
            except Exception as exc:
                log.status = "FAILED"
                log.error_message = str(exc)
                log.completed_at = datetime.now(timezone.utc)
            await session.commit()
            logger.info("Sync cycle complete for %s: %s", adapter.source_id, log.status)


async def scheduler_loop(interval_seconds: int = 3600) -> None:
    while True:
        await run_source_sync_cycle()
        await asyncio.sleep(interval_seconds)


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO)
    asyncio.run(run_source_sync_cycle())
