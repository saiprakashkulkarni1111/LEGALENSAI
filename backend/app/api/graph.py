"""
LEGALENS AI - Legal Knowledge Graph API Router
Serves knowledge graph nodes and edges for visual legal discovery.
"""
from typing import Optional
from fastapi import APIRouter, Query
from backend.app.services.knowledge_graph_service import knowledge_graph_service

router = APIRouter(prefix="/api/graph", tags=["Knowledge Graph"])


@router.get("")
async def get_knowledge_graph(concept: Optional[str] = Query(None)):
    """Returns the legal knowledge graph nodes and edges."""
    if concept:
        return knowledge_graph_service.get_subgraph_for_concept(concept)
    return knowledge_graph_service.get_full_graph()
