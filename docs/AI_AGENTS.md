# AI agents

Typed orchestration in `backend/app/agents/orchestrator.py`.

| Agent | Role |
| --- | --- |
| ClauseIntelligenceAgent | Clause types, obligations, attention labels |
| TimelineAgent | Dates and notice windows |
| ComparisonAgent | Added / removed / modified |
| LegalResearchAgent | Evidence-first research |
| CitationVerificationAgent | Entailment / unverified |
| LawyerPrepAgent | Counsel briefing |
| Document-to-law impact | Maps clauses to official hits |

Prompt hierarchy: system policy > application policy > user request > retrieved data > document content.
