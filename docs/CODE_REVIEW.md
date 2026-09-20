# Code review notes

Checked:

- No CAPTCHA bypass in eCourts adapter
- Demo vs real mode fields on documents
- Citation verifier rejects empty/mismatched evidence
- CORS no longer uses `*` with credentials
- Default JWT secrets are development-only (must rotate in production)
- `init_db` now imports models so tables register
- Large sync work is in `workers/sync_worker.py`, not request handlers

Follow-ups:

- Organization RBAC and per-user document isolation
- Replace in-memory rate limiter with Redis in production
- Wire real OCR (Tesseract) in deployment images
- Playwright e2e against the Next.js app
