# Feature traceability

| Problem | Implementation | UI | API | Test |
| --- | --- | --- | --- | --- |
| Simplify documents | ClauseIntelligenceAgent | `/documents/[id]` | POST `/api/documents/upload` | `tests/e2e/test_full_workflow.py` |
| Contract comparison | ComparisonAgent | `/compare` | POST `/api/documents/compare` | `test_comparison_agent.py` |
| Clause identification | ClauseIntelligenceAgent | Document analysis | GET `/api/documents/{id}/clauses` | e2e |
| Obligation extraction | ClauseIntelligenceAgent | Analysis metrics | GET `/api/documents/{id}` | e2e |
| Risk/attention | attention_category enum | Labels, not color-only | clauses payload | `test_accessibility_standards.py` |
| Inconsistency | ComparisonAgent diffs | Compare page | compare API | comparison tests |
| Document Q&A | LegalResearchAgent + document context | Ask on analysis page | POST `/api/documents/{id}/ask` | research integration |
| Legal discovery | Source registry | `/research` | POST `/api/research/live`, GET `/api/legal/search` | `test_api_endpoints.py` |
| Next steps | Evidence-first response schema | Research page | research response | integration |
| Summary | orchestrator summary | Analysis header | document detail | e2e |
| Checklist | checklist generator | Lawyer prep | POST `/api/checklists/generate` | e2e adjacent |
| Lawyer questions | LawyerPrepAgent | `/lawyer-prep` | POST `/api/lawyer-prep/generate` | e2e |
| Source freshness | LegalFreshnessEngine | Overview / Sources | GET `/api/sources/health` | integration |
| Cases | SupremeCourtAdapter + eCourts fallback | `/cases` | GET `/api/cases/search` | integration |
| Document-to-law | impact_engine | Analysis actions | POST `/api/documents/{id}/impact` | adapters |
