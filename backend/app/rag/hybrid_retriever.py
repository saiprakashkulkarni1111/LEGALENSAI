"""
LEGALENS AI - Hybrid Legal Retriever
Combines:
1. Live / Verified Authoritative Source Search (India Code, Supreme Court, NJDG, High Courts)
2. Semantic Vector Similarity Search
3. Reranking and Filtering
"""
from typing import List, Dict, Any, Optional
from backend.app.connectors.registry import source_registry
from backend.app.rag.vector_index import global_vector_index
from backend.app.rag.reranker import LegalReranker


class HybridLegalRetriever:
    @staticmethod
    async def retrieve(
        query: str,
        target_sources: Optional[List[str]] = None,
        target_year: Optional[int] = None,
        top_k: int = 5
    ) -> List[Dict[str, Any]]:
        """
        Execute hybrid retrieval across authoritative sources and indexed vectors.
        """
        candidates = []

        # 1. Authoritative Source Registry Search (Tier 1 live/verified connectors)
        source_results = await source_registry.search_all(
            query=query,
            source_ids=target_sources,
            filters={"year": target_year} if target_year else None
        )
        candidates.extend(source_results)

        # 2. Vector Index Semantic Search (Document Chunks & Local Pre-indexed Provisions)
        vector_results = global_vector_index.search(query, top_k=top_k)
        for meta, score in vector_results:
            meta_copy = meta.copy()
            meta_copy["relevance_score"] = float(score) * 4.0
            candidates.append(meta_copy)

        # 3. Deduplicate by content hash or title
        seen_keys = set()
        deduped = []
        for c in candidates:
            key = c.get("id") or c.get("content_hash") or c.get("title")
            if key not in seen_keys:
                seen_keys.add(key)
                deduped.append(c)

        # 4. Apply Reranking
        ranked = LegalReranker.rerank(query, deduped, target_year=target_year)

        return ranked[:top_k]
