"""
LEGALENS AI - Clause Intelligence Agent
Identifies key contractual clauses, extracts plain-language explanations, obligations,
and assigns ethical attention categories (never calling clauses 'illegal').
"""
import re
from typing import Dict, Any, List
from backend.app.agents.base import BaseAgent


class ClauseIntelligenceAgent(BaseAgent):
    @property
    def name(self) -> str:
        return "ClauseIntelligenceAgent"

    @property
    def description(self) -> str:
        return "Detects standard contractual clauses, extracts obligations, and flags potential areas requiring review."

    CLAUSE_DEFINITIONS = [
        {
            "type": "Payment",
            "keywords": ["compensation", "remuneration", "salary shall be payable", "invoice", "consideration of"],
            "category": "Informational",
            "explanation": "Sets payment amounts, currency, and when money must be paid.",
            "concern": "Confirm invoicing windows, tax treatment, and whether late-payment interest is one-sided.",
            "concept": "Consideration and payment obligations"
        },
        {
            "type": "Notice",
            "keywords": ["written notice", "notice period", "days prior written notice", "serve notice"],
            "category": "Review",
            "explanation": "Specifies how and when a party must notify the other before a contractual event.",
            "concern": "Check whether notice periods are unusually short or apply only to one party.",
            "concept": "Contractual notice"
        },
        {
            "type": "Data Protection",
            "keywords": ["personal data", "sensitive personal data", "data protection", "privacy", "aadhaar"],
            "category": "Important review",
            "explanation": "Addresses handling of personal information collected under the agreement.",
            "concern": "Check alignment with the Digital Personal Data Protection Act, 2023 and IT Act security duties.",
            "concept": "Personal data processing"
        },
        {
            "type": "Assignment",
            "keywords": ["may assign", "assignment of this agreement", "transfer this agreement"],
            "category": "Review",
            "explanation": "Controls whether a party may transfer rights or duties to someone else.",
            "concern": "One-sided assignment rights can change who actually performs the contract.",
            "concept": "Assignment of contractual rights"
        },
        {
            "type": "Amendment",
            "keywords": ["amendment", "modified in writing", "variation of this agreement"],
            "category": "Informational",
            "explanation": "States how the contract can be changed after signing.",
            "concern": "Oral variations may still arise; confirm the written-amendment process is followed.",
            "concept": "Contract variation"
        },
        {
            "type": "Warranty",
            "keywords": ["represents and warrants", "warranty", "representation"],
            "category": "Review",
            "explanation": "Contains statements of fact or quality promises that may support later claims.",
            "concern": "Broad warranties can expand liability beyond the commercial bargain.",
            "concept": "Representations and warranties"
        },
        {
            "type": "Non-Compete & Restraint of Trade",
            "keywords": ["non-compete", "competing business", "competing enterprise", "restraint of trade", "restrictive covenant"],
            "category": "Professional review recommended",
            "explanation": "Restricts engaging in or providing services to similar or competing businesses.",
            "concern": "Under Section 27 of the Indian Contract Act, agreements restraining lawful profession or business post-termination are generally void.",
            "concept": "Restraint of Trade (Section 27)"
        },
        {
            "type": "Non-Solicitation",
            "keywords": ["non-solicit", "not solicit", "entice away", "hire any employee", "soliciting clients"],
            "category": "Review",
            "explanation": "Bars actively hiring former colleagues or pitching services directly to existing clients.",
            "concern": "Ensure the definition of restricted employees and client solicitation is not disproportionately broad.",
            "concept": "Non-Solicitation Covenants"
        },
        {
            "type": "Liquidated Damages & Penalties",
            "keywords": ["liquidated damages", "penalty payment", "penalty for early exit", "minimum commitment", "stipulated sum"],
            "category": "Important review",
            "explanation": "Pre-estimates monetary damages to be paid in the event of contractual delay or early departure.",
            "concern": "Under Section 74 of the Indian Contract Act, Indian courts award reasonable compensation only, not punitive penalties.",
            "concept": "Liquidated Damages (Section 74)"
        },
        {
            "type": "Termination",
            "keywords": ["either party may terminate", "terminate this agreement", "right to terminate", "notice of termination", "immediate termination"],
            "category": "Important review",
            "explanation": "Specifies when and how either party may end the agreement, including required notice periods.",
            "concern": "Check if notice periods are one-sided or unusually short, and whether termination can occur without cause.",
            "concept": "Contract Termination & Notice"
        },
        {
            "type": "Indemnification & Liability",
            "keywords": ["indemnify", "indemnity", "hold harmless", "defend and hold harmless", "limitation of liability"],
            "category": "Important review",
            "explanation": "Requires one party to compensate the other for specified losses, legal claims, or damages.",
            "concern": "Check whether indemnity obligations are uncapped, one-sided, or encompass third-party claims.",
            "concept": "Indemnification & Damage Liability"
        },
        {
            "type": "Confidentiality & Non-Disclosure",
            "keywords": ["confidential information", "proprietary information", "non-disclosure", "secrecy"],
            "category": "Informational",
            "explanation": "Protects sensitive trade secrets, business data, and software source code from disclosure.",
            "concern": "Check the duration of confidentiality surviving termination and exclusions for publicly known data.",
            "concept": "Confidentiality"
        },
        {
            "type": "Intellectual Property & Work for Hire",
            "keywords": ["intellectual property", "work made for hire", "assignment of rights", "copyright", "patent", "moral rights"],
            "category": "Important review",
            "explanation": "Assigns ownership of created software, designs, or deliverables to the receiving party.",
            "concern": "Clarify whether pre-existing intellectual property and open-source components are retained.",
            "concept": "IP Assignment"
        },
        {
            "type": "Dispute Resolution & Arbitration",
            "keywords": ["arbitration", "dispute resolution", "arbitrator", "arbitration and conciliation act", "amicable settlement"],
            "category": "Review",
            "explanation": "Mandates private arbitration over court litigation to resolve contractual disagreements.",
            "concern": "Confirm the seat of arbitration, governing rules, and who bears initial arbitration costs.",
            "concept": "Arbitration Agreement (Section 7)"
        },
        {
            "type": "Governing Law & Jurisdiction",
            "keywords": ["governing law", "jurisdiction", "exclusive jurisdiction", "courts of", "laws of india"],
            "category": "Informational",
            "explanation": "Designates which state's courts and national legal statutes govern the interpretation of the contract.",
            "concern": "Confirm that the designated court venue is geographically practical.",
            "concept": "Territorial Jurisdiction"
        },
        {
            "type": "Liquidated Damages & Penalties",
            "keywords": ["liquidated damages", "penalty", "forfeit", "compensation for delay", "stipulated sum"],
            "category": "Important review",
            "explanation": "Pre-estimates monetary damages to be paid in the event of contractual delay or default.",
            "concern": "Under Section 74 of the Indian Contract Act, Indian courts award reasonable compensation only, not punitive windfalls.",
            "concept": "Liquidated Damages (Section 74)"
        },
        {
            "type": "Force Majeure",
            "keywords": ["force majeure", "act of god", "unforeseen events", "pandemic", "war", "beyond reasonable control"],
            "category": "Informational",
            "explanation": "Suspends contractual obligations when unforeseen external events prevent performance.",
            "concern": "Check whether prompt written notice is required to claim force majeure relief.",
            "concept": "Frustration & Force Majeure"
        }
    ]

    async def process(self, context: Dict[str, Any]) -> Dict[str, Any]:
        """
        Analyze document pages or text chunks and extract classified clauses.
        context: {"pages": List[Dict[str, Any]]}
        """
        pages = context.get("pages", [])
        detected_clauses = []
        clause_id_counter = 1

        for page in pages:
            page_num = page.get("page_number", 1)
            text = page.get("text", "")
            if not text.strip():
                continue

            # Split text by paragraphs or double newlines
            paragraphs = [p.strip() for p in text.split("\n\n") if len(p.strip()) > 30]

            for para in paragraphs:
                para_lower = para.lower()

                for defn in self.CLAUSE_DEFINITIONS:
                    if any(kw in para_lower for kw in defn["keywords"]):
                        # Extract section reference if present at start of paragraph
                        sec_match = re.match(r'^(?:(?:Section|Clause|Article)\s+[0-9IVXLCDM\.]+|[0-9]{1,2}\.[0-9]{0,2})', para, re.IGNORECASE)
                        sec_ref = sec_match.group(0) if sec_match else None

                        # Extract potential obligation statement
                        obligation = None
                        if "shall" in para_lower or "agrees to" in para_lower or "must" in para_lower:
                            obligation = "Creates a binding performance duty or contractual restriction."

                        detected_clauses.append({
                            "id": f"clause_{clause_id_counter}",
                            "clause_type": defn["type"],
                            "original_text": para,
                            "page_number": page_num,
                            "section_reference": sec_ref,
                            "plain_explanation": defn["explanation"],
                            "obligation": obligation,
                            "attention_category": defn["category"],
                            "potential_concern": defn["concern"],
                            "relevant_legal_concept": defn["concept"],
                            "suggested_verification": f"Cross-reference with Indian statutory provisions on {defn['concept']}.",
                            "confidence": 0.94
                        })
                        clause_id_counter += 1
                        break  # Match highest priority clause definition for this paragraph

        # Calculate metrics
        total_clauses = len(detected_clauses)
        obligations_count = sum(1 for c in detected_clauses if c.get("obligation"))
        review_items_count = sum(1 for c in detected_clauses if c.get("attention_category") in ["Review", "Important review", "Professional review recommended"])

        return {
            "clauses": detected_clauses,
            "total_clauses": total_clauses,
            "obligations_count": obligations_count,
            "review_items_count": review_items_count
        }
