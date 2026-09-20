"""
LEGALENS AI - Legal Knowledge Graph Service
Models relationships between Acts, Sections, Judgments, Courts, and Legal Concepts.
Graph Edges: CITES, INTERPRETS, AMENDS, REPEALS, REFERENCES, RELATED_TO, DECIDED_BY, CONTAINS, SUPERSEDES
"""
from typing import Dict, Any, List


class LegalKnowledgeGraphService:
    def __init__(self):
        # Pre-built authoritative graph nodes
        self.nodes = [
            {"id": "act_contract", "label": "Indian Contract Act, 1872", "type": "Act", "year": 1872},
            {"id": "sec_73", "label": "Section 73 (Unliquidated Damages)", "type": "Section", "act": "act_contract"},
            {"id": "sec_74", "label": "Section 74 (Liquidated Damages / Penalty)", "type": "Section", "act": "act_contract"},
            {"id": "sec_27", "label": "Section 27 (Restraint of Trade Void)", "type": "Section", "act": "act_contract"},
            {"id": "concept_liquidated", "label": "Liquidated Damages", "type": "LegalConcept", "domain": "Damages"},
            {"id": "concept_noncompete", "label": "Post-Termination Non-Compete", "type": "LegalConcept", "domain": "Employment"},
            {"id": "case_kailash", "label": "Kailash Nath Associates v. DDA (2015)", "type": "Judgment", "court": "Supreme Court of India"},
            {"id": "case_percept", "label": "Percept D'Mark v. Zaheer Khan (2006)", "type": "Judgment", "court": "Supreme Court of India"},
            {"id": "court_sci", "label": "Supreme Court of India", "type": "Court", "jurisdiction": "National"},
            {"id": "act_dpdp", "label": "Digital Personal Data Protection Act, 2023", "type": "Act", "year": 2023},
            {"id": "act_it", "label": "Information Technology Act, 2000", "type": "Act", "year": 2000}
        ]

        # Edges
        self.edges = [
            {"source": "act_contract", "target": "sec_73", "relation": "CONTAINS"},
            {"source": "act_contract", "target": "sec_74", "relation": "CONTAINS"},
            {"source": "act_contract", "target": "sec_27", "relation": "CONTAINS"},
            {"source": "sec_74", "target": "concept_liquidated", "relation": "RELATED_TO"},
            {"source": "sec_27", "target": "concept_noncompete", "relation": "RELATED_TO"},
            {"source": "case_kailash", "target": "sec_74", "relation": "INTERPRETS"},
            {"source": "case_kailash", "target": "court_sci", "relation": "DECIDED_BY"},
            {"source": "case_percept", "target": "sec_27", "relation": "INTERPRETS"},
            {"source": "case_percept", "target": "court_sci", "relation": "DECIDED_BY"},
            {"source": "act_dpdp", "target": "act_it", "relation": "SUPERSEDES"}
        ]

    def get_full_graph(self) -> Dict[str, Any]:
        return {
            "nodes": self.nodes,
            "edges": self.edges,
            "total_nodes": len(self.nodes),
            "total_edges": len(self.edges)
        }

    def get_subgraph_for_concept(self, concept_query: str) -> Dict[str, Any]:
        query_lower = concept_query.lower()
        matched_node_ids = set()

        for node in self.nodes:
            if query_lower in node["label"].lower() or query_lower in node.get("type", "").lower():
                matched_node_ids.add(node["id"])

        # Find connected edges and target/source nodes
        active_edges = []
        for edge in self.edges:
            if edge["source"] in matched_node_ids or edge["target"] in matched_node_ids:
                active_edges.append(edge)
                matched_node_ids.add(edge["source"])
                matched_node_ids.add(edge["target"])

        active_nodes = [n for n in self.nodes if n["id"] in matched_node_ids]

        return {
            "nodes": active_nodes if active_nodes else self.nodes[:6],
            "edges": active_edges if active_edges else self.edges[:5]
        }


knowledge_graph_service = LegalKnowledgeGraphService()
