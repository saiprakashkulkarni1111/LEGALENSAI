from backend.app.connectors.base_adapter import LegalSourceAdapter
from backend.app.connectors.india_code_adapter import IndiaCodeAdapter
from backend.app.connectors.supreme_court_adapter import SupremeCourtAdapter
from backend.app.connectors.ecourts_adapter import ECourtsAdapter
from backend.app.connectors.njdg_adapter import NJDGAdapter
from backend.app.connectors.high_court_adapter import HighCourtAdapter
from backend.app.connectors.registry import source_registry, LegalSourceRegistry

__all__ = [
    "LegalSourceAdapter",
    "IndiaCodeAdapter",
    "SupremeCourtAdapter",
    "ECourtsAdapter",
    "NJDGAdapter",
    "HighCourtAdapter",
    "source_registry",
    "LegalSourceRegistry"
]
