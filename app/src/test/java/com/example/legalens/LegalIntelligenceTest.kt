package com.example.legalens

import com.example.legalens.data.local.LocalDocumentDao
import com.example.legalens.data.local.LocalLegalDao
import com.example.legalens.data.repository.DocumentRepository
import com.example.legalens.data.repository.LegalRepository
import com.example.legalens.data.seed.SeedData
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LegalIntelligenceTest {

    private lateinit var documentRepository: DocumentRepository
    private lateinit var legalRepository: LegalRepository

    @Before
    fun setup() = runBlocking {
        val docDao = LocalDocumentDao()
        val legalDao = LocalLegalDao()
        documentRepository = DocumentRepository(docDao)
        legalRepository = LegalRepository(legalDao)

        documentRepository.ensureSeeded()
    }

    @Test
    fun testSeededDocumentsPresent() = runBlocking {
        val docA = documentRepository.getDocumentById(SeedData.DEMO_DOC_A_ID)
        assertNotNull(docA)
        assertEquals("SYNTHETIC_DEMO", docA?.mode)
        assertTrue((docA?.totalClauses ?: 0) > 0)
    }

    @Test
    fun testResearchReturnsEvidenceForSection27() = runBlocking {
        val response = legalRepository.performResearch("Section 27 Indian Contract Act non-compete")
        assertNotNull(response)
        assertTrue(response.evidence.isNotEmpty())
        assertTrue(response.answer.contains("void", ignoreCase = true))
        assertTrue(response.claims.all { it.isVerified })
    }

    @Test
    fun testDocumentComparisonGeneratesDiffs() = runBlocking {
        val compareResult = documentRepository.compareDocuments(
            SeedData.DEMO_DOC_A_ID,
            SeedData.DEMO_DOC_B_ID
        )
        assertNotNull(compareResult)
        assertTrue(compareResult!!.totalDifferences > 0)
        assertTrue(compareResult.diffs.any { it.changeType == "MODIFIED" })
    }

    @Test
    fun testLawyerPrepDossierGenerated() = runBlocking {
        val dossier = documentRepository.generateLawyerPrep(SeedData.DEMO_DOC_A_ID)
        assertNotNull(dossier)
        assertTrue(dossier!!.executiveSummary.isNotBlank())
        assertTrue(dossier.keyFacts.isNotEmpty())
        assertTrue(dossier.questionsForCounsel.isNotEmpty())
        assertTrue(dossier.legalDisclaimer.contains("Bar Council of India"))
    }
}
