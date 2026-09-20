"""
Unit Tests for Legal Temporal Versioning & Change Intelligence
Verifies: When user asks "What law applied in 2022?", retrieve the applicable version,
and do not return future statutes that were not yet in force.
"""
import pytest
from backend.app.services.version_control_service import version_control_service


@pytest.mark.asyncio
async def test_historical_version_statute_not_yet_in_force():
    # DPDP Act was enacted in 2023. In 2022, it was not yet in force.
    result = await version_control_service.get_applicable_version("dpdp_act_sec_6", target_year=2022)
    assert result is not None
    assert result["status"] == "NOT_YET_IN_FORCE"
    assert "did not apply in 2022" in result["message"]


@pytest.mark.asyncio
async def test_historical_version_statute_in_force():
    # Contract Act Section 73 (1872) applied in 2022
    result = await version_control_service.get_applicable_version("contract_act_sec_73", target_year=2022)
    assert result is not None
    assert result["status"] == "APPLICABLE_VERSION"
    assert result["section_number"] == "Section 73"
    assert result["applicable_to_year"] == 2022
