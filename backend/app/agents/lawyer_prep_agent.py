"""
LEGALENS AI - Lawyer Preparation Agent
Synthesizes a structured, exportable briefing pack for the user to take to legal counsel.
Highlights facts, missing info, relevant statutory authorities, and targeted questions.
"""
from typing import Dict, Any, List
from datetime import datetime, timezone
from backend.app.agents.base import BaseAgent


class LawyerPrepAgent(BaseAgent):
    @property
    def name(self) -> str:
        return "LawyerPrepAgent"

    @property
    def description(self) -> str:
        return "Generates a structured legal preparation dossier with key facts, statutory context, and targeted questions for counsel."

    async def process(self, context: Dict[str, Any]) -> Dict[str, Any]:
        """
        Generate lawyer prep pack from document metadata, extracted clauses, and timeline.
        context: {
            "document": Dict[str, Any],
            "clauses": List[Dict[str, Any]],
            "timeline": List[Dict[str, Any]],
            "user_notes": Optional[str]
        }
        """
        doc = context.get("document", {})
        clauses = context.get("clauses", [])
        timeline = context.get("timeline", [])
        user_notes = context.get("user_notes")

        doc_title = doc.get("title", "Legal Agreement")
        parties = doc.get("parties_detected", ["Party 1", "Party 2"])
        parties_str = " and ".join(parties) if parties else "the contracting entities"

        # Categorize review items
        review_clauses = [c for c in clauses if c.get("attention_category") in ["Review", "Important review", "Professional review recommended"]]

        questions_for_counsel = [
            f"Are the termination notice periods in Section/Clause balanced and enforceable under current Indian contract practice?",
            f"Does the non-compete/restraint clause exceed the permissible boundaries of Section 27 of the Indian Contract Act, 1872?",
            f"Is the pre-agreed liquidated damages or penalty provision enforceable without concrete proof of actual loss (ref: Kailash Nath Associates v. DDA)?",
            f"Are there statutory provisions in my specific state jurisdiction that impact the dispute resolution or venue choice?",
            f"What specific documentary evidence should I preserve in case of an anticipatory or actual breach?"
        ]

        missing_info = [
            "Signed execution and signature pages (to verify authority of executing signatories).",
            "Ancillary schedules, service level agreements (SLAs), or statements of work referenced in the main text.",
            "Prior correspondence, notices, or email exchanges relating to contractual amendments or waivers.",
            "Official stamp duty proof or registration certificate where applicable under the Indian Stamp Act."
        ]

        matters_for_review = []
        for c in review_clauses:
            matters_for_review.append(f"{c.get('clause_type')}: {c.get('potential_concern', 'Requires legal interpretation.')}")

        return {
            "document_title": doc_title,
            "prepared_at": datetime.now(timezone.utc).strftime("%d %B %Y, %H:%M UTC"),
            "executive_summary": (
                f"Briefing dossier prepared for legal consultation regarding '{doc_title}'. "
                f"The agreement governs relations between {parties_str}, containing {len(clauses)} analyzed clauses, "
                f"{len(review_clauses)} items flagged for professional review, and {len(timeline)} chronological milestones."
            ),
            "key_facts": [
                f"Document Type: {doc.get('doc_type', 'Commercial Contract')}",
                f"Parties Identified: {', '.join(parties) if parties else 'Unspecified in header'}",
                f"Effective Date: {doc.get('effective_date_detected', 'Refer to execution date')}",
                f"Jurisdiction Clause: {doc.get('jurisdiction_detected', 'India (General)')}"
            ],
            "timeline": timeline[:8],
            "documents_provided": [doc.get("file_name", "Primary uploaded contract")],
            "missing_information": missing_info,
            "key_clauses": clauses[:6],
            "relevant_statutory_provisions": [
                {"act": "The Indian Contract Act, 1872", "sections": "Section 27 (Restraint of Trade), Section 73 & 74 (Breach & Damages)"},
                {"act": "The Arbitration and Conciliation Act, 1996", "sections": "Section 7 (Arbitration Agreement)"},
                {"act": "The Specific Relief Act, 1963", "sections": "Section 14 (Contracts not specifically enforceable)"}
            ],
            "related_cases": [
                {"case": "Kailash Nath Associates v. DDA (2015) 4 SCC 136", "principle": "Liquidated damages require reasonable compensation, not penalty."},
                {"case": "Percept D'Mark v. Zaheer Khan (2006) 4 SCC 227", "principle": "Post-termination restrictive covenants are void under Section 27."}
            ],
            "questions_for_counsel": questions_for_counsel,
            "matters_for_professional_review": matters_for_review if matters_for_review else ["General compliance and enforceability of standard covenants."],
            "legal_disclaimer": (
                "CONFIDENTIAL BRIEFING PREPARATION: This document is an automated analytical summary prepared "
                "to assist you in consulting with a licensed advocate. It does not constitute legal advice or create "
                "an attorney-client relationship. Please have all provisions reviewed by qualified legal counsel."
            )
        }
