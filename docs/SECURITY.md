# Security

## Threat model

- Untrusted uploaded documents (prompt injection, malware-like payloads, oversized files)
- Unauthorized document access
- Secret leakage to the browser
- Scraping official court portals / CAPTCHA bypass
- Hallucinated legal citations presented as fact

## Controls implemented

| Area | Implementation |
| --- | --- |
| Auth | JWT (`/api/auth/login`); development guest analyst if no token |
| Rate limiting | In-memory sliding window middleware |
| File validation | Extension allowlist, size cap |
| PII | Regex redaction for Aadhaar-like, PAN, phone, email, IFSC, bank account |
| Prompt injection | Document text treated as untrusted; detector in `prompt_injection.py` |
| Headers | nosniff, DENY frame, referrer policy |
| Secrets | `.env` gitignored; keys never sent to frontend |
| CAPTCHA | eCourts adapter returns official-search fallback |
| Logging | No raw document dump in application logs of agents |

## Residual risk

Malware scanning is architectural (file stored locally after type/size checks), not a live AV engine. Production should add Cloud Storage + AV and PostgreSQL RLS per organization.
