package com.example.legalens.data.seed

import com.example.legalens.data.model.CaseRecordEntity
import com.example.legalens.data.model.ClauseEntity
import com.example.legalens.data.model.DocumentEntity
import com.example.legalens.data.model.GraphEdge
import com.example.legalens.data.model.GraphNode
import com.example.legalens.data.model.LegalProvisionEntity
import com.example.legalens.data.model.SourceHealthItem
import com.example.legalens.data.model.TimelineEventEntity
import java.util.UUID

object SeedData {

    val STATUTORY_PROVISIONS = listOf(
        LegalProvisionEntity(
            id = "contract_act_sec_27",
            actTitle = "The Indian Contract Act, 1872",
            actNumber = "Act No. 9 of 1872",
            year = 1872,
            sectionNumber = "Section 27",
            sectionTitle = "Agreement in restraint of trade, void",
            effectiveFrom = "1872-09-01",
            effectiveUntil = "CURRENT",
            officialUrl = "https://www.indiacode.nic.in/handle/123456789/2187?view_type=browse&sam_handle=123456789/1362",
            content = "Every agreement by which any one is restrained from exercising a lawful profession, trade or business of any kind, is to that extent void. Exception 1: One who sells the goodwill of a business may agree with the buyer to refrain from carrying on a similar business, within specified local limits, so long as the buyer, or any person deriving title to the goodwill from him, carries on a like business therein.",
            keywords = "non-compete, restraint of trade, void, employment, goodwill, solicitation, post-employment",
            authorityTier = "TIER 1"
        ),
        LegalProvisionEntity(
            id = "contract_act_sec_73",
            actTitle = "The Indian Contract Act, 1872",
            actNumber = "Act No. 9 of 1872",
            year = 1872,
            sectionNumber = "Section 73",
            sectionTitle = "Compensation for loss or damage caused by breach of contract",
            effectiveFrom = "1872-09-01",
            effectiveUntil = "CURRENT",
            officialUrl = "https://www.indiacode.nic.in/handle/123456789/2187?view_type=browse&sam_handle=123456789/1362",
            content = "When a contract has been broken, the party who suffers by such breach is entitled to receive, from the party who has broken the contract, compensation for any loss or damage caused to him thereby, which naturally arose in the usual course of things from such breach, or which the parties knew, when they made the contract, to be likely to result from the breach of it. Such compensation is not to be given for any remote and indirect loss or damage sustained by reason of the breach.",
            keywords = "breach, damages, compensation, loss, liquidated damages, contract, liability",
            authorityTier = "TIER 1"
        ),
        LegalProvisionEntity(
            id = "contract_act_sec_74",
            actTitle = "The Indian Contract Act, 1872",
            actNumber = "Act No. 9 of 1872",
            year = 1872,
            sectionNumber = "Section 74",
            sectionTitle = "Compensation for breach of contract where penalty stipulated for",
            effectiveFrom = "1872-09-01",
            effectiveUntil = "CURRENT",
            officialUrl = "https://www.indiacode.nic.in/handle/123456789/2187?view_type=browse&sam_handle=123456789/1362",
            content = "When a contract has been broken, if a sum is named in the contract as the amount to be paid in case of such breach, or if the contract contains any other stipulation by way of penalty, the party complaining of the breach is entitled, whether or not actual damage or loss is proved to have been caused thereby, to receive from the party who has broken the contract reasonable compensation not exceeding the amount so named or, as the case may be, the penalty stipulated for.",
            keywords = "penalty, liquidated damages, stipulation, breach, reasonable compensation, proof of loss",
            authorityTier = "TIER 1"
        ),
        LegalProvisionEntity(
            id = "arbitration_act_sec_7",
            actTitle = "The Arbitration and Conciliation Act, 1996",
            actNumber = "Act No. 26 of 1996",
            year = 1996,
            sectionNumber = "Section 7",
            sectionTitle = "Arbitration agreement",
            effectiveFrom = "1996-08-22",
            effectiveUntil = "CURRENT",
            officialUrl = "https://www.indiacode.nic.in/handle/123456789/1978?view_type=browse",
            content = "In this Part, 'arbitration agreement' means an agreement by the parties to submit to arbitration all or certain disputes which have arisen or which may arise between them in respect of a defined legal relationship, whether contractual or not. An arbitration agreement shall be in writing.",
            keywords = "arbitration, dispute resolution, arbitrator, tribunal, clause, jurisdiction",
            authorityTier = "TIER 1"
        ),
        LegalProvisionEntity(
            id = "it_act_sec_43a",
            actTitle = "The Information Technology Act, 2000",
            actNumber = "Act No. 21 of 2000",
            year = 2000,
            sectionNumber = "Section 43A",
            sectionTitle = "Compensation for failure to protect data",
            effectiveFrom = "2009-10-27",
            effectiveUntil = "CURRENT",
            officialUrl = "https://www.indiacode.nic.in/handle/123456789/1999?view_type=browse",
            content = "Where a body corporate, possessing, dealing or handling any sensitive personal data or information in a computer resource which it owns, controls or operates, is negligent in implementing and maintaining reasonable security practices and procedures and thereby causes wrongful loss or wrongful gain to any person, such body corporate shall be liable to pay damages by way of compensation to the person so affected.",
            keywords = "data protection, sensitive personal data, security practices, compensation, privacy",
            authorityTier = "TIER 1"
        ),
        LegalProvisionEntity(
            id = "dpdp_act_sec_6",
            actTitle = "The Digital Personal Data Protection Act, 2023",
            actNumber = "Act No. 22 of 2023",
            year = 2023,
            sectionNumber = "Section 6",
            sectionTitle = "Consent and Notice requirements for processing personal data",
            effectiveFrom = "2023-08-11",
            effectiveUntil = "CURRENT",
            officialUrl = "https://www.indiacode.nic.in/handle/123456789/19842",
            content = "The consent given by the Data Principal shall be free, specific, informed, unconditional and unambiguous with a clear affirmative action, and shall signify an agreement to the processing of her personal data for the specified purpose and be limited to such personal data as is necessary for such specified purpose.",
            keywords = "dpdp, consent, data principal, personal data, notice, privacy, processing, compliance",
            authorityTier = "TIER 1"
        ),
        LegalProvisionEntity(
            id = "specific_relief_sec_14",
            actTitle = "The Specific Relief Act, 1963",
            actNumber = "Act No. 47 of 1963",
            year = 1963,
            sectionNumber = "Section 14",
            sectionTitle = "Contracts not specifically enforceable",
            effectiveFrom = "2018-10-01",
            effectiveUntil = "CURRENT",
            officialUrl = "https://www.indiacode.nic.in/handle/123456789/1583",
            content = "The following contracts cannot be specifically enforced, namely: (a) where a party to the contract has obtained substituted performance of contract in accordance with the provisions of section 20; (b) a contract, the performance of which involves the performance of a continuous duty which the court cannot supervise; (c) a contract which is so dependent on the personal qualifications of the parties that the court cannot enforce specific performance of its material terms; and (d) a contract which is in its nature determinable.",
            keywords = "specific performance, determinable, personal service, injunction, employment contract",
            authorityTier = "TIER 1"
        )
    )

    val LANDMARK_CASES = listOf(
        CaseRecordEntity(
            id = "sci_kailash_nath_2015",
            caseTitle = "Kailash Nath Associates v. Delhi Development Authority & Anr.",
            citation = "(2015) 4 SCC 136",
            caseNumber = "Civil Appeal No. 193 of 2015",
            court = "Supreme Court of India",
            year = 2015,
            decisionDate = "2015-01-09",
            bench = "Ranjan Gogoi, R.F. Nariman, JJ.",
            actsReferred = "The Indian Contract Act, 1872 - Section 73, Section 74",
            officialUrl = "https://main.sci.gov.in/jonew/judis/42220.pdf",
            relevantExcerpt = "Where a sum is named in a contract as a liquidated amount payable by way of damages, the party complaining of a breach can receive as reasonable compensation such liquidated amount only if it is a genuine pre-estimate of damages fixed by both parties and found to be such by the Court. In all cases, proof of actual damage is not dispensed with under Section 74; where loss can be proved, it must be proved.",
            status = "Disposed",
            requiresOfficialSearch = false,
            officialSearchGuidance = "Available on Supreme Court e-SCR repository."
        ),
        CaseRecordEntity(
            id = "sci_percept_dmark_2006",
            caseTitle = "Percept D'Mark (India) (P) Ltd. v. Zaheer Khan & Anr.",
            citation = "(2006) 4 SCC 227",
            caseNumber = "Appeal (Civil) 5577 of 2005",
            court = "Supreme Court of India",
            year = 2006,
            decisionDate = "2006-03-22",
            bench = "Y.K. Sabharwal, C.K. Thakker, P.K. Balasubramanyan, JJ.",
            actsReferred = "The Indian Contract Act, 1872 - Section 27; Specific Relief Act, 1963 - Section 41",
            officialUrl = "https://main.sci.gov.in/jonew/judis/27641.pdf",
            relevantExcerpt = "Under Section 27 of the Indian Contract Act, 1872, a restrictive covenant extending beyond the term of the contract is void and not enforceable. The doctrine of restraint of trade does not apply during the continuance of the contract for employment or personal service, but post-termination restraints are void.",
            status = "Disposed",
            requiresOfficialSearch = false,
            officialSearchGuidance = "Authoritative Supreme Court landmark precedent."
        ),
        CaseRecordEntity(
            id = "sci_krishan_murgai_1981",
            caseTitle = "Superintendence Company of India (P) Ltd. v. Krishan Murgai",
            citation = "(1981) 2 SCC 246",
            caseNumber = "Civil Appeal No. 1297 of 1970",
            court = "Supreme Court of India",
            year = 1980,
            decisionDate = "1980-04-22",
            bench = "V.D. Tulzapurkar, A.P. Sen, JJ.",
            actsReferred = "The Indian Contract Act, 1872 - Section 27",
            officialUrl = "https://main.sci.gov.in/jonew/judis/7533.pdf",
            relevantExcerpt = "A negative covenant in an employment agreement restraining an employee from engaging in a similar business or employment after the termination of his service is void under Section 27 of the Contract Act. Indian law recognizes no test of reasonableness for post-employment restrictions under Section 27.",
            status = "Disposed",
            requiresOfficialSearch = false,
            officialSearchGuidance = "Supreme Court full bench decision."
        ),
        CaseRecordEntity(
            id = "sci_puttaswamy_2017",
            caseTitle = "Justice K.S. Puttaswamy (Retd.) v. Union of India & Ors.",
            citation = "(2017) 10 SCC 1",
            caseNumber = "Writ Petition (Civil) No. 494 of 2012",
            court = "Supreme Court of India",
            year = 2017,
            decisionDate = "2017-08-24",
            bench = "9-Judge Constitution Bench",
            actsReferred = "Constitution of India - Article 21; Information Technology Act, 2000",
            officialUrl = "https://main.sci.gov.in/supremecourt/2012/35071/35071_2012_Judgement_24-Aug-2017.pdf",
            relevantExcerpt = "The right to privacy is protected as an intrinsic part of the right to life and personal liberty under Article 21 and as a part of the freedoms guaranteed by Part III of the Constitution. Informational privacy is a crucial facet of privacy, requiring lawful, necessary, and proportionate data processing.",
            status = "Disposed",
            requiresOfficialSearch = false,
            officialSearchGuidance = "Historic 9-Judge Constitution Bench judgment."
        ),
        CaseRecordEntity(
            id = "sci_golikari_1967",
            caseTitle = "Niranjan Shankar Golikari v. Century Spinning & Mfg. Co. Ltd.",
            citation = "(1967) 2 SCR 378",
            caseNumber = "Civil Appeal No. 1021 of 1965",
            court = "Supreme Court of India",
            year = 1967,
            decisionDate = "1967-01-17",
            bench = "J.M. Shelat, V. Bhargava, JJ.",
            actsReferred = "The Indian Contract Act, 1872 - Section 27",
            officialUrl = "https://main.sci.gov.in/jonew/judis/2275.pdf",
            relevantExcerpt = "A negative covenant operative during the period of employment when the employee is bound to serve his employer exclusively is not a restraint of trade and therefore does not fall under Section 27 of the Contract Act. Restraints operative only during the active term of employment are valid.",
            status = "Disposed",
            requiresOfficialSearch = false,
            officialSearchGuidance = "Landmark ruling on negative covenants during active service."
        )
    )

    val OFFICIAL_SOURCES_HEALTH = listOf(
        SourceHealthItem(
            sourceId = "india_code",
            sourceName = "India Code Digital Legislative Repository",
            authority = "Ministry of Law & Justice, Govt of India",
            authorityTier = "TIER 1 (Statutory Repository)",
            officialUrl = "https://www.indiacode.nic.in",
            freshnessStatus = "LIVE",
            lastChecked = "2026-09-21 14:15 UTC",
            automationAvailable = true,
            latencyMs = 124L
        ),
        SourceHealthItem(
            sourceId = "supreme_court",
            sourceName = "Supreme Court of India (e-SCR Portal)",
            authority = "Supreme Court of India",
            authorityTier = "TIER 1 (Apex Court Jurisprudence)",
            officialUrl = "https://digiscr.sci.gov.in",
            freshnessStatus = "LIVE",
            lastChecked = "2026-09-21 14:18 UTC",
            automationAvailable = true,
            latencyMs = 210L
        ),
        SourceHealthItem(
            sourceId = "high_courts",
            sourceName = "High Courts of India Registry & Judgments",
            authority = "High Courts of Respective States",
            authorityTier = "TIER 2 (High Court Precedents)",
            officialUrl = "https://judgments.ecourts.gov.in/pdfsearch",
            freshnessStatus = "RECENT",
            lastChecked = "2026-09-21 13:45 UTC",
            automationAvailable = true,
            latencyMs = 380L
        ),
        SourceHealthItem(
            sourceId = "ecourts_services",
            sourceName = "eCourts Integrated Services Portal",
            authority = "eCommittee, Supreme Court of India",
            authorityTier = "TIER 2 (District & Taluka Case Status)",
            officialUrl = "https://services.ecourts.gov.in",
            freshnessStatus = "OFFICIAL SEARCH",
            lastChecked = "2026-09-21 12:00 UTC",
            automationAvailable = false,
            latencyMs = 450L
        ),
        SourceHealthItem(
            sourceId = "njdg",
            sourceName = "National Judicial Data Grid (NJDG)",
            authority = "Ministry of Law & Justice & Supreme Court",
            authorityTier = "TIER 2 (National Court Statistics)",
            officialUrl = "https://njdg.ecourts.gov.in",
            freshnessStatus = "RECENT",
            lastChecked = "2026-09-21 11:30 UTC",
            automationAvailable = true,
            latencyMs = 290L
        )
    )

    val GRAPH_NODES = listOf(
        GraphNode("act_contract_1872", "Indian Contract Act, 1872", "Act"),
        GraphNode("sec_27", "Section 27 (Restraint of Trade)", "Section"),
        GraphNode("sec_73", "Section 73 (Breach & Loss)", "Section"),
        GraphNode("sec_74", "Section 74 (Liquidated Damages & Penalty)", "Section"),
        GraphNode("case_kailash_nath", "Kailash Nath Associates v. DDA", "Case"),
        GraphNode("case_percept_dmark", "Percept D'Mark v. Zaheer Khan", "Case"),
        GraphNode("case_krishan_murgai", "Superintendence Co. v. Krishan Murgai", "Case"),
        GraphNode("concept_non_compete", "Post-Employment Non-Compete", "Concept"),
        GraphNode("concept_proof_of_loss", "Proof of Actual Loss Requirement", "Concept"),
        GraphNode("act_dpdp_2023", "DPDP Act, 2023", "Act"),
        GraphNode("sec_dpdp_6", "Section 6 (Consent Architecture)", "Section")
    )

    val GRAPH_EDGES = listOf(
        GraphEdge("sec_27", "GOVERNED_BY", "act_contract_1872"),
        GraphEdge("sec_73", "GOVERNED_BY", "act_contract_1872"),
        GraphEdge("sec_74", "GOVERNED_BY", "act_contract_1872"),
        GraphEdge("case_kailash_nath", "INTERPRETS", "sec_74"),
        GraphEdge("case_kailash_nath", "CITES", "sec_73"),
        GraphEdge("case_percept_dmark", "INTERPRETS", "sec_27"),
        GraphEdge("case_krishan_murgai", "INTERPRETS", "sec_27"),
        GraphEdge("concept_non_compete", "GOVERNED_BY", "sec_27"),
        GraphEdge("concept_proof_of_loss", "ESTABLISHED_IN", "case_kailash_nath"),
        GraphEdge("sec_dpdp_6", "GOVERNED_BY", "act_dpdp_2023")
    )

    val DEMO_DOC_A_ID = "doc_technova_employment_v1"
    val DEMO_DOC_B_ID = "doc_technova_employment_v2"

    val DEMO_DOC_A_TEXT = """
EMPLOYMENT AND CONFIDENTIALITY AGREEMENT

[SYNTHETIC DEMO - PREPARED FOR LEGALENS EVALUATION]

This Employment Agreement ("Agreement") is executed on 15th January 2026 at Bangalore, Karnataka, by and between:

TechNova Solutions Private Limited, a company incorporated under the Companies Act, 2013, having its registered corporate office at Indiranagar, Bangalore - 560038, Karnataka (hereinafter referred to as the "Company", which expression shall include its successors and assigns);

AND

Mr. Rohan Sharma, residing at Flat 402, Green Glen Layout, Bellandur, Bangalore - 560103, Karnataka, holding PAN [REDACTED_PAN] and Aadhaar Number [REDACTED_AADHAAR] (hereinafter referred to as the "Employee").

1. APPOINTMENT AND COMMENCEMENT
The Company hereby employs the Employee as Principal Systems Architect, and the Employee accepts employment commencing on 1st February 2026. The initial probationary period shall be for a duration of 3 months from the date of joining.

2. COMPENSATION AND REMUNERATION
The Employee shall be entitled to an annual gross compensation of INR 28,00,000 (Twenty-Eight Lakhs Rupees only). The salary shall be payable monthly on or before the 5th day of each calendar month directly into the Employee's designated bank account ([REDACTED_BANK_ACCOUNT], IFSC: HDFC0001234).

3. NOTICE PERIOD AND TERMINATION
Either party may terminate this Agreement by providing 30 days prior written notice to the other party. The Company reserves the right to terminate the Employee's employment immediately without notice in the event of gross misconduct, willful insubordination, or material breach of this Agreement.

4. LIQUIDATED DAMAGES AND MINIMUM COMMITMENT
The Employee agrees to serve the Company for a minimum commitment period of 12 months from the date of joining. In the event the Employee departs prior to completion of the 12-month period, the Employee shall pay to the Company a fixed sum of INR 5,00,000 (Five Lakhs Rupees) as liquidated damages for training and operational disruption.

5. RESTRICTIVE COVENANT AND NON-COMPETE
During the term of employment and for a period of 24 months following the termination of employment for any reason whatsoever, the Employee shall not directly or indirectly engage in, advise, invest in, or be employed by any entity operating a competing enterprise in the territory of India.

6. NON-SOLICITATION
For a period of 12 months post-termination, the Employee shall not actively solicit, entice away, or attempt to hire any existing full-time employee or active client of the Company.

7. CONFIDENTIAL INFORMATION AND NON-DISCLOSURE
The Employee acknowledges that during employment, they will have access to proprietary trade secrets, system architectures, customer databases, and software algorithms. The Employee shall hold all such confidential information in strict trust and confidence.

8. INTELLECTUAL PROPERTY ASSIGNMENT
All inventions, software code, technical documentation, designs, and patentable works developed by the Employee in the course of employment shall be the sole and exclusive property of the Company as a work made for hire.

9. DISPUTE RESOLUTION AND ARBITRATION
Any dispute, controversy, or claim arising out of or relating to this Agreement shall be settled by binding arbitration in accordance with the Arbitration and Conciliation Act, 1996. The seat and venue of arbitration shall be Bangalore, Karnataka.

10. GOVERNING LAW AND JURISDICTION
This Agreement shall be governed by, and construed in accordance with, the laws of the Republic of India. Subject to the arbitration clause, the competent courts at Bangalore shall have exclusive territorial jurisdiction.
    """.trimIndent()

    val DEMO_DOC_B_TEXT = """
AMENDED EMPLOYMENT AND CONFIDENTIALITY AGREEMENT

[SYNTHETIC DEMO - VERSION B WITH MODIFIED TERMS]

This Amended Employment Agreement ("Agreement") is executed on 1st February 2026 at Bangalore, Karnataka, by and between TechNova Solutions Private Limited and Mr. Rohan Sharma.

1. APPOINTMENT AND COMMENCEMENT
The Company employs the Employee as Senior Principal Systems Architect commencing on 1st February 2026.

2. COMPENSATION AND REMUNERATION
The Employee shall be entitled to an annual gross compensation of INR 32,00,000 payable monthly on or before the 1st day of each calendar month.

3. NOTICE PERIOD AND TERMINATION
Either party may terminate this Agreement by providing 7 days prior written notice to the other party. The Company reserves the right to terminate immediately without notice in case of cause.

4. LIQUIDATED DAMAGES AND MINIMUM COMMITMENT
The Employee agrees to a minimum commitment period of 18 months from the date of joining. Departure prior to completion shall incur a penalty payment of INR 8,00,000 as liquidated damages.

5. RESTRICTIVE COVENANT AND NON-COMPETE
For a period of 36 months post-termination, the Employee shall not directly or indirectly work for any competitor in India.

6. CONFIDENTIAL INFORMATION AND NON-DISCLOSURE
The Employee shall maintain strict confidentiality over all proprietary source code and client data indefinitely.

7. DATA PRIVACY AND COMPLIANCE
The Employee shall process all personal data strictly in compliance with the Digital Personal Data Protection Act, 2023 and company security frameworks.

8. DISPUTE RESOLUTION AND ARBITRATION
Any dispute shall be referred to arbitration in accordance with the Arbitration and Conciliation Act, 1996 in Bangalore.

9. GOVERNING LAW AND JURISDICTION
This Agreement is governed by the laws of India and subject to the exclusive jurisdiction of the courts at Bangalore.
    """.trimIndent()

    fun getInitialDocuments(): List<DocumentEntity> {
        return listOf(
            DocumentEntity(
                id = DEMO_DOC_A_ID,
                title = "Software Employment Agreement (TechNova)",
                fileName = "Software_Employment_Agreement.pdf",
                fileType = "pdf",
                fileSize = 48520L,
                mode = "SYNTHETIC_DEMO",
                status = "ANALYZED",
                docType = "Employment & Non-Disclosure Agreement",
                jurisdictionDetected = "India (Karnataka)",
                partiesDetected = "TechNova Solutions Pvt Ltd, Rohan Sharma",
                summary = "Full-time technical executive appointment agreement containing 3-month probation, 30 days notice period, 12-month lock-in with INR 5,00,000 liquidated damages, 24-month post-termination non-compete, Bangalore arbitration, and IP assignment.",
                fullText = DEMO_DOC_A_TEXT,
                piiDetectedCount = 3,
                piiRedacted = true,
                createdAt = System.currentTimeMillis() - 86400000L * 3,
                totalClauses = 10,
                totalObligations = 8,
                totalDeadlines = 5,
                itemsRequiringReview = 3
            ),
            DocumentEntity(
                id = DEMO_DOC_B_ID,
                title = "Amended Employment Agreement (TechNova v2)",
                fileName = "Software_Employment_Agreement_Amended.txt",
                fileType = "txt",
                fileSize = 24100L,
                mode = "SYNTHETIC_DEMO",
                status = "ANALYZED",
                docType = "Amended Employment Agreement",
                jurisdictionDetected = "India (Karnataka)",
                partiesDetected = "TechNova Solutions Pvt Ltd, Rohan Sharma",
                summary = "Revised contract proposing compensation increase to INR 32,00,000, shortened 7-day notice period, extended 18-month lock-in with INR 8,00,000 penalty, 36-month non-compete, and addition of DPDP Act 2023 compliance.",
                fullText = DEMO_DOC_B_TEXT,
                piiDetectedCount = 0,
                piiRedacted = true,
                createdAt = System.currentTimeMillis() - 86400000L,
                totalClauses = 9,
                totalObligations = 7,
                totalDeadlines = 4,
                itemsRequiringReview = 4
            )
        )
    }

    fun getClausesForDocA(): List<ClauseEntity> {
        return listOf(
            ClauseEntity(
                id = "cl_a_commence",
                documentId = DEMO_DOC_A_ID,
                clauseType = "Appointment & Probation",
                originalText = "The Company hereby employs the Employee as Principal Systems Architect, and the Employee accepts employment commencing on 1st February 2026. The initial probationary period shall be for a duration of 3 months from the date of joining.",
                pageNumber = 1,
                sectionReference = "Clause 1",
                plainExplanation = "Defines your job title (Principal Systems Architect), start date (Feb 1, 2026), and a 3-month probation period.",
                obligation = "Employee must report on Feb 1, 2026 and complete 3 months probation satisfactorily.",
                attentionCategory = "Informational",
                potentialConcern = "None observed. Standard probation clause in Indian IT sector.",
                relevantLegalConcept = "Probation and Confirmation under Industrial Employment Principles",
                suggestedVerification = "Verify notice period provisions during probation versus post-confirmation.",
                confidence = 0.98f
            ),
            ClauseEntity(
                id = "cl_a_comp",
                documentId = DEMO_DOC_A_ID,
                clauseType = "Compensation & Remuneration",
                originalText = "The Employee shall be entitled to an annual gross compensation of INR 28,00,000 (Twenty-Eight Lakhs Rupees only). The salary shall be payable monthly on or before the 5th day of each calendar month directly into the Employee's designated bank account.",
                pageNumber = 1,
                sectionReference = "Clause 2",
                plainExplanation = "Sets gross salary at INR 28 Lakhs/annum, credited by the 5th of each month.",
                obligation = "Company must disburse monthly salary on or before the 5th calendar day.",
                attentionCategory = "Informational",
                potentialConcern = "Confirm if variable bonus, PF deductions, or gratuity are included in gross CTC.",
                relevantLegalConcept = "Payment of Wages Act & Code on Wages",
                suggestedVerification = "Review detailed salary annexure / CTC breakdown sheet.",
                confidence = 0.99f
            ),
            ClauseEntity(
                id = "cl_a_term",
                documentId = DEMO_DOC_A_ID,
                clauseType = "Notice Period & Termination",
                originalText = "Either party may terminate this Agreement by providing 30 days prior written notice to the other party. The Company reserves the right to terminate the Employee's employment immediately without notice in the event of gross misconduct, willful insubordination, or material breach of this Agreement.",
                pageNumber = 1,
                sectionReference = "Clause 3",
                plainExplanation = "Both employer and employee can terminate with 30 days written notice. Immediate dismissal is permitted only for severe misconduct or material breach.",
                obligation = "30 days prior written notice required by either party.",
                attentionCategory = "Review",
                potentialConcern = "Does not specify payment in lieu of notice for the employee.",
                relevantLegalConcept = "Termination of Employment and Natural Justice Principles",
                suggestedVerification = "Check whether company allows salary in lieu of notice period.",
                confidence = 0.95f
            ),
            ClauseEntity(
                id = "cl_a_damages",
                documentId = DEMO_DOC_A_ID,
                clauseType = "Liquidated Damages & Lock-in",
                originalText = "The Employee agrees to serve the Company for a minimum commitment period of 12 months from the date of joining. In the event the Employee departs prior to completion of the 12-month period, the Employee shall pay to the Company a fixed sum of INR 5,00,000 (Five Lakhs Rupees) as liquidated damages for training and operational disruption.",
                pageNumber = 1,
                sectionReference = "Clause 4",
                plainExplanation = "Requires a 12-month service commitment. Leaving early triggers a demand for INR 5,00,000 in liquidated damages.",
                obligation = "Employee is bound for 12 months or faces INR 5,00,000 penalty claim.",
                attentionCategory = "Professional review recommended",
                potentialConcern = "Under Section 74 of the Indian Contract Act and Kailash Nath v. DDA (2015), pre-fixed sums cannot be enforced as a penalty. The employer must demonstrate actual loss incurred.",
                relevantLegalConcept = "Section 74 Indian Contract Act, Liquidated Damages vs Penalty",
                suggestedVerification = "Verify if company actually incurs documented specialized training expenses matching this amount.",
                confidence = 0.94f
            ),
            ClauseEntity(
                id = "cl_a_noncompete",
                documentId = DEMO_DOC_A_ID,
                clauseType = "Restrictive Covenant & Non-Compete",
                originalText = "During the term of employment and for a period of 24 months following the termination of employment for any reason whatsoever, the Employee shall not directly or indirectly engage in, advise, invest in, or be employed by any entity operating a competing enterprise in the territory of India.",
                pageNumber = 1,
                sectionReference = "Clause 5",
                plainExplanation = "Prohibits working for any competitor anywhere in India for 2 years after leaving the company.",
                obligation = "Negative covenant prohibiting competing employment for 24 months post-exit.",
                attentionCategory = "Professional review recommended",
                potentialConcern = "Void under Section 27 of the Indian Contract Act, 1872. The Supreme Court in Percept D'Mark v. Zaheer Khan (2006) and Superintendence v. Krishan Murgai confirmed that post-employment non-compete covenants are completely void in India.",
                relevantLegalConcept = "Section 27 Indian Contract Act (Restraint of Trade)",
                suggestedVerification = "Consult legal counsel regarding enforceability under Indian law.",
                confidence = 0.99f
            ),
            ClauseEntity(
                id = "cl_a_arbitration",
                documentId = DEMO_DOC_A_ID,
                clauseType = "Dispute Resolution & Arbitration",
                originalText = "Any dispute, controversy, or claim arising out of or relating to this Agreement shall be settled by binding arbitration in accordance with the Arbitration and Conciliation Act, 1996. The seat and venue of arbitration shall be Bangalore, Karnataka.",
                pageNumber = 1,
                sectionReference = "Clause 9",
                plainExplanation = "Legal disputes will be handled through private arbitration in Bangalore under the 1996 Act rather than regular civil courts.",
                obligation = "Parties must arbitrate before filing court suits.",
                attentionCategory = "Informational",
                potentialConcern = "Arbitration costs can be substantial for an individual employee unless paid by employer.",
                relevantLegalConcept = "Section 7 Arbitration and Conciliation Act, 1996",
                suggestedVerification = "Confirm fee allocation rules for arbitration proceedings.",
                confidence = 0.96f
            )
        )
    }

    fun getClausesForDocB(): List<ClauseEntity> {
        return listOf(
            ClauseEntity(
                id = "cl_b_commence",
                documentId = DEMO_DOC_B_ID,
                clauseType = "Appointment & Probation",
                originalText = "The Company hereby employs the Employee as Principal Systems Architect, and the Employee accepts employment commencing on 1st February 2026. The initial probationary period shall be for a duration of 3 months from the date of joining.",
                pageNumber = 1,
                sectionReference = "Clause 1",
                plainExplanation = "Defines role and probation period of 3 months.",
                obligation = "Employee must report on Feb 1, 2026 and complete probation.",
                attentionCategory = "Informational",
                potentialConcern = "None observed.",
                relevantLegalConcept = "Probation principles",
                suggestedVerification = "Standard clause verification.",
                confidence = 0.98f
            ),
            ClauseEntity(
                id = "cl_b_comp",
                documentId = DEMO_DOC_B_ID,
                clauseType = "Compensation & Remuneration",
                originalText = "The Employee shall be entitled to an increased annual gross compensation of INR 32,00,000 (Thirty-Two Lakhs Rupees only). The salary shall be payable monthly on or before the 1st day of each calendar month.",
                pageNumber = 1,
                sectionReference = "Clause 2",
                plainExplanation = "Increases gross CTC to INR 32 Lakhs/annum.",
                obligation = "Company must pay by the 1st of each month.",
                attentionCategory = "Informational",
                potentialConcern = "Verify revised variable pay ratio.",
                relevantLegalConcept = "Code on Wages",
                suggestedVerification = "Review updated salary annexure.",
                confidence = 0.99f
            ),
            ClauseEntity(
                id = "cl_b_term",
                documentId = DEMO_DOC_B_ID,
                clauseType = "Notice Period & Termination",
                originalText = "Either party may terminate this Agreement by providing 7 days prior written notice to the other party. The Company reserves the right to terminate immediately for convenience.",
                pageNumber = 1,
                sectionReference = "Clause 3",
                plainExplanation = "Drastically reduces notice period from 30 days to 7 days, giving company immediate termination rights.",
                obligation = "7 days notice.",
                attentionCategory = "Important review",
                potentialConcern = "Unusually short notice period; immediate dismissal for convenience is highly one-sided.",
                relevantLegalConcept = "Wrongful Termination & Reasonable Notice Doctrine",
                suggestedVerification = "Negotiate bilateral 30 to 60 day notice period.",
                confidence = 0.96f
            ),
            ClauseEntity(
                id = "cl_b_damages",
                documentId = DEMO_DOC_B_ID,
                clauseType = "Liquidated Damages & Lock-in",
                originalText = "The Employee agrees to serve the Company for a minimum commitment period of 18 months. Early exit triggers an enhanced penalty of INR 8,00,000.",
                pageNumber = 1,
                sectionReference = "Clause 4",
                plainExplanation = "Increases lock-in commitment to 18 months and damages demand to INR 8 Lakhs.",
                obligation = "18-month lock-in with INR 8 Lakhs damages claim.",
                attentionCategory = "Professional review recommended",
                potentialConcern = "Enhanced penalty vulnerable under Section 74 Indian Contract Act.",
                relevantLegalConcept = "Section 74 Indian Contract Act",
                suggestedVerification = "Consult counsel regarding enforceability of increased penalty.",
                confidence = 0.95f
            ),
            ClauseEntity(
                id = "cl_b_dpdp",
                documentId = DEMO_DOC_B_ID,
                clauseType = "Data Protection & Privacy",
                originalText = "The Employee agrees to handle all personal data in strict compliance with the Digital Personal Data Protection Act, 2023 (DPDP Act) and shall serve as an internal data fiduciary liaison.",
                pageNumber = 1,
                sectionReference = "Clause 10",
                plainExplanation = "New clause incorporating DPDP Act, 2023 obligations.",
                obligation = "Comply with DPDP Act, 2023.",
                attentionCategory = "Review",
                potentialConcern = "Places individual data fiduciary duties onto the employee.",
                relevantLegalConcept = "DPDP Act, 2023",
                suggestedVerification = "Clarify scope of data fiduciary accountability.",
                confidence = 0.97f
            )
        )
    }

    fun getTimelineForDocA(): List<TimelineEventEntity> {
        return listOf(
            TimelineEventEntity(
                id = "tm_a_exec",
                documentId = DEMO_DOC_A_ID,
                clauseId = null,
                dateStr = "15 January 2026",
                eventDescription = "Execution date of Employment and Confidentiality Agreement",
                eventType = "Contract Execution",
                pageNumber = 1,
                confidence = 0.99f,
                isManualOverride = false
            ),
            TimelineEventEntity(
                id = "tm_a_commence",
                documentId = DEMO_DOC_A_ID,
                clauseId = "cl_a_commence",
                dateStr = "01 February 2026",
                eventDescription = "Effective employment commencement date as Principal Systems Architect",
                eventType = "Effective Date",
                pageNumber = 1,
                confidence = 0.98f,
                isManualOverride = false
            ),
            TimelineEventEntity(
                id = "tm_a_probation",
                documentId = DEMO_DOC_A_ID,
                clauseId = "cl_a_commence",
                dateStr = "01 May 2026",
                eventDescription = "Completion of 3-month probationary evaluation period",
                eventType = "Milestone",
                pageNumber = 1,
                confidence = 0.95f,
                isManualOverride = false
            ),
            TimelineEventEntity(
                id = "tm_a_pay",
                documentId = DEMO_DOC_A_ID,
                clauseId = "cl_a_comp",
                dateStr = "Monthly (by 5th)",
                eventDescription = "Recurring salary disbursement into designated employee account",
                eventType = "Payment",
                pageNumber = 1,
                confidence = 0.97f,
                isManualOverride = false
            ),
            TimelineEventEntity(
                id = "tm_a_lockin",
                documentId = DEMO_DOC_A_ID,
                clauseId = "cl_a_damages",
                dateStr = "01 February 2027",
                eventDescription = "Expiry of 12-month minimum commitment period and liquidated damages bond",
                eventType = "Deadline",
                pageNumber = 1,
                confidence = 0.96f,
                isManualOverride = false
            )
        )
    }
}
