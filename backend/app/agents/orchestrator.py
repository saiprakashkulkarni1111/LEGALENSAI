"""
LEGALENS AI - Multi-Agent Orchestrator
Coordinates domain agents into a coherent legal intelligence processing pipeline.
"""
from typing import Dict, Any, List
from backend.app.agents.clause_intelligence_agent import ClauseIntelligenceAgent
from backend.app.agents.timeline_agent import TimelineAgent
from backend.app.agents.comparison_agent import ComparisonAgent
from backend.app.agents.lawyer_prep_agent import LawyerPrepAgent
from backend.app.agents.legal_research_agent import LegalResearchAgent
from backend.app.agents.citation_verification_agent import CitationVerificationAgent
from backend.app.security.pii_redactor import PIIRedactor
from backend.app.security.prompt_injection import PromptInjectionDefense


class AgentOrchestrator:
    def __init__(self):
        self.clause_agent = ClauseIntelligenceAgent()
        self.timeline_agent = TimelineAgent()
        self.comparison_agent = ComparisonAgent()
        self.lawyer_prep_agent = LawyerPrepAgent()
        self.research_agent = LegalResearchAgent()
        self.citation_verifier = CitationVerificationAgent()

    async def analyze_document_pipeline(self, pages: List[Dict[str, Any]], auto_redact: bool = True) -> Dict[str, Any]:
        """
        Execute full document intelligence pipeline:
        PII Scan -> Prompt Injection Guard -> Clause Extraction -> Timeline Extraction -> Summary
        """
        total_pii = 0
        pii_types = []
        processed_pages = []

        # 1. Security & Redaction Pass
        for page in pages:
            raw_text = page.get("text", "")
            # Check prompt injection
            is_suspicious, _ = PromptInjectionDefense.inspect_untrusted_text(raw_text)

            # Redact PII
            redacted_text, count, types_found = PIIRedactor.redact_text(raw_text)
            total_pii += count
            pii_types.extend(types_found)

            processed_pages.append({
                "page_number": page.get("page_number", 1),
                "text": redacted_text if auto_redact else raw_text,
                "raw_text": raw_text,
                "is_suspicious": is_suspicious
            })

        # 2. Clause Intelligence
        clause_res = await self.clause_agent.process({"pages": processed_pages})
        clauses = clause_res.get("clauses", [])

        # 3. Timeline Extraction
        timeline_res = await self.timeline_agent.process({"pages": processed_pages, "clauses": clauses})
        timeline_events = timeline_res.get("events", [])

        # 4. Summary & Metrics Formulation
        total_clauses = len(clauses)
        total_obligations = sum(1 for c in clauses if c.get("obligation"))
        total_deadlines = len(timeline_events)
        review_count = sum(1 for c in clauses if c.get("attention_category") in ["Review", "Important review", "Professional review recommended"])

        summary = (
            f"Comprehensive analysis complete. Document contains {total_clauses} identified clauses across "
            f"{len(pages)} pages, including {total_obligations} contractual obligations and {total_deadlines} milestone dates. "
            f"{review_count} provisions require dedicated review."
        )

        return {
            "processed_pages": processed_pages,
            "clauses": clauses,
            "timeline_events": timeline_events,
            "total_clauses": total_clauses,
            "total_obligations": total_obligations,
            "total_deadlines": total_deadlines,
            "items_requiring_review": review_count,
            "pii_detected_count": total_pii,
            "pii_redacted": auto_redact and total_pii > 0,
            "pii_types_found": list(set(pii_types)),
            "summary": summary
        }

    async def compare_documents(self, doc_a_data: Dict[str, Any], doc_b_data: Dict[str, Any]) -> Dict[str, Any]:
        return await self.comparison_agent.process({
            "doc_a_title": doc_a_data.get("title", "Document A"),
            "doc_b_title": doc_b_data.get("title", "Document B"),
            "doc_a_clauses": doc_a_data.get("clauses", []),
            "doc_b_clauses": doc_b_data.get("clauses", [])
        })

    async def generate_lawyer_prep(self, doc_data: Dict[str, Any], user_notes: str = None) -> Dict[str, Any]:
        return await self.lawyer_prep_agent.process({
            "document": doc_data,
            "clauses": doc_data.get("clauses", []),
            "timeline": doc_data.get("timeline_events", []),
            "user_notes": user_notes
        })

    async def research_query(self, query: str, jurisdiction: str = "India", date_context: str = "CURRENT", doc_context: str = None) -> Dict[str, Any]:
        return await self.research_agent.process({
            "query": query,
            "jurisdiction": jurisdiction,
            "date_context": date_context,
            "document_context": doc_context
        })


orchestrator = AgentOrchestrator()
