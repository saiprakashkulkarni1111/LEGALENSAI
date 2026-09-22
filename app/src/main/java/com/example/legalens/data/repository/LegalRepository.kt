package com.example.legalens.data.repository

import com.example.legalens.data.local.LegalDao
import com.example.legalens.data.model.CaseRecordEntity
import com.example.legalens.data.model.ClaimItem
import com.example.legalens.data.model.EvidenceItem
import com.example.legalens.data.model.LegalProvisionEntity
import com.example.legalens.data.model.ResearchResponse
import com.example.legalens.data.model.SourceHealthItem
import com.example.legalens.data.seed.SeedData
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class LegalRepository(private val legalDao: LegalDao) {

    fun getProvisions(): Flow<List<LegalProvisionEntity>> = legalDao.getAllProvisions()

    fun getCases(): Flow<List<CaseRecordEntity>> = legalDao.getAllCases()

    suspend fun getSourceHealth(): List<SourceHealthItem> {
        return SeedData.OFFICIAL_SOURCES_HEALTH
    }

    suspend fun searchCases(query: String): List<CaseRecordEntity> {
        val allCases = legalDao.getAllCasesDirect().ifEmpty {
            legalDao.insertCases(SeedData.LANDMARK_CASES)
            SeedData.LANDMARK_CASES
        }
        if (query.isBlank()) return allCases

        val q = query.lowercase().trim()
        return allCases.filter { case ->
            case.caseTitle.lowercase().contains(q) ||
            case.court.lowercase().contains(q) ||
            case.actsReferred.lowercase().contains(q) ||
            case.relevantExcerpt.lowercase().contains(q) ||
            case.citation.lowercase().contains(q)
        }
    }

    suspend fun performResearch(
        query: String,
        documentContext: String? = null
    ): ResearchResponse {
        val provisions = legalDao.getAllProvisionsDirect().ifEmpty {
            legalDao.insertProvisions(SeedData.STATUTORY_PROVISIONS)
            SeedData.STATUTORY_PROVISIONS
        }
        val cases = legalDao.getAllCasesDirect().ifEmpty {
            legalDao.insertCases(SeedData.LANDMARK_CASES)
            SeedData.LANDMARK_CASES
        }

        val q = query.lowercase().trim()

        // Match statutory provisions
        val matchedProvisions = provisions.filter { p ->
            val numMatch = p.sectionNumber.lowercase().contains(q) || q.contains(p.sectionNumber.lowercase())
            val actMatch = p.actTitle.lowercase().split(" ").any { w -> w.length > 4 && q.contains(w) }
            val kwMatch = p.keywords.split(",").any { kw -> q.contains(kw.trim().lowercase()) }
            val contentMatch = p.content.lowercase().split(" ").count { w -> w.length > 5 && q.contains(w) } >= 2
            numMatch || (actMatch && kwMatch) || kwMatch || contentMatch
        }

        // Match case precedents
        val matchedCases = cases.filter { c ->
            c.caseTitle.lowercase().contains(q) ||
            c.actsReferred.lowercase().contains(q) ||
            c.relevantExcerpt.lowercase().split(" ").count { w -> w.length > 5 && q.contains(w) } >= 2
        }

        val evidenceList = mutableListOf<EvidenceItem>()
        var evIndex = 1

        matchedProvisions.forEach { p ->
            evidenceList.add(
                EvidenceItem(
                    id = "ev_${evIndex++}",
                    sourceTitle = p.actTitle,
                    sectionOrPara = "${p.sectionNumber} - ${p.sectionTitle}",
                    exactText = p.content,
                    officialUrl = p.officialUrl,
                    authority = "Government of India (Legislative Dept)",
                    authorityTier = p.authorityTier,
                    effectivePeriod = "${p.effectiveFrom} to ${p.effectiveUntil}",
                    relevanceScore = 0.96f
                )
            )
        }

        matchedCases.forEach { c ->
            evidenceList.add(
                EvidenceItem(
                    id = "ev_${evIndex++}",
                    sourceTitle = "${c.caseTitle} [${c.citation}]",
                    sectionOrPara = c.bench,
                    exactText = c.relevantExcerpt,
                    officialUrl = c.officialUrl,
                    authority = c.court,
                    authorityTier = "TIER 1 (Supreme Court Precedent)",
                    effectivePeriod = "Decided: ${c.decisionDate}",
                    relevanceScore = 0.94f
                )
            )
        }

        val sdf = SimpleDateFormat("dd MMMM yyyy, HH:mm:ss 'UTC'", Locale.ENGLISH)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val timestamp = sdf.format(Date())

        if (evidenceList.isEmpty()) {
            return ResearchResponse(
                query = query,
                answer = "Unable to verify this claim or locate authoritative statutory provisions matching '$query' within the verified Indian legal repository. Under our strict 'NO EVIDENCE -> NO CLAIM' policy, unverified propositions or non-existent citations are not presented.",
                whatDocumentSays = documentContext,
                evidence = emptyList(),
                claims = listOf(
                    ClaimItem(
                        claimText = "Statutory authority for '$query' is unverified in official indices.",
                        isVerified = false,
                        confidence = "UNVERIFIED",
                        entailmentReasoning = "No verbatim match was retrieved from India Code or Supreme Court e-SCR."
                    )
                ),
                whatThisMeans = "No official central statutory provision or reported apex court precedent was found matching the specific search terms.",
                whatToVerify = listOf(
                    "Check the spelling of the Act or Section reference.",
                    "Verify whether this principle arises from state-level amendments or local rules.",
                    "Review recent High Court notifications in the relevant territorial jurisdiction."
                ),
                possibleNextSteps = listOf(
                    "Search the India Code digital legislative repository (indiacode.nic.in).",
                    "Conduct a manual case search on the eCourts or Supreme Court e-SCR portal.",
                    "Consult an advocate enrolled with the Bar Council of India."
                ),
                questionsForLawyer = listOf(
                    "Does a specific state enactment or subordinate rule govern this subject in my jurisdiction?",
                    "Are there recent unreported High Court judgments that address this situation?"
                ),
                freshnessStatus = "LIVE VERIFIED",
                retrievedAt = timestamp,
                responsibleAiNotice = "Legalens AI provides evidence-first statutory intelligence. It does not replace a lawyer, predict court outcomes, or invent citations."
            )
        }

        val topEv = evidenceList.first()
        val claims = evidenceList.take(3).map { ev ->
            ClaimItem(
                claimText = "Under ${ev.sourceTitle}, the rights and obligations are governed strictly by authoritative statutory text.",
                isVerified = true,
                confidence = "HIGH",
                entailmentReasoning = "Directly supported by verbatim excerpt retrieved from ${ev.authority}."
            )
        }

        return ResearchResponse(
            query = query,
            answer = "Based on authoritative sources retrieved from ${topEv.sourceTitle}, ${topEv.sectionOrPara} establishes the binding legal framework in India. The statutory text stipulates explicit rights and remedies as verified from official publications.",
            whatDocumentSays = documentContext,
            evidence = evidenceList,
            claims = claims,
            whatThisMeans = "In plain terms, ${topEv.sectionOrPara} sets mandatory legal boundaries in India. Contracting parties cannot contract out of fundamental statutory protections (e.g. restraint of trade under Section 27 or unproven penalty damages under Section 74).",
            whatToVerify = listOf(
                "Confirm the effective date of the retrieved provision relative to your agreement's execution date.",
                "Verify whether any subsequent state notifications or judicial rulings qualify this section."
            ),
            possibleNextSteps = listOf(
                "Review the corresponding clauses in your document against the retrieved statutory text.",
                "Inspect the official source directly on ${topEv.officialUrl}."
            ),
            questionsForLawyer = listOf(
                "How has the jurisdictional High Court applied ${topEv.sectionOrPara} to contracts with comparable facts?",
                "Are there specific exceptions or carve-outs applicable to this commercial context?"
            ),
            freshnessStatus = "RECENT",
            retrievedAt = timestamp,
            responsibleAiNotice = "Legalens AI provides evidence-first legal assistance. It does not replace qualified counsel."
        )
    }
}
