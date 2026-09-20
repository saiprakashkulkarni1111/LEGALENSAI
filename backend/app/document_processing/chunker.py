"""
LEGALENS AI - Semantic Legal Chunker
Chunks documents preserving page boundaries, section numbers, and paragraph structure.
"""
import re
from typing import List, Dict, Any


class SemanticLegalChunker:
    # Patterns for legal sections (e.g., "1. Term", "Clause 4", "Section 12", "ARTICLE III")
    SECTION_HEADER_PATTERN = re.compile(
        r'^(?:(?:Section|Clause|Article|Paragraph)\s+[0-9IVXLCDM\.]+|[0-9]{1,2}\.[0-9]{0,2}\s+[A-Z][A-Za-z\s]+)',
        re.MULTILINE | re.IGNORECASE
    )

    @classmethod
    def chunk_pages(cls, pages: List[Dict[str, Any]], target_chunk_size: int = 800) -> List[Dict[str, Any]]:
        """
        Takes parsed pages and breaks them into structured semantic chunks with page provenance.
        """
        chunks = []
        chunk_idx = 0

        for page in pages:
            page_num = page.get("page_number", 1)
            raw_text = page.get("text", "")
            if not raw_text.strip():
                continue

            # Split paragraphs by double newline
            paragraphs = [p.strip() for p in raw_text.split("\n\n") if p.strip()]

            current_chunk = ""
            current_section = "General Provision"

            for para in paragraphs:
                # Check if paragraph begins with a section header
                header_match = cls.SECTION_HEADER_PATTERN.match(para)
                if header_match:
                    current_section = header_match.group(0).strip()

                if len(current_chunk) + len(para) > target_chunk_size and current_chunk:
                    chunks.append({
                        "chunk_index": chunk_idx,
                        "page_number": page_num,
                        "section_header": current_section,
                        "content": current_chunk.strip()
                    })
                    chunk_idx += 1
                    current_chunk = para + "\n\n"
                else:
                    current_chunk += para + "\n\n"

            if current_chunk.strip():
                chunks.append({
                    "chunk_index": chunk_idx,
                    "page_number": page_num,
                    "section_header": current_section,
                    "content": current_chunk.strip()
                })
                chunk_idx += 1

        return chunks
