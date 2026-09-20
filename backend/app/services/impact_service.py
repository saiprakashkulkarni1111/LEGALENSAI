"""Document-to-law impact: map clauses to potentially relevant official provisions."""
from typing import Any, Dict, List
from backend.app.connectors.registry import source_registry


class DocumentToLawImpactEngine:
    CONCEPT_QUERIES = {
        "Termination": "termination notice Indian Contract Act",
        "Non-Compete & Restraint of Trade": "Section 27 restraint of trade",
        "Liquidated Damages & Penalties": "Section 74 liquidated damages penalty",
        "Dispute Resolution & Arbitration": "Section 7 arbitration agreement",
        "Intellectual Property & Work for Hire": "intellectual property assignment",
        "Confidentiality & Non-Disclosure": "confidential information",
        "Governing Law & Jurisdiction": "jurisdiction courts India",
        "Payment": "compensation breach of contract Section 73",
        "Data Protection": "Digital Personal Data Protection Act consent",
    }

    async def analyze(self, clauses: List[Dict[str, Any]]) -> List[Dict[str, Any]]:
        impacts: List[Dict[str, Any]] = []
        seen = set()
        for clause in clauses:
            clause_type = clause.get("clause_type", "")
            query = self.CONCEPT_QUERIES.get(clause_type) or clause.get("relevant_legal_concept") or clause_type
            if not query or query in seen:
                continue
            seen.add(query)
            hits = await source_registry.search_all(query=str(query), source_ids=["india_code", "supreme_court"])
            verified = [
                h for h in hits
                if h.get("official_url") and (h.get("content") or h.get("relevant_excerpt"))
            ]
            if not verified:
                impacts.append({
                    "clause_type": clause_type,
                    "clause_id": clause.get("id"),
                    "status": "unable_to_verify",
                    "message": "Unable to verify this claim from the available authoritative sources.",
                    "attention": "Potential issue requiring review.",
                })
                continue
            top = verified[0]
            impacts.append({
                "clause_type": clause_type,
                "clause_id": clause.get("id"),
                "status": "potential_update_relevant",
                "headline": "Potential legal update relevant to this clause.",
                "attention": "Potential issue requiring review.",
                "source_title": top.get("title") or top.get("case_title"),
                "section": top.get("section_number") or top.get("citation"),
                "excerpt": (top.get("content") or top.get("relevant_excerpt") or "")[:600],
                "official_url": top.get("official_url"),
                "authority": top.get("authority"),
                "authority_tier": top.get("authority_tier", "TIER 1"),
                "never_conclude": "This system does not determine that a contract is illegal.",
            })
        return impacts


impact_engine = DocumentToLawImpactEngine()
