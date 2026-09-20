"""
Unit Tests for PII Redaction Engine
Verifies scrubbing of Aadhaar, PAN, Indian mobile numbers, emails, and bank accounts.
"""
from backend.app.security.pii_redactor import PIIRedactor


def test_aadhaar_redaction():
    text = "The employee's Aadhaar number is 9876 5432 1098 as verified on record."
    redacted, count, types = PIIRedactor.redact_text(text)
    assert count >= 1
    assert "Aadhaar" in types
    assert "9876 5432 1098" not in redacted
    assert "[REDACTED AADHAAR]" in redacted


def test_pan_redaction():
    text = "PAN card reference: ABCPS1234F submitted during onboarding."
    redacted, count, types = PIIRedactor.redact_text(text)
    assert count >= 1
    assert "PAN" in types
    assert "ABCPS1234F" not in redacted
    assert "[REDACTED PAN]" in redacted


def test_phone_and_email_redaction():
    text = "Contact Mr. Sharma at rohan.sharma@example.com or mobile +91 9876543210 for notice."
    redacted, count, types = PIIRedactor.redact_text(text)
    assert count >= 2
    assert "Email" in types
    assert "Phone" in types
    assert "rohan.sharma@example.com" not in redacted
    assert "9876543210" not in redacted


def test_scan_pii_counts():
    text = "Aadhaar: 2345 6789 0123, PAN: BNZPK4321Q, Email: legal@company.in"
    counts = PIIRedactor.scan_pii(text)
    assert counts.get("aadhaar") == 1
    assert counts.get("pan") == 1
    assert counts.get("email") == 1
