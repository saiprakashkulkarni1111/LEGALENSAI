# Evaluation mapping

| Criterion | Implementation | Files | Tests | UI evidence |
| --- | --- | --- | --- | --- |
| Code quality | Typed FastAPI + strict TS frontend, layered agents | `backend/app/**`, `frontend/**` | pytest, `npm run typecheck` | Workspace pages |
| Security | Auth, rate limit, PII, injection defense, no CAPTCHA bypass | `backend/app/security/**`, connectors | `test_prompt_injection.py`, `test_pii_redactor.py` | Redaction on upload |
| Efficiency | Deterministic clause/date extraction; LLM optional | parser, agents, vector_index | unit tests | Fast local analysis without API keys |
| Testing | Unit, integration, e2e, accessibility, hallucination | `tests/**` | `pytest` | Demo employment agreement flow |
| Accessibility | Semantic nav, labels, focus, text attention categories | `frontend/components/AppShell.tsx` | `test_accessibility_standards.py` | Keyboard-focusable controls |
| Problem alignment | Features A–L mapped | `docs/FEATURE_TRACEABILITY.md` | e2e + research tests | Landing + workspace |
