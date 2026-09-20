"""
End-to-End Workflow Integration Test
Exercises complete lifecycle:
Upload Document -> Analyze Clauses -> Extract Timeline -> Compare Contracts -> Generate Lawyer Prep Pack
"""
import os
import pytest
from starlette.testclient import TestClient
from backend.app.main import app


@pytest.fixture(scope="module")
def client():
    with TestClient(app) as c:
        yield c


def test_full_document_lifecycle(client):
    demo_path = os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__)))), "data", "demo", "Software_Employment_Agreement.txt")
    amended_path = os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__)))), "data", "demo", "Software_Employment_Agreement_Amended.txt")

    assert os.path.exists(demo_path)

    # 1. Upload Document A
    with open(demo_path, "rb") as f:
        res_a = client.post(
            "/api/documents/upload",
            files={"file": ("Software_Employment_Agreement.txt", f, "text/plain")},
            data={"mode": "SYNTHETIC_DEMO", "auto_redact": "true"}
        )
    assert res_a.status_code == 200
    doc_a_data = res_a.json()
    doc_a_id = doc_a_data["id"]
    assert doc_a_data["pii_detected_count"] >= 2  # Aadhaar & PAN detected
    assert doc_a_data["pii_redacted"] is True

    # 2. Get Document Details & Clauses
    res_details = client.get(f"/api/documents/{doc_a_id}")
    assert res_details.status_code == 200
    details = res_details.json()
    assert details["total_clauses"] >= 6
    assert details["total_obligations"] >= 3
    assert details["total_deadlines"] >= 1

    # Verify specific clauses identified
    clause_types = [c["clause_type"] for c in details["clauses"]]
    assert "Termination" in clause_types
    assert "Non-Compete & Restraint of Trade" in clause_types

    # 3. Upload Document B (Amended)
    with open(amended_path, "rb") as f:
        res_b = client.post(
            "/api/documents/upload",
            files={"file": ("Software_Employment_Agreement_Amended.txt", f, "text/plain")},
            data={"mode": "SYNTHETIC_DEMO", "auto_redact": "true"}
        )
    assert res_b.status_code == 200
    doc_b_id = res_b.json()["id"]

    # 4. Compare Document A vs Document B
    compare_res = client.post(
        "/api/documents/compare",
        json={"doc_a_id": doc_a_id, "doc_b_id": doc_b_id}
    )
    assert compare_res.status_code == 200
    diff_data = compare_res.json()
    assert diff_data["total_differences"] >= 1

    # 5. Generate Lawyer Prep Pack
    prep_res = client.post(
        "/api/lawyer-prep/generate",
        json={"document_id": doc_a_id, "user_notes": "Employee departed after 4 months."}
    )
    assert prep_res.status_code == 200
    prep_data = prep_res.json()
    assert len(prep_data["questions_for_counsel"]) >= 3
    assert len(prep_data["missing_information"]) >= 2
    assert "CONFIDENTIAL BRIEFING PREPARATION" in prep_data["legal_disclaimer"]
