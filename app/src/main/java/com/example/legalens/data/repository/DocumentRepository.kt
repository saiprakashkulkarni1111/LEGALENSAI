package com.example.legalens.data.repository

import com.example.legalens.data.local.DocumentDao
import com.example.legalens.data.model.ClauseEntity
import com.example.legalens.data.model.CompareResult
import com.example.legalens.data.model.DiffItem
import com.example.legalens.data.model.DocumentEntity
import com.example.legalens.data.model.LawyerPrepPack
import com.example.legalens.data.model.TimelineEventEntity
import com.example.legalens.data.security.PiiSanitizer
import com.example.legalens.data.seed.SeedData
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

class DocumentRepository(private val documentDao: DocumentDao) {

    fun getDocuments(): Flow<List<DocumentEntity>> = documentDao.getAllDocuments()

    suspend fun getDocumentById(id: String): DocumentEntity? = documentDao.getDocumentById(id)

    fun getClausesForDocument(documentId: String): Flow<List<ClauseEntity>> =
        documentDao.getClausesForDocument(documentId)

    suspend fun getClausesForDocumentDirect(documentId: String): List<ClauseEntity> =
        documentDao.getClausesForDocumentDirect(documentId)

    fun getTimelineForDocument(documentId: String): Flow<List<TimelineEventEntity>> =
        documentDao.getTimelineEventsForDocument(documentId)

    fun getAllTimelineEvents(): Flow<List<TimelineEventEntity>> =
        documentDao.getAllTimelineEvents()

    suspend fun ensureSeeded() {
        val existing = documentDao.getDocumentById(SeedData.DEMO_DOC_A_ID)
        if (existing == null) {
            val docs = SeedData.getInitialDocuments()
            for (doc in docs) {
                documentDao.insertDocument(doc)
            }
            documentDao.insertClauses(SeedData.getClausesForDocA())
            documentDao.insertClauses(SeedData.getClausesForDocB())
            documentDao.insertTimelineEvents(SeedData.getTimelineForDocA())
        }
    }

    suspend fun uploadAndAnalyze(
        title: String,
        rawText: String,
        isSyntheticDemo: Boolean = false
    ): String {
        val sanitizeResult = PiiSanitizer.sanitize(rawText)
        val docId = "doc_" + UUID.randomUUID().toString().take(8)

        // Parse extracted clauses intelligently from headings / sections
        val paragraphs = sanitizeResult.sanitizedText.split("\n\n").filter { it.isNotBlank() }
        val clauses = mutableListOf<ClauseEntity>()
        val timelineEvents = mutableListOf<TimelineEventEntity>()

        var clauseIdx = 1
        var reviewCount = 0
        var obligationCount = 0

        for (p in paragraphs) {
            val trimmed = p.trim()
            if (trimmed.length < 30) continue

            val lower = trimmed.lowercase()
            var clauseType = "General Provision"
            var category = "Informational"
            var concern = "Standard contractual clause."
            var legalConcept = "Indian Contract Act, 1872"

            if (lower.contains("non-compete") || lower.contains("restraint") || lower.contains("competing")) {
                clauseType = "Restrictive Covenant & Non-Compete"
                category = "Professional review recommended"
                concern = "Post-employment restraints of trade are void under Section 27 of the Indian Contract Act, 1872."
                legalConcept = "Section 27 (Agreement in Restraint of Trade Void)"
                reviewCount++
                obligationCount++
            } else if (lower.contains("liquidated damages") || lower.contains("penalty") || lower.contains("minimum commitment") || lower.contains("lock-in")) {
                clauseType = "Liquidated Damages & Lock-in"
                category = "Important review"
                concern = "Stipulations by way of penalty require proof of actual loss under Section 74 (Kailash Nath Associates v. DDA)."
                legalConcept = "Section 74 Indian Contract Act (Penalty Stipulations)"
                reviewCount++
                obligationCount++
            } else if (lower.contains("termination") || lower.contains("notice period")) {
                clauseType = "Notice Period & Termination"
                category = "Review"
                concern = "Verify reciprocal notice periods and payment in lieu options."
                legalConcept = "Contractual Termination Rights"
                reviewCount++
                obligationCount++
            } else if (lower.contains("arbitration") || lower.contains("dispute")) {
                clauseType = "Dispute Resolution & Arbitration"
                category = "Informational"
                concern = "Arbitration venue and seat determines court supervisory jurisdiction."
                legalConcept = "Arbitration and Conciliation Act, 1996"
            } else if (lower.contains("compensation") || lower.contains("salary") || lower.contains("remuneration") || lower.contains("inr")) {
                clauseType = "Compensation & Remuneration"
                category = "Informational"
                concern = "Ensure clear breakdown of statutory components (PF, Gratuity, Bonus)."
                legalConcept = "Payment of Wages Act"
                obligationCount++
            } else if (lower.contains("privacy") || lower.contains("data") || lower.contains("personal data")) {
                clauseType = "Data Privacy & Compliance"
                category = "Review"
                concern = "Mandates affirmative consent and notice standards under the DPDP Act, 2023."
                legalConcept = "Digital Personal Data Protection Act, 2023"
                reviewCount++
                obligationCount++
            }

            clauses.add(
                ClauseEntity(
                    id = "cl_${docId}_${clauseIdx}",
                    documentId = docId,
                    clauseType = clauseType,
                    originalText = trimmed,
                    pageNumber = (clauseIdx / 5) + 1,
                    sectionReference = "Section $clauseIdx",
                    plainExplanation = "Summarized clause governing $clauseType.",
                    obligation = if (obligationCount > 0) "Party must comply with duties specified in text." else "None explicit.",
                    attentionCategory = category,
                    potentialConcern = concern,
                    relevantLegalConcept = legalConcept,
                    suggestedVerification = "Verify applicability of recent judicial precedents.",
                    confidence = 0.94f
                )
            )
            clauseIdx++
        }

        // Add a default timeline event
        timelineEvents.add(
            TimelineEventEntity(
                id = "tm_${docId}_1",
                documentId = docId,
                clauseId = clauses.firstOrNull()?.id,
                dateStr = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH).format(Date()),
                eventDescription = "Document uploaded, sanitized, and ingested into local knowledge store.",
                eventType = "Ingestion",
                pageNumber = 1,
                confidence = 1.0f,
                isManualOverride = false
            )
        )

        val docEntity = DocumentEntity(
            id = docId,
            title = title,
            fileName = "$title.txt",
            fileType = "txt",
            fileSize = rawText.length.toLong(),
            mode = if (isSyntheticDemo || title.lowercase().contains("demo")) "SYNTHETIC_DEMO" else "REAL",
            status = "ANALYZED",
            docType = "Commercial Agreement",
            jurisdictionDetected = "India",
            partiesDetected = "Identified Contracting Parties",
            summary = "Analyzed agreement containing ${clauses.size} clauses, $reviewCount items requiring review, and $obligationCount contractual obligations.",
            fullText = sanitizeResult.sanitizedText,
            piiDetectedCount = sanitizeResult.redactionCount,
            piiRedacted = sanitizeResult.redactionCount > 0,
            createdAt = System.currentTimeMillis(),
            totalClauses = clauses.size,
            totalObligations = obligationCount,
            totalDeadlines = timelineEvents.size,
            itemsRequiringReview = reviewCount
        )

        documentDao.insertDocument(docEntity)
        documentDao.insertClauses(clauses)
        documentDao.insertTimelineEvents(timelineEvents)

        return docId
    }

    suspend fun compareDocuments(docAId: String, docBId: String): CompareResult? {
        val docA = documentDao.getDocumentById(docAId) ?: return null
        val docB = documentDao.getDocumentById(docBId) ?: return null

        val clausesA = documentDao.getClausesForDocumentDirect(docAId)
        val clausesB = documentDao.getClausesForDocumentDirect(docBId)

        val mapA = clausesA.associateBy { it.clauseType }
        val mapB = clausesB.associateBy { it.clauseType }

        val allTypes = (mapA.keys + mapB.keys).distinct().sorted()
        val diffs = mutableListOf<DiffItem>()
        var added = 0
        var removed = 0
        var modified = 0

        for (type in allTypes) {
            val inA = mapA[type]
            val inB = mapB[type]

            if (inA != null && inB == null) {
                removed++
                diffs.add(
                    DiffItem(
                        clauseType = type,
                        changeType = "REMOVED",
                        docAText = inA.originalText,
                        docBText = null,
                        deltaSummary = "$type was present in '${docA.title}' but omitted entirely in '${docB.title}'.",
                        potentialSignificance = "The protections and obligations under $type no longer apply in the newer revision."
                    )
                )
            } else if (inA == null && inB != null) {
                added++
                diffs.add(
                    DiffItem(
                        clauseType = type,
                        changeType = "ADDED",
                        docAText = null,
                        docBText = inB.originalText,
                        deltaSummary = "A new $type was introduced in '${docB.title}'.",
                        potentialSignificance = "Introduces new legal duties or compliance burdens not present in the earlier draft."
                    )
                )
            } else if (inA != null && inB != null) {
                if (inA.originalText.trim() != inB.originalText.trim()) {
                    modified++
                    diffs.add(
                        DiffItem(
                            clauseType = type,
                            changeType = "MODIFIED",
                            docAText = inA.originalText,
                            docBText = inB.originalText,
                            deltaSummary = "Wording and obligations under $type were modified between the two documents.",
                            potentialSignificance = "Examine changes to notice timelines, monetary penalties, or restriction scopes."
                        )
                    )
                }
            }
        }

        return CompareResult(
            docAId = docAId,
            docBId = docBId,
            docATitle = docA.title,
            docBTitle = docB.title,
            totalDifferences = diffs.size,
            addedCount = added,
            removedCount = removed,
            modifiedCount = modified,
            diffs = diffs
        )
    }

    suspend fun generateLawyerPrep(documentId: String): LawyerPrepPack? {
        val doc = documentDao.getDocumentById(documentId) ?: return null
        val clauses = documentDao.getClausesForDocumentDirect(documentId)
        val timeline = documentDao.getTimelineEventsForDocument(documentId)

        val reviewClauses = clauses.filter {
            it.attentionCategory.contains("Review", ignoreCase = true) ||
            it.attentionCategory.contains("Professional", ignoreCase = true)
        }

        val questions = mutableListOf(
            "Are the termination notice period terms enforceable under current Indian commercial practice?",
            "Does the non-compete/restraint clause violate Section 27 of the Indian Contract Act, 1872 (ref: Percept D'Mark v. Zaheer Khan)?",
            "Is the stipulated liquidated damages or lock-in penalty enforceable without concrete proof of actual loss (ref: Kailash Nath Associates v. DDA)?",
            "What specific jurisdiction-specific protections apply in the designated arbitration seat?",
            "What documentary evidence should be preserved in case of anticipatory breach or premature exit?"
        )

        val missingInfo = listOf(
            "Counterparty signature pages and board resolutions confirming signatory authority.",
            "Detailed CTC annexure and benefits schedule referenced in remuneration clauses.",
            "Prior correspondence or waiver emails exchanged between the parties.",
            "Proof of payment of stamp duty under the relevant State Stamp Act."
        )

        val keyFacts = listOf(
            "Document Title: ${doc.title}",
            "Jurisdiction: ${doc.jurisdictionDetected}",
            "Parties Detected: ${doc.partiesDetected}",
            "Clauses Analyzed: ${clauses.size}",
            "Flagged for Counsel Review: ${reviewClauses.size}"
        )

        val mattersForReview = reviewClauses.map {
            "${it.clauseType} (${it.sectionReference}): ${it.potentialConcern}"
        }

        val sdf = SimpleDateFormat("dd MMMM yyyy, HH:mm 'UTC'", Locale.ENGLISH)
        sdf.timeZone = TimeZone.getTimeZone("UTC")

        return LawyerPrepPack(
            documentTitle = doc.title,
            preparedAt = sdf.format(Date()),
            executiveSummary = "Dossier prepared for legal consultation regarding '${doc.title}'. The agreement governs relations between ${doc.partiesDetected}, containing ${clauses.size} analyzed clauses, ${reviewClauses.size} items flagged for professional review, and extracted chronological milestones.",
            keyFacts = keyFacts,
            missingInformation = missingInfo,
            mattersForReview = mattersForReview,
            questionsForCounsel = questions,
            legalDisclaimer = "This briefing pack is synthesized by Legalens AI for consultation with a qualified advocate enrolled with the Bar Council of India. It does not constitute legal advice."
        )
    }

    suspend fun evaluateLawImpact(documentId: String): List<String> {
        val doc = documentDao.getDocumentById(documentId) ?: return emptyList()
        val clauses = documentDao.getClausesForDocumentDirect(documentId)

        val impacts = mutableListOf<String>()

        val hasNonCompete = clauses.any { it.clauseType.contains("Non-Compete", ignoreCase = true) }
        if (hasNonCompete) {
            impacts.add("Statutory Impact (Section 27 Contract Act): Post-termination non-compete clauses are held strictly void by the Supreme Court of India in Percept D'Mark (2006). Restraints extending past service termination cannot be enforced.")
        }

        val hasDamages = clauses.any { it.clauseType.contains("Liquidated", ignoreCase = true) || it.clauseType.contains("Damages", ignoreCase = true) }
        if (hasDamages) {
            impacts.add("Statutory Impact (Section 74 Contract Act): Under Kailash Nath Associates v. DDA (2015), pre-agreed liquidated damages are not automatically recoverable. The party claiming breach must prove actual loss where possible.")
        }

        val hasPrivacy = clauses.any { it.clauseType.contains("Privacy", ignoreCase = true) || it.clauseType.contains("Data", ignoreCase = true) }
        if (hasPrivacy) {
            impacts.add("Statutory Impact (DPDP Act 2023): Notice and consent mechanisms must comply with the Digital Personal Data Protection Act, 2023. Blanket waivers or unconstrained data sharing risk regulatory penalties.")
        }

        if (impacts.isEmpty()) {
            impacts.add("General Commercial Law: Clauses must be read in harmony with the Indian Contract Act, 1872 and the Specific Relief Act, 1963.")
        }

        return impacts
    }
}
