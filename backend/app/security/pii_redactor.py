"""
LEGALENS AI - PII Detection & Redaction Engine
Detects and redacts sensitive Indian personal data:
- Aadhaar-like 12-digit numbers
- PAN (Permanent Account Number)
- Indian Mobile / Phone Numbers
- Email Addresses
- Bank Account Numbers and IFSC Codes
"""
import re
from typing import Dict, List, Tuple


class PIIRedactor:
    # Strict regex patterns for Indian identifiers
    AADHAAR_PATTERN = re.compile(r'\b[2-9]{1}[0-9]{3}\s?[0-9]{4}\s?[0-9]{4}\b')
    PAN_PATTERN = re.compile(r'\b[A-Z]{5}[0-9]{4}[A-Z]{1}\b')
    PHONE_PATTERN = re.compile(r'(?:\+91[\-\s]?)?[6-9]\d{9}\b')
    EMAIL_PATTERN = re.compile(r'\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}\b')
    IFSC_PATTERN = re.compile(r'\b[A-Z]{4}0[A-Z0-9]{6}\b')
    BANK_ACC_PATTERN = re.compile(r'\b(?:Account|A/C|Acc\.?)\s*(?:No\.?|Number)?[:\s]*(\d{9,18})\b', re.IGNORECASE)

    @classmethod
    def scan_pii(cls, text: str) -> Dict[str, int]:
        """Scan text and return counts of detected PII types without modifying text."""
        if not text:
            return {}

        counts = {
            "aadhaar": len(cls.AADHAAR_PATTERN.findall(text)),
            "pan": len(cls.PAN_PATTERN.findall(text)),
            "phone": len(cls.PHONE_PATTERN.findall(text)),
            "email": len(cls.EMAIL_PATTERN.findall(text)),
            "ifsc": len(cls.IFSC_PATTERN.findall(text)),
            "bank_account": len(cls.BANK_ACC_PATTERN.findall(text))
        }
        return {k: v for k, v in counts.items() if v > 0}

    @classmethod
    def redact_text(cls, text: str) -> Tuple[str, int, List[str]]:
        """
        Redact sensitive identifiers in text, replacing them with typed placeholders.
        Returns (redacted_text, total_redactions, detected_types).
        """
        if not text:
            return text, 0, []

        redacted = text
        total_redactions = 0
        detected_types = []

        # Redact Aadhaar
        aadhaar_matches = cls.AADHAAR_PATTERN.findall(redacted)
        if aadhaar_matches:
            total_redactions += len(aadhaar_matches)
            detected_types.append("Aadhaar")
            redacted = cls.AADHAAR_PATTERN.sub("[REDACTED AADHAAR]", redacted)

        # Redact PAN
        pan_matches = cls.PAN_PATTERN.findall(redacted)
        if pan_matches:
            total_redactions += len(pan_matches)
            detected_types.append("PAN")
            redacted = cls.PAN_PATTERN.sub("[REDACTED PAN]", redacted)

        # Redact Email
        email_matches = cls.EMAIL_PATTERN.findall(redacted)
        if email_matches:
            total_redactions += len(email_matches)
            detected_types.append("Email")
            redacted = cls.EMAIL_PATTERN.sub("[REDACTED EMAIL]", redacted)

        # Redact Phone
        phone_matches = cls.PHONE_PATTERN.findall(redacted)
        if phone_matches:
            total_redactions += len(phone_matches)
            detected_types.append("Phone")
            redacted = cls.PHONE_PATTERN.sub("[REDACTED PHONE]", redacted)

        # Redact IFSC
        ifsc_matches = cls.IFSC_PATTERN.findall(redacted)
        if ifsc_matches:
            total_redactions += len(ifsc_matches)
            detected_types.append("IFSC Code")
            redacted = cls.IFSC_PATTERN.sub("[REDACTED IFSC]", redacted)

        # Redact Bank Account Numbers
        def mask_bank_acc(match):
            full_match = match.group(0)
            acc_num = match.group(1)
            return full_match.replace(acc_num, "[REDACTED BANK ACCOUNT]")

        bank_matches = cls.BANK_ACC_PATTERN.findall(redacted)
        if bank_matches:
            total_redactions += len(bank_matches)
            detected_types.append("Bank Account")
            redacted = cls.BANK_ACC_PATTERN.sub(mask_bank_acc, redacted)

        return redacted, total_redactions, list(set(detected_types))
