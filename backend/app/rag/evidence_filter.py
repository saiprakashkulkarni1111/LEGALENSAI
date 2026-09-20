"""
LEGALENS AI - Evidence Filter
Ensures every candidate evidence snippet meets strict authenticity and provenance standards.
"""
from typing import List, Dict, Any


class EvidenceFilter:
    MIN_RELEVANCE_THRESHOLD = 0.5

    @classmethod
    def filter_evidence(cls, candidates: List[Dict[str, Any]]) -> List[Dict[str, Any]]:
        valid = []
        for item in candidates:
            content = item.get("content") or item.get("relevant_excerpt") or ""
            if not content or len(content.strip()) < 20:
                continue

            # Must have verifiable official source or valid provenance
            if not item.get("official_url") and not item.get("source_id"):
                continue

            score = item.get("final_rank_score", item.get("relevance_score", 0.0))
            if score >= cls.MIN_RELEVANCE_THRESHOLD:
                valid.append(item)

        return valid
