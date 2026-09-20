# Testing

```bash
pytest
```

Covers hallucination (imaginary Section 9999), temporal versioning (DPDP not in force in 2022), PII, prompt injection, comparison, source adapters, and the upload→analyze→compare→lawyer-prep path.

Frontend lint/typecheck/build are separate quality gates and require `npm install` in `frontend/`.
