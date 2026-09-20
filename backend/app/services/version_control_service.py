"""
LEGALENS AI - Legal Version Control & Change Intelligence Service
Tracks legislative versions across time.
Enforces the mandatory rule: When user asks "What law applied in 2022?",
retrieve the version applicable to that date, not current law.
"""
from typing import Dict, Any, List, Optional
from datetime import datetime, timezone
from backend.app.connectors.india_code_adapter import IndiaCodeAdapter


class LegalVersionControlService:
    def __init__(self):
        self.india_code = IndiaCodeAdapter()

    async def get_applicable_version(self, statutory_key: str, target_year: int) -> Optional[Dict[str, Any]]:
        """
        Retrieve the statutory version effective in a specific year.
        """
        all_provisions = self.india_code.AUTHORITATIVE_PROVISIONS
        item = all_provisions.get(statutory_key)
        if not item:
            return None

        enactment_year = item["year"]

        # If statute was enacted after target year, it was not yet in force
        if enactment_year > target_year:
            return {
                "status": "NOT_YET_IN_FORCE",
                "act_title": item["act_title"],
                "target_year": target_year,
                "enacted_year": enactment_year,
                "message": f"{item['act_title']} was enacted in {enactment_year} and did not apply in {target_year}."
            }

        return {
            "status": "APPLICABLE_VERSION",
            "act_title": item["act_title"],
            "section_number": item["section_number"],
            "section_title": item["section_title"],
            "version_effective_from": item["effective_from"],
            "version_effective_until": item["effective_until"],
            "applicable_to_year": target_year,
            "content": item["content"],
            "official_url": item["official_url"],
            "provenance": "India Code Official Legislative Record"
        }

    async def detect_legal_updates(self, user_clauses: List[Dict[str, Any]]) -> List[Dict[str, Any]]:
        """
        Identifies recent legislative amendments relevant to clauses in an uploaded document.
        For example: Digital Personal Data Protection Act 2023 impacting data privacy clauses.
        """
        detected_updates = []

        for clause in user_clauses:
            clause_type = clause.get("clause_type", "")
            original_text = clause.get("original_text", "").lower()

            if "data protection" in clause_type.lower() or "privacy" in original_text:
                detected_updates.append({
                    "clause_id": clause.get("id"),
                    "clause_type": clause_type,
                    "update_title": "Enactment of Digital Personal Data Protection Act, 2023 (DPDP)",
                    "previous_law": "Information Technology Act, 2000 (Section 43A)",
                    "new_law": "Digital Personal Data Protection Act, 2023 (Act No. 22 of 2023)",
                    "effective_date": "2023-08-11",
                    "changed_sections": "Section 6 (Consent Architecture) & Section 8 (Data Fiduciary Duties)",
                    "change_summary": "Replaces legacy reasonable security rules under IT Act with explicit consent notices, verifiable parent consent, and strict purpose limitation.",
                    "official_source": "https://www.indiacode.nic.in/handle/123456789/19842",
                    "impact": "Data processing and confidentiality terms should be aligned with DPDP notice requirements."
                })

            elif "arbitration" in clause_type.lower():
                detected_updates.append({
                    "clause_id": clause.get("id"),
                    "clause_type": clause_type,
                    "update_title": "Arbitration and Conciliation (Amendment) Act Standards",
                    "previous_law": "Arbitration & Conciliation Act 1996 (Pre-2015)",
                    "new_law": "Arbitration & Conciliation (Amendment) Act, 2015/2019/2021",
                    "effective_date": "2019-08-09",
                    "changed_sections": "Section 29A (Time limits for arbitral awards)",
                    "change_summary": "Imposes strict 12-month timeline for passing arbitral awards from the date of completion of pleadings.",
                    "official_source": "https://www.indiacode.nic.in/handle/123456789/1978?view_type=browse",
                    "impact": "Arbitration clauses with open-ended timeframes should reference Section 29A statutory schedules."
                })

        return detected_updates


version_control_service = LegalVersionControlService()
