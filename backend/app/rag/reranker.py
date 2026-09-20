"""
LEGALENS AI - Legal Evidence Reranker
Reranks candidate evidence based on:
1. Authority Tier (Tier 1 Government / Court > Secondary)
2. Exact statutory section match
3. Temporal applicability (Current vs. Historical requested year)
4. Semantic and keyword overlap
"""
import re
from typing import List, Dict, Any


class LegalReranker:
    SECTION_REGEX = re.compile(r'\b(?:section|sec\.?|clause|art\.?)\s*([0-9]{1,4}[A-Za-z]?)\b', re.IGNORECASE)

    @classmethod
    def rerank(cls, query: str, candidates: List[Dict[str, Any]], target_year: int = None) -> List[Dict[str, Any]]:
        query_lower = query.lower()
        query_sections = cls.SECTION_REGEX.findall(query_lower)

        for item in candidates:
            score = float(item.get("relevance_score", 1.0))

            # Tier 1 Authority Boost
            if item.get("authority_tier") == "TIER 1":
                score *= 1.4

            # Exact section match boost
            sec_num = str(item.get("section_number", "")).lower().replace("section", "").strip()
            if sec_num and sec_num in query_sections:
                score *= 2.0

            # Title or Act name match boost
            act_title = str(item.get("act_title", "")).lower()
            if act_title and any(w in act_title for w in query_lower.split() if len(w) > 4):
                score *= 1.3

            # Temporal discount if candidate is clearly outside the requested historical year
            item_year = item.get("year")
            if target_year and item_year:
                if item_year > target_year:
                    score *= 0.2  # Law enacted after requested historical date

            item["final_rank_score"] = round(score, 3)

        candidates.sort(key=lambda x: x.get("final_rank_score", 0.0), reverse=True)
        return candidates
