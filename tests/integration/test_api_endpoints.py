"""
Integration Tests for Core API Endpoints
Verifies document upload, analysis pipeline, live research, and case explorer.
"""
import pytest
from starlette.testclient import TestClient
from backend.app.main import app


@pytest.fixture(scope="module")
def client():
    with TestClient(app) as c:
        yield c


def test_root_endpoint(client):
    response = client.get("/")
    assert response.status_code == 200
    data = response.json()
    assert data["platform"] == "LEGALENS AI"
    assert data["core_doctrine"] == "NO EVIDENCE -> NO CLAIM"


def test_sources_health_endpoint(client):
    response = client.get("/api/sources/health")
    assert response.status_code == 200
    data = response.json()
    assert data["total_sources"] >= 4
    assert data["system_pulse"] in ["OPERATIONAL", "DEGRADED"]
    assert any(s["source_id"] == "india_code" for s in data["sources"])


def test_live_research_real_statute(client):
    payload = {
        "query": "Section 73 Indian Contract Act liquidated damages",
        "jurisdiction": "India",
        "date_context": "CURRENT"
    }
    response = client.post("/api/research/live", json=payload)
    assert response.status_code == 200
    data = response.json()
    assert len(data["evidence"]) > 0
    assert any("Section 73" in e["section_or_para"] for e in data["evidence"])
    assert len(data["legal_sources"]) > 0
    assert "indiacode.nic.in" in data["legal_sources"][0]["official_url"]


def test_live_research_imaginary_statute_hallucination_prevention(client):
    """
    Test Prompt Requirement 52:
    Query imaginary statute, verify system rejects hallucination and returns 'Unable to verify'.
    """
    payload = {
        "query": "Section 9999 of Imaginary Corporate Decree 2028",
        "jurisdiction": "India"
    }
    response = client.post("/api/research/live", json=payload)
    assert response.status_code == 200
    data = response.json()
    assert "Unable to verify" in data["answer"]


def test_case_explorer_search(client):
    response = client.get("/api/cases/search?query=Kailash Nath")
    assert response.status_code == 200
    cases = response.json()
    assert len(cases) > 0
    assert "Kailash Nath" in cases[0]["case_title"]
    assert cases[0]["court"] == "Supreme Court of India"
