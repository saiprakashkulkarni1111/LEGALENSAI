"""
Unit Tests for Contract Comparison Agent
Tests clause diffing and numerical period delta detection (e.g., 30-day notice vs 7-day notice).
"""
import pytest
from backend.app.agents.comparison_agent import ComparisonAgent


@pytest.mark.asyncio
async def test_notice_period_reduction_delta():
    agent = ComparisonAgent()
    doc_a_clauses = [{
        "clause_type": "Notice Period",
        "original_text": "Either party may terminate this Agreement by providing 30 days prior written notice to the other party.",
        "page_number": 1
    }]
    doc_b_clauses = [{
        "clause_type": "Notice Period",
        "original_text": "Either party may terminate this Agreement by providing 7 days prior written notice to the other party.",
        "page_number": 1
    }]

    result = await agent.process({
        "doc_a_title": "Draft A",
        "doc_b_title": "Draft B",
        "doc_a_clauses": doc_a_clauses,
        "doc_b_clauses": doc_b_clauses
    })

    assert result["modified_count"] == 1
    diff = result["diffs"][0]
    assert diff["change_type"] == "MODIFIED"
    assert "reduced by 23 days" in diff["delta_summary"]


@pytest.mark.asyncio
async def test_added_and_removed_clauses():
    agent = ComparisonAgent()
    doc_a_clauses = [{
        "clause_type": "Non-Compete & Restraint of Trade",
        "original_text": "Employee shall not engage in competing business for 24 months.",
        "page_number": 2
    }]
    doc_b_clauses = [{
        "clause_type": "Data Privacy and DPDP",
        "original_text": "Employee shall adhere to the Digital Personal Data Protection Act 2023.",
        "page_number": 2
    }]

    result = await agent.process({
        "doc_a_title": "Version 1",
        "doc_b_title": "Version 2",
        "doc_a_clauses": doc_a_clauses,
        "doc_b_clauses": doc_b_clauses
    })

    assert result["removed_count"] == 1
    assert result["added_count"] == 1
    removed = [d for d in result["diffs"] if d["change_type"] == "REMOVED"][0]
    added = [d for d in result["diffs"] if d["change_type"] == "ADDED"][0]
    assert removed["clause_type"] == "Non-Compete & Restraint of Trade"
    assert added["clause_type"] == "Data Privacy and DPDP"
