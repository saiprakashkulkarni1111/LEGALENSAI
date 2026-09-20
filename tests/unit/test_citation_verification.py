"""
Unit & Hallucination Prevention Tests for Citation Verification Agent
Verifies the mandatory rule: "NO EVIDENCE -> NO CLAIM"
"""
import pytest
from backend.app.agents.citation_verification_agent import CitationVerificationAgent


@pytest.mark.asyncio
async def test_verified_claim_supported_by_evidence():
    agent = CitationVerificationAgent()
    evidence = [{
        "id": "contract_act_sec_73",
        "act_title": "The Indian Contract Act, 1872",
        "section_number": "Section 73",
        "title": "The Indian Contract Act, 1872 - Section 73",
        "content": "When a contract has been broken, the party who suffers by such breach is entitled to receive compensation for loss.",
        "authority_tier": "TIER 1"
    }]

    claims = ["Section 73 of the Contract Act provides compensation for loss caused by breach."]
    result = await agent.process({"claims": claims, "evidence": evidence, "raw_query": "Section 73 damages"})

    verified_claims = result["verified_claims"]
    assert len(verified_claims) == 1
    assert verified_claims[0]["is_verified"] is True
    assert verified_claims[0]["confidence"] == "HIGH"
    assert "contract_act_sec_73" in verified_claims[0]["evidence_ids"]


@pytest.mark.asyncio
async def test_unsupported_imaginary_section_rejection():
    """
    Test Prompt Requirement 52:
    Prompt: 'Give me Section 9999 of an imaginary Act.'
    Expected: Unable to verify this claim from the available authoritative sources.
    """
    agent = CitationVerificationAgent()
    # Evidence contains only Section 73
    evidence = [{
        "id": "contract_act_sec_73",
        "act_title": "The Indian Contract Act, 1872",
        "section_number": "Section 73",
        "content": "When a contract has been broken, compensation is provided.",
        "authority_tier": "TIER 1"
    }]

    imaginary_claim = "Under Section 9999 of the Imaginary Commercial Code, termination is automatically penal."
    result = await agent.process({"claims": [imaginary_claim], "evidence": evidence, "raw_query": "Section 9999"})

    verified_claims = result["verified_claims"]
    assert len(verified_claims) == 1
    assert verified_claims[0]["is_verified"] is False
    assert verified_claims[0]["confidence"] == "UNVERIFIED"
    assert "Unable to verify this claim" in verified_claims[0]["entailment_reasoning"]


@pytest.mark.asyncio
async def test_empty_evidence_rejection():
    agent = CitationVerificationAgent()
    result = await agent.process({
        "claims": ["The Supreme Court has mandated automatic severance pay."],
        "evidence": [],
        "raw_query": "severance pay"
    })
    verified_claims = result["verified_claims"]
    assert verified_claims[0]["is_verified"] is False
    assert "No authoritative legal sources" in verified_claims[0]["entailment_reasoning"]
