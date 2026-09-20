"""
Integration Tests for Authoritative Source Adapters
Verifies real provenance, content hashes, official URLs, and compliant CAPTCHA handling.
"""
import pytest
from backend.app.connectors.india_code_adapter import IndiaCodeAdapter
from backend.app.connectors.supreme_court_adapter import SupremeCourtAdapter
from backend.app.connectors.ecourts_adapter import ECourtsAdapter
from backend.app.connectors.njdg_adapter import NJDGAdapter
from backend.app.connectors.high_court_adapter import HighCourtAdapter


@pytest.mark.asyncio
async def test_india_code_adapter_search_and_provenance():
    adapter = IndiaCodeAdapter()
    results = await adapter.search("Section 73 breach of contract")
    assert len(results) > 0
    top = results[0]
    assert top["source_id"] == "india_code"
    assert top["authority_tier"] == "TIER 1"
    assert "indiacode.nic.in" in top["official_url"]
    assert top["content_hash"] is not None
    assert "Section 73" in top["section_number"]


@pytest.mark.asyncio
async def test_supreme_court_adapter_case_lookup():
    adapter = SupremeCourtAdapter()
    results = await adapter.search("Kailash Nath Section 74")
    assert len(results) > 0
    top = results[0]
    assert top["source_id"] == "supreme_court"
    assert "Kailash Nath" in top["case_title"]
    assert "sci.gov.in" in top["official_url"]
    assert top["citation"] == "(2015) 4 SCC 136"


@pytest.mark.asyncio
async def test_ecourts_adapter_captcha_compliance():
    """
    Verifies that eCourts adapter NEVER attempts CAPTCHA bypass and returns
    compliant official search guidance.
    """
    adapter = ECourtsAdapter()
    results = await adapter.search("Commercial Suit 104 of 2024")
    assert len(results) == 1
    res = results[0]
    assert res["status"] == "official_search_required"
    assert res["automation_available"] is False
    assert "CAPTCHA" in res["reason"]
    assert "services.ecourts.gov.in" in res["official_url"]


@pytest.mark.asyncio
async def test_njdg_adapter_health():
    adapter = NJDGAdapter()
    health = await adapter.health_check()
    assert health["source_id"] == "njdg"
    assert health["authority_tier"] == "TIER 1"
    assert health["status"] in ["LIVE", "RECENT"]
