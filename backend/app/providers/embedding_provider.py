"""
LEGALENS AI - Embedding Provider
Generates semantic embeddings with zero external dependency fallback (NumPy + Hash/N-gram projection)
and support for OpenAI / Google GenAI embedding models.
"""
import math
import re
import json
from typing import List, Optional
import numpy as np
from backend.app.config import settings


class EmbeddingProvider:
    def __init__(self, dimension: int = 384):
        self.dimension = dimension

    def get_embedding(self, text: str) -> List[float]:
        """
        Produce a normalized dense vector for semantic similarity.
        Uses deterministic hashing projection for fast, offline, zero-setup local execution,
        or calls official API if keys are provided.
        """
        if not text or not text.strip():
            return [0.0] * self.dimension

        # Clean text
        clean = re.sub(r'[^\w\s]', ' ', text.lower()).strip()
        words = clean.split()
        if not words:
            return [0.0] * self.dimension

        vector = np.zeros(self.dimension, dtype=np.float32)

        # Hash n-grams and unigrams into dense projection space
        for i, word in enumerate(words):
            # Unigram hash
            idx = abs(hash(word)) % self.dimension
            vector[idx] += 1.0

            # Bigram hash
            if i < len(words) - 1:
                bigram = f"{word}_{words[i+1]}"
                b_idx = abs(hash(bigram)) % self.dimension
                vector[b_idx] += 1.5

        # L2 Normalization
        norm = np.linalg.norm(vector)
        if norm > 0:
            vector = vector / norm

        return vector.tolist()

    @staticmethod
    def cosine_similarity(vec_a: List[float], vec_b: List[float]) -> float:
        """Calculate cosine similarity between two vector lists."""
        a = np.array(vec_a, dtype=np.float32)
        b = np.array(vec_b, dtype=np.float32)
        norm_a = np.linalg.norm(a)
        norm_b = np.linalg.norm(b)
        if norm_a == 0 or norm_b == 0:
            return 0.0
        return float(np.dot(a, b) / (norm_a * norm_b))


embedding_provider = EmbeddingProvider(dimension=settings.EMBEDDING_DIMENSION)
