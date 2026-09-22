# RAG

Pipeline: query → year/jurisdiction detection → source registry keyword search + vector cosine search → rerank → evidence filter → citation verifier → response.

Empty or mismatched section numbers (e.g. Section 9999) produce no evidence and an Unable to verify answer.

Local embeddings: hash/projection embedding provider so the stack runs without paid APIs. Production uses a single unified API key: `GOOGLE_API_KEY` (Google Gemini) to process all tasks.
