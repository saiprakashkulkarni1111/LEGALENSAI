"""
LEGALENS AI - Legal Source Registry
Central orchestrator managing all authoritative Indian legal source connectors.
"""
from typing import Dict, List, Any, Optional
from backend.app.connectors.base_adapter import LegalSourceAdapter
from backend.app.connectors.india_code_adapter import IndiaCodeAdapter
from backend.app.connectors.supreme_court_adapter import SupremeCourtAdapter
from backend.app.connectors.ecourts_adapter import ECourtsAdapter
from backend.app.connectors.njdg_adapter import NJDGAdapter
from backend.app.connectors.high_court_adapter import HighCourtAdapter


class LegalSourceRegistry:
    def __init__(self):
        self._adapters: Dict[str, LegalSourceAdapter] = {}
        # Register core Indian legal adapters
        self.register(IndiaCodeAdapter())
        self.register(SupremeCourtAdapter())
        self.register(ECourtsAdapter())
        self.register(NJDGAdapter())
        self.register(HighCourtAdapter())

    def register(self, adapter: LegalSourceAdapter):
        self._adapters[adapter.source_id] = adapter

    def get_adapter(self, source_id: str) -> Optional[LegalSourceAdapter]:
        return self._adapters.get(source_id)

    def list_adapters(self) -> List[LegalSourceAdapter]:
        return list(self._adapters.values())

    async def search_all(self, query: str, source_ids: Optional[List[str]] = None, filters: Optional[Dict[str, Any]] = None) -> List[Dict[str, Any]]:
        """
        Federated search across selected or all authoritative adapters.
        """
        results = []
        target_adapters = (
            [self._adapters[s] for s in source_ids if s in self._adapters]
            if source_ids
            else list(self._adapters.values())
        )

        for adapter in target_adapters:
            try:
                res = await adapter.search(query, filters=filters)
                results.extend(res)
            except Exception as e:
                # Never crash the entire search if one adapter experiences network interruption
                pass

        results.sort(key=lambda x: x.get("relevance_score", 0.0), reverse=True)
        return results

    async def get_all_health(self) -> List[Dict[str, Any]]:
        health_list = []
        for adapter in self._adapters.values():
            try:
                h = await adapter.health_check()
                health_list.append(h)
            except Exception as e:
                health_list.append({
                    "source_id": adapter.source_id,
                    "source_name": adapter.source_name,
                    "status": "UNAVAILABLE",
                    "error": str(e),
                    "automation_available": False
                })
        return health_list


source_registry = LegalSourceRegistry()
