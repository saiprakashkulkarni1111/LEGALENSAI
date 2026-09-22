package com.example.legalens.data.model

data class DocumentEntity(
    val id: String,
    val title: String,
    val fileName: String,
    val fileType: String,
    val fileSize: Long,
    val mode: String, // "REAL" or "SYNTHETIC_DEMO"
    val status: String, // "ANALYZED"
    val docType: String,
    val jurisdictionDetected: String,
    val partiesDetected: String, // comma-separated
    val summary: String,
    val fullText: String,
    val piiDetectedCount: Int,
    val piiRedacted: Boolean,
    val createdAt: Long,
    val totalClauses: Int,
    val totalObligations: Int,
    val totalDeadlines: Int,
    val itemsRequiringReview: Int
)

data class ClauseEntity(
    val id: String,
    val documentId: String,
    val clauseType: String,
    val originalText: String,
    val pageNumber: Int,
    val sectionReference: String,
    val plainExplanation: String,
    val obligation: String,
    val attentionCategory: String, // Informational, Review, Important review, Professional review recommended
    val potentialConcern: String,
    val relevantLegalConcept: String,
    val suggestedVerification: String,
    val confidence: Float
)

data class TimelineEventEntity(
    val id: String,
    val documentId: String,
    val clauseId: String?,
    val dateStr: String,
    val eventDescription: String,
    val eventType: String,
    val pageNumber: Int,
    val confidence: Float,
    val isManualOverride: Boolean
)

data class LegalProvisionEntity(
    val id: String,
    val actTitle: String,
    val actNumber: String,
    val year: Int,
    val sectionNumber: String,
    val sectionTitle: String,
    val effectiveFrom: String,
    val effectiveUntil: String,
    val officialUrl: String,
    val content: String,
    val keywords: String,
    val authorityTier: String
)

data class CaseRecordEntity(
    val id: String,
    val caseTitle: String,
    val citation: String,
    val caseNumber: String,
    val court: String,
    val year: Int,
    val decisionDate: String,
    val bench: String,
    val actsReferred: String,
    val officialUrl: String,
    val relevantExcerpt: String,
    val status: String,
    val requiresOfficialSearch: Boolean,
    val officialSearchGuidance: String
)

data class SourceHealthItem(
    val sourceId: String,
    val sourceName: String,
    val authority: String,
    val authorityTier: String,
    val officialUrl: String,
    val freshnessStatus: String,
    val lastChecked: String,
    val automationAvailable: Boolean,
    val latencyMs: Long
)

data class EvidenceItem(
    val id: String,
    val sourceTitle: String,
    val sectionOrPara: String,
    val exactText: String,
    val officialUrl: String,
    val authority: String,
    val authorityTier: String,
    val effectivePeriod: String,
    val relevanceScore: Float
)

data class ClaimItem(
    val claimText: String,
    val isVerified: Boolean,
    val confidence: String,
    val entailmentReasoning: String
)

data class ResearchResponse(
    val query: String,
    val answer: String,
    val whatDocumentSays: String? = null,
    val evidence: List<EvidenceItem>,
    val claims: List<ClaimItem>,
    val whatThisMeans: String,
    val whatToVerify: List<String>,
    val possibleNextSteps: List<String>,
    val questionsForLawyer: List<String>,
    val freshnessStatus: String,
    val retrievedAt: String,
    val responsibleAiNotice: String
)

data class DiffItem(
    val clauseType: String,
    val changeType: String, // "ADDED", "REMOVED", "MODIFIED"
    val docAText: String?,
    val docBText: String?,
    val deltaSummary: String,
    val potentialSignificance: String
)

data class CompareResult(
    val docAId: String,
    val docBId: String,
    val docATitle: String,
    val docBTitle: String,
    val totalDifferences: Int,
    val addedCount: Int,
    val removedCount: Int,
    val modifiedCount: Int,
    val diffs: List<DiffItem>
)

data class LawyerPrepPack(
    val documentTitle: String,
    val preparedAt: String,
    val executiveSummary: String,
    val keyFacts: List<String>,
    val missingInformation: List<String>,
    val mattersForReview: List<String>,
    val questionsForCounsel: List<String>,
    val legalDisclaimer: String
)

data class GraphNode(
    val id: String,
    val label: String,
    val type: String // "Act", "Section", "Case", "Concept"
)

data class GraphEdge(
    val source: String,
    val relation: String, // "CITES", "INTERPRETS", "GOVERNED_BY", "REFERENCES"
    val target: String
)
