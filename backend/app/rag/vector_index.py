"""
LEGALENS AI - Vector Index Engine
Zero-dependency in-memory & database vector index for semantic legal chunk matching.
"""
from typing import List, Dict, Any, Tuple
import numpy as np
from backend.app.providers.embedding_provider import embedding_provider


class LegalVectorIndex:
    def __init__(self):
        # In-memory document & statutory chunk registry
        self.chunk_ids: List[str] = []
        self.vectors: List[np.ndarray] = []
        self.metadata: List[Dict[str, Any]] = []

    def clear(self):
        self.chunk_ids.clear()
        self.vectors.clear()
        self.metadata.clear()

    def add_item(self, item_id: str, text: str, meta: Dict[str, Any]):
        vec = embedding_provider.get_embedding(text)
        self.chunk_ids.append(item_id)
        self.vectors.append(np.array(vec, dtype=np.float32))
        self.metadata.append(meta)

    def search(self, query: str, top_k: int = 5) -> List[Tuple[Dict[str, Any], float]]:
        """Search top-k chunks by cosine similarity."""
        if not self.vectors:
            return []

        query_vec = np.array(embedding_provider.get_embedding(query), dtype=np.float32)
        matrix = np.vstack(self.vectors)

        # Cosine similarity matrix multiplication
        dots = np.dot(matrix, query_vec)
        norms = np.linalg.norm(matrix, axis=1) * np.linalg.norm(query_vec)
        norms[norms == 0] = 1e-9
        similarities = dots / norms

        top_indices = np.argsort(similarities)[::-1][:top_k]
        results = []
        for idx in top_indices:
            score = float(similarities[idx])
            results.append((self.metadata[idx], score))

        return results


global_vector_index = LegalVectorIndex()
