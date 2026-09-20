"""
LEGALENS AI - Contract Comparison Agent
Performs clause-by-clause diffing between Document A and Document B.
Detects added, removed, modified clauses and numerical deltas (e.g., notice days).
Adheres strictly to neutral observations without subjective bias.
"""
import re
from typing import Dict, Any, List
from backend.app.agents.base import BaseAgent


class ComparisonAgent(BaseAgent):
    @property
    def name(self) -> str:
        return "ComparisonAgent"

    @property
    def description(self) -> str:
        return "Compares two legal agreements, calculating clause deltas and structural modifications."

    DAYS_PATTERN = re.compile(r'\b([0-9]{1,3})\s*(?:calendar\s+|business\s+)?days?\b', re.IGNORECASE)

    async def process(self, context: Dict[str, Any]) -> Dict[str, Any]:
        """
        Compare clauses of Document A and Document B.
        context: {
            "doc_a_title": str,
            "doc_b_title": str,
            "doc_a_clauses": List[Dict[str, Any]],
            "doc_b_clauses": List[Dict[str, Any]]
        }
        """
        doc_a_title = context.get("doc_a_title", "Document A")
        doc_b_title = context.get("doc_b_title", "Document B")
        clauses_a = context.get("doc_a_clauses", [])
        clauses_b = context.get("doc_b_clauses", [])

        # Group by clause_type
        a_by_type = {c["clause_type"]: c for c in clauses_a}
        b_by_type = {c["clause_type"]: c for c in clauses_b}

        all_types = sorted(list(set(list(a_by_type.keys()) + list(b_by_type.keys()))))
        diffs = []
        added_count = 0
        removed_count = 0
        modified_count = 0
        neutral_observations = []

        for c_type in all_types:
            in_a = c_type in a_by_type
            in_b = c_type in b_by_type

            if in_a and not in_b:
                # Removed in B
                removed_count += 1
                diffs.append({
                    "clause_type": c_type,
                    "change_type": "REMOVED",
                    "doc_a_text": a_by_type[c_type].get("original_text"),
                    "doc_b_text": None,
                    "doc_a_page": a_by_type[c_type].get("page_number", 1),
                    "doc_b_page": None,
                    "delta_summary": f"{c_type} was present in {doc_a_title} but omitted entirely in {doc_b_title}.",
                    "potential_significance": f"The obligations and protections governing {c_type.lower()} no longer apply in the newer draft."
                })
                neutral_observations.append(f"Omission of {c_type} in {doc_b_title}.")

            elif not in_a and in_b:
                # Added in B
                added_count += 1
                diffs.append({
                    "clause_type": c_type,
                    "change_type": "ADDED",
                    "doc_a_text": None,
                    "doc_b_text": b_by_type[c_type].get("original_text"),
                    "doc_a_page": None,
                    "doc_b_page": b_by_type[c_type].get("page_number", 1),
                    "delta_summary": f"{c_type} has been newly introduced in {doc_b_title}.",
                    "potential_significance": f"Introduces new terms or duties regarding {c_type.lower()} not contemplated in {doc_a_title}."
                })
                neutral_observations.append(f"Addition of {c_type} in {doc_b_title}.")

            else:
                # Present in both, check for text/numerical divergence
                text_a = a_by_type[c_type].get("original_text", "")
                text_b = b_by_type[c_type].get("original_text", "")

                if text_a.strip().lower() != text_b.strip().lower():
                    modified_count += 1

                    # Check for day count differences (e.g. 30 days vs 7 days)
                    days_a = self.DAYS_PATTERN.findall(text_a)
                    days_b = self.DAYS_PATTERN.findall(text_b)
                    delta_desc = f"Wording modified in {c_type}."
                    sig_desc = f"Terms governing {c_type.lower()} have been altered between versions."

                    if days_a and days_b:
                        num_a, num_b = int(days_a[0]), int(days_b[0])
                        diff_days = abs(num_a - num_b)
                        if num_b < num_a:
                            delta_desc = f"Timeline reduced by {diff_days} days ({num_a} days in {doc_a_title} vs. {num_b} days in {doc_b_title})."
                            sig_desc = "The operational window to respond or act before this clause takes effect has been shortened."
                        elif num_b > num_a:
                            delta_desc = f"Timeline increased by {diff_days} days ({num_a} days in {doc_a_title} vs. {num_b} days in {doc_b_title})."
                            sig_desc = "The operational window to respond or act has been lengthened."

                    diffs.append({
                        "clause_type": c_type,
                        "change_type": "MODIFIED",
                        "doc_a_text": text_a,
                        "doc_b_text": text_b,
                        "doc_a_page": a_by_type[c_type].get("page_number", 1),
                        "doc_b_page": b_by_type[c_type].get("page_number", 1),
                        "delta_summary": delta_desc,
                        "potential_significance": sig_desc
                    })
                    neutral_observations.append(delta_desc)

        return {
            "doc_a_title": doc_a_title,
            "doc_b_title": doc_b_title,
            "total_differences": len(diffs),
            "added_count": added_count,
            "removed_count": removed_count,
            "modified_count": modified_count,
            "diffs": diffs,
            "neutral_observations": neutral_observations
        }
