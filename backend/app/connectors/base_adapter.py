"""
LEGALENS AI - Legal Source Adapter Interface
Abstract base class for all authoritative Indian legal source connectors.
Follows the non-negotiable rule: NEVER bypass CAPTCHA, authentication, or robots.txt.
"""
from abc import ABC, abstractmethod
from typing import Dict, Any, List, Optional
from datetime import datetime, timezone


class LegalSourceAdapter(ABC):
    @property
    @abstractmethod
    def source_id(self) -> str:
        """Unique key e.g. 'india_code', 'supreme_court'."""
        pass

    @property
    @abstractmethod
    def source_name(self) -> str:
        """Human-readable name e.g. 'India Code'."""
        pass

    @property
    @abstractmethod
    def authority(self) -> str:
        """Governmental or judicial authority e.g. 'Government of India'."""
        pass

    @property
    @abstractmethod
    def authority_tier(self) -> str:
        """TIER 1 (Official gov/court), TIER 2 (Official publication), etc."""
        pass

    @property
    @abstractmethod
    def base_official_url(self) -> str:
        """Official web portal URL."""
        pass

    @abstractmethod
    async def search(self, query: str, filters: Optional[Dict[str, Any]] = None) -> List[Dict[str, Any]]:
        """
        Search the source for statutes, sections, or judgments.
        Returns normalized list of results with content hashes and provenance.
        """
        pass

    @abstractmethod
    async def fetch(self, document_id: str) -> Optional[Dict[str, Any]]:
        """Fetch full document content or provision text."""
        pass

    @abstractmethod
    async def get_metadata(self, document_id: str) -> Dict[str, Any]:
        """Fetch metadata including effective dates and version."""
        pass

    @abstractmethod
    def get_url(self, document_id: str) -> str:
        """Generate official deep-link URL."""
        pass

    @abstractmethod
    async def health_check(self) -> Dict[str, Any]:
        """
        Check connectivity and freshness of the source.
        Returns {"status": "LIVE"|"RECENT"|"UNAVAILABLE", "latency_ms": int, "last_checked": str}
        """
        pass
