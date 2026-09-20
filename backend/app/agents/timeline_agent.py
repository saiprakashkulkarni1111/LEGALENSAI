"""
LEGALENS AI - Timeline Extraction Agent
Extracts contractual dates, notice windows, payment schedules, and renewal milestones.
"""
import re
from typing import Dict, Any, List
from backend.app.agents.base import BaseAgent


class TimelineAgent(BaseAgent):
    @property
    def name(self) -> str:
        return "TimelineAgent"

    @property
    def description(self) -> str:
        return "Detects temporal milestones, notice deadlines, payment intervals, and agreement durations."

    DATE_PATTERNS = [
        # Explicit date: 15th January 2026, September 20, 2026, 01/04/2026
        re.compile(r'\b(?:[0-3]?[0-9](?:st|nd|rd|th)?\s+(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)[a-z]*[\s,]+[12][0-9]{3})\b', re.IGNORECASE),
        re.compile(r'\b(?:(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)[a-z]*\s+[0-3]?[0-9][\s,]+[12][0-9]{3})\b', re.IGNORECASE),
        re.compile(r'\b[0-3]?[0-9][\/\-\.][0-1]?[0-9][\/\-\.][12][0-9]{3}\b'),
        # Interval/Deadline patterns: "within 30 days", "60 days notice", "12 months from"
        re.compile(r'\b(?:within\s+[0-9]{1,3}\s+(?:days|calendar days|business days|months))\b', re.IGNORECASE),
        re.compile(r'\b(?:[0-9]{1,3}\s+(?:days|months)\s+(?:prior\s+)?written\s+notice)\b', re.IGNORECASE)
    ]

    async def process(self, context: Dict[str, Any]) -> Dict[str, Any]:
        """
        Extract timeline events from document pages and identified clauses.
        context: {"pages": List[Dict[str, Any]], "clauses": List[Dict[str, Any]]}
        """
        pages = context.get("pages", [])
        events = []
        event_counter = 1

        for page in pages:
            page_num = page.get("page_number", 1)
            text = page.get("text", "")
            if not text.strip():
                continue

            for pattern in self.DATE_PATTERNS:
                matches = pattern.finditer(text)
                for m in matches:
                    date_snippet = m.group(0)
                    start_pos = max(0, m.start() - 60)
                    end_pos = min(len(text), m.end() + 100)
                    context_snippet = text[start_pos:end_pos].replace("\n", " ").strip()

                    # Classify event type
                    lower_ctx = context_snippet.lower()
                    if "terminate" in lower_ctx or "notice" in lower_ctx:
                        ev_type = "Notice / Termination"
                    elif "payment" in lower_ctx or "invoice" in lower_ctx or "fee" in lower_ctx:
                        ev_type = "Payment Schedule"
                    elif "effective" in lower_ctx or "commence" in lower_ctx or "entered into" in lower_ctx:
                        ev_type = "Effective Date"
                    elif "expire" in lower_ctx or "term of" in lower_ctx:
                        ev_type = "Agreement Expiry"
                    else:
                        ev_type = "Contractual Milestone"

                    events.append({
                        "id": f"event_{event_counter}",
                        "date_str": date_snippet,
                        "event_description": f"{ev_type}: {context_snippet[:150]}...",
                        "event_type": ev_type,
                        "page_number": page_num,
                        "confidence": 0.95,
                        "is_manual_override": False
                    })
                    event_counter += 1

        # Deduplicate near-identical dates on same page
        seen_keys = set()
        deduped = []
        for ev in events:
            key = (ev["date_str"].lower(), ev["page_number"], ev["event_type"])
            if key not in seen_keys:
                seen_keys.add(key)
                deduped.append(ev)

        return {
            "events": deduped,
            "total_events": len(deduped),
            "has_critical_deadlines": any(e["event_type"] in ["Notice / Termination", "Payment Schedule"] for e in deduped)
        }
