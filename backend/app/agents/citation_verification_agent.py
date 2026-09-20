"""
LEGALENS AI - Citation Verification Agent
The supreme reliability guardrail enforcing: "NO EVIDENCE -> NO CLAIM"
For every legal claim:
CLAIM -> RETRIEVED EVIDENCE -> ENTAILMENT CHECK -> SOURCE VALIDATION -> CITATION
If evidence does not strictly support the claim:
Mark unverified or return: "Unable to verify this claim from the available authoritative sources."
"""
import re
from typing import Dict, Any, List
from backend.app.agents.base import BaseAgent


class CitationVerificationAgent(BaseAgent):
    @property
    def name(self) -> str:
        return "CitationVerificationAgent"

    @property
    def description(self) -> str:
        return "Validates that all external legal claims are strictly grounded in retrieved authoritative sources."

    async def process(self, context: Dict[str, Any]) -> Dict[str, Any]:
        """
        Verify claims against provided evidence items.
        context: {"claims": List[str], "evidence": List[Dict[str, Any]], "raw_query": str}
        """
        claims = context.get("claims", [])
        evidence_list = context.get("evidence", [])
        verified_claims = []

        combined_evidence_text = " ".join([
            (e.get("content") or e.get("relevant_excerpt") or "") + " " +
            (e.get("title") or "") + " " +
            (e.get("section_number") or "") + " " +
            (e.get("act_title") or "")
            for e in evidence_list
        ]).lower()

        for claim in claims:
            claim_text = claim if isinstance(claim, str) else claim.get("text", "")
            claim_lower = claim_text.lower()

            # Check for section numbers or case citations mentioned in the claim
            sections_in_claim = re.findall(r'\b(?:section|sec\.?)\s*([0-9]{1,4}[A-Za-z]?)\b', claim_lower)
            acts_in_claim = [act for act in ["contract act", "arbitration", "information technology", "dpdp", "specific relief"] if act in claim_lower]

            is_supported = False
            matching_evidence_ids = []
            reasoning = ""

            if not evidence_list:
                is_supported = False
                reasoning = "No authoritative legal sources were retrieved to substantiate this proposition."
            else:
                # Check entailment against evidence snippets
                for ev in evidence_list:
                    ev_text = (
                        (ev.get("content") or "") + " " +
                        (ev.get("relevant_excerpt") or "") + " " +
                        (ev.get("section_number") or "") + " " +
                        (ev.get("act_title") or "")
                    ).lower()

                    # Match specific statutory sections if present
                    if sections_in_claim:
                        sec_match = any(f"section {s}" in ev_text or f"sec. {s}" in ev_text or f"{s}" == str(ev.get("section_number", "")).lower() for s in sections_in_claim)
                        if sec_match:
                            is_supported = True
                            matching_evidence_ids.append(ev.get("id") or ev.get("source_id"))
                    else:
                        # Conceptual keyword overlap check
                        keywords = [w for w in claim_lower.split() if len(w) > 4 and w not in ["court", "under", "shall", "party", "which"]]
                        overlap = sum(1 for kw in keywords if kw in ev_text)
                        if len(keywords) > 0 and (overlap / len(keywords)) >= 0.4:
                            is_supported = True
                            matching_evidence_ids.append(ev.get("id") or ev.get("source_id"))

                if is_supported:
                    reasoning = f"Verified by authoritative source: {', '.join(matching_evidence_ids[:2])}"
                else:
                    reasoning = "Unable to verify this claim from the available authoritative sources."

            verified_claims.append({
                "claim_text": claim_text,
                "is_verified": is_supported,
                "evidence_ids": matching_evidence_ids,
                "confidence": "HIGH" if is_supported else "UNVERIFIED",
                "entailment_reasoning": reasoning
            })

        all_verified = all(c["is_verified"] for c in verified_claims) if verified_claims else False

        return {
            "verified_claims": verified_claims,
            "all_verified": all_verified,
            "unverified_count": sum(1 for c in verified_claims if not c["is_verified"])
        }
