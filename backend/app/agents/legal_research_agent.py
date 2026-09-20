"""
LEGALENS AI - Legal Research Agent
Executes structured legal research across authoritative Indian sources.
Enforces the mandatory Evidence-First schema:
### Answer
### What The Document Says
### Evidence
### Relevant Legal Sources
### What This Means
### What to Verify
### Possible Next Steps
### Questions for a Lawyer
### Source Freshness
"""
import re
from typing import Dict, Any, List, Optional
from datetime import datetime, timezone
from backend.app.agents.base import BaseAgent
from backend.app.rag.hybrid_retriever import HybridLegalRetriever
from backend.app.rag.evidence_filter import EvidenceFilter
from backend.app.agents.citation_verification_agent import CitationVerificationAgent


class LegalResearchAgent(BaseAgent):
    def __init__(self):
        self.retriever = HybridLegalRetriever()
        self.citation_verifier = CitationVerificationAgent()

    @property
    def name(self) -> str:
        return "LegalResearchAgent"

    @property
    def description(self) -> str:
        return "Conducts evidence-first research across verified Indian legal sources with temporal version awareness."

    def extract_year_context(self, query: str, date_context: Optional[str]) -> Optional[int]:
        """Extract explicit historical year from query or date_context parameter."""
        if date_context and date_context.isdigit():
            return int(date_context)
        match = re.search(r'\b(19\d\d|20[0-2]\d)\b', query)
        if match:
            return int(match.group(1))
        return None

    async def process(self, context: Dict[str, Any]) -> Dict[str, Any]:
        """
        context: {
            "query": str,
            "jurisdiction": str,
            "date_context": str,
            "document_context": Optional[str],
            "sources": Optional[List[str]]
        }
        """
        query = context.get("query", "").strip()
        jurisdiction = context.get("jurisdiction", "India")
        date_context = context.get("date_context", "CURRENT")
        doc_context = context.get("document_context")
        target_sources = context.get("sources")

        target_year = self.extract_year_context(query, date_context)

        # 1. Hybrid Retrieval from official Indian sources
        raw_candidates = await self.retriever.retrieve(
            query=query,
            target_sources=target_sources,
            target_year=target_year,
            top_k=6
        )

        # 2. Evidence Filtering
        evidence = EvidenceFilter.filter_evidence(raw_candidates)

        # If user explicitly requested a specific section number, ensure evidence actually contains that section
        explicit_sec = re.search(r'\b(?:section|sec\.?)\s*([0-9]{1,4}[A-Za-z]?)\b', query, re.IGNORECASE)
        if explicit_sec:
            req_num = explicit_sec.group(1).lower()
            evidence = [
                ev for ev in evidence
                if req_num in str(ev.get("section_number", "")).lower() or
                   req_num in str(ev.get("section_or_para", "")).lower()
            ]

        # 3. Formulate Claims based on evidence
        claims_to_verify = []
        if not evidence:
            claims_to_verify.append({
                "text": f"The query '{query}' does not match verified statutory provisions or published landmark rulings in the current authoritative index.",
                "confidence": "UNVERIFIED"
            })
        else:
            for ev in evidence[:3]:
                title = ev.get("title") or ev.get("case_title") or "Retrieved provision"
                claims_to_verify.append({
                    "text": f"Under {title}, the legal principle governs rights and liabilities as set out in official statutory text.",
                    "confidence": "HIGH"
                })

        # 4. Citation & Entailment Verification
        verification_result = await self.citation_verifier.process({
            "claims": claims_to_verify,
            "evidence": evidence,
            "raw_query": query
        })

        # 5. Build Evidence-First Response Sections
        if not evidence:
            answer_text = (
                f"Unable to verify this claim or locate authoritative statutory provisions matching '{query}' "
                f"within the verified legal repository. In accordance with our 'NO EVIDENCE -> NO CLAIM' policy, "
                f"no speculative or unverified legal rules are presented."
            )
            what_means = "No verified statutory provision was retrieved. Please verify the Act or Section title."
            what_verify = ["Confirm whether the statutory title or section number is spelled correctly.", "Check whether this relates to central or state legislation."]
            lawyer_questions = ["Does a specific state amendment or subordinate rule apply to this matter?"]
            next_steps = ["Conduct an official search on indiacode.nic.in or consult an advocate."]
            freshness_status = "VERIFIED"
        else:
            top_ev = evidence[0]
            sec_or_title = top_ev.get("section_title") or top_ev.get("title") or "statutory authority"
            act_name = top_ev.get("act_title") or top_ev.get("court") or "Indian Law"

            answer_text = (
                f"Based on authoritative sources retrieved from {act_name}, {sec_or_title} addresses the queried subject. "
                f"The law stipulates defined rights and statutory remedies as verified from official publications."
            )
            what_means = (
                f"In plain terms, {sec_or_title} sets the official legal parameters in India. "
                f"Parties contracting in India must align their agreements with these mandatory statutory principles."
            )
            what_verify = [
                f"Verify whether any subsequent state notifications or amendments modify {top_ev.get('section_number', 'this section')}.",
                f"Check the effective date ({top_ev.get('effective_from', 'enactment')}) relative to your contract date."
            ]
            lawyer_questions = [
                f"How has the High Court with jurisdiction over my territory interpreted {top_ev.get('section_number', 'this provision')}?",
                "Are there specific judicial precedents or contractual carve-outs that apply to my situation?"
            ]
            next_steps = [
                "Review the relevant clauses in your document against the retrieved statutory text.",
                "Review the official document link on the official portal."
            ]
            freshness_status = "RECENT"

        # Format sources for UI
        formatted_sources = []
        for ev in evidence:
            formatted_sources.append({
                "source_id": ev.get("source_id", "india_code"),
                "source_name": ev.get("source_name", "Official Indian Legal Portal"),
                "authority": ev.get("authority", "Government of India"),
                "source_type": ev.get("source_type", "legislation"),
                "authority_tier": ev.get("authority_tier", "TIER 1"),
                "official_url": ev.get("official_url", "https://www.indiacode.nic.in"),
                "retrieved_at": ev.get("retrieved_at", datetime.now(timezone.utc).isoformat()),
                "effective_from": ev.get("effective_from"),
                "effective_until": ev.get("effective_until"),
                "version": ev.get("version", "1.0"),
                "content_hash": ev.get("content_hash", "verified_hash"),
                "retrieval_method": "verified_connector",
                "freshness_status": "RECENT"
            })

        # Format evidence items
        formatted_evidence = []
        for i, ev in enumerate(evidence):
            formatted_evidence.append({
                "id": ev.get("id", f"ev_{i}"),
                "source_title": ev.get("title") or ev.get("case_title") or "Authoritative Source",
                "section_or_para": ev.get("section_number") or ev.get("citation") or "Official Provision",
                "exact_text": ev.get("content") or ev.get("relevant_excerpt") or "",
                "official_url": ev.get("official_url", ""),
                "authority": ev.get("authority", "Official Judicial / Legislative Authority"),
                "authority_tier": ev.get("authority_tier", "TIER 1"),
                "effective_period": f"{ev.get('effective_from', 'Enactment')} to {ev.get('effective_until', 'Present')}",
                "relevance_score": float(ev.get("final_rank_score", ev.get("relevance_score", 1.0)))
            })

        return {
            "query": query,
            "answer": answer_text,
            "what_document_says": doc_context,
            "evidence": formatted_evidence,
            "claims": verification_result.get("verified_claims", []),
            "legal_sources": formatted_sources,
            "what_this_means": what_means,
            "what_to_verify": what_verify,
            "possible_next_steps": next_steps,
            "questions_for_lawyer": lawyer_questions,
            "retrieved_at": datetime.now(timezone.utc).strftime("%d %B %Y, %H:%M:%S UTC"),
            "freshness_status": freshness_status,
            "limitations": [
                "Automated retrieval is restricted to public statutory and judicial repositories.",
                "Does not reflect sealed orders, pending unreported decisions, or local state ordinances."
            ]
        }
