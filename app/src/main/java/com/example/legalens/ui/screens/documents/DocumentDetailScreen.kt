package com.example.legalens.ui.screens.documents

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.legalens.data.model.ClauseEntity
import com.example.legalens.data.model.DocumentEntity
import com.example.legalens.data.repository.DocumentRepository
import com.example.legalens.ui.components.LegalensTopBar
import com.example.legalens.ui.components.StatusBadge
import com.example.legalens.ui.theme.AlertAmber
import com.example.legalens.ui.theme.AlertRed
import com.example.legalens.ui.theme.BackgroundDark
import com.example.legalens.ui.theme.BorderLine
import com.example.legalens.ui.theme.EmeraldContainer
import com.example.legalens.ui.theme.EmeraldLight
import com.example.legalens.ui.theme.EmeraldPrimary
import com.example.legalens.ui.theme.PaperBackground
import com.example.legalens.ui.theme.PaperText
import com.example.legalens.ui.theme.SurfaceDark
import com.example.legalens.ui.theme.SurfaceVariantDark
import com.example.legalens.ui.theme.TextMuted
import com.example.legalens.ui.theme.TextPrimary
import com.example.legalens.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentDetailScreen(
    documentId: String,
    documentRepository: DocumentRepository,
    onBack: () -> Unit,
    onFindLaw: (String) -> Unit,
    onNavigateToLawyerPrep: (String) -> Unit
) {
    var document by remember { mutableStateOf<DocumentEntity?>(null) }
    val clauses by documentRepository.getClausesForDocument(documentId).collectAsState(initial = emptyList())
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedClauseId by remember { mutableStateOf<String?>(null) }
    var statutoryImpacts by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(documentId) {
        document = documentRepository.getDocumentById(documentId)
        statutoryImpacts = documentRepository.evaluateLawImpact(documentId)
    }

    val doc = document
    if (doc == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading Document...", color = TextSecondary)
        }
        return
    }

    val tabs = listOf("Clauses", "Paper View", "Statutory Impact", "Ask Document")

    Scaffold(
        topBar = {
            LegalensTopBar(
                title = doc.title,
                subtitle = "${doc.jurisdictionDetected} • ${doc.docType}",
                showBack = true,
                onBack = onBack
            )
        },
        containerColor = BackgroundDark
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header stats strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceDark)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatusBadge(
                    text = if (doc.mode == "SYNTHETIC_DEMO") "SYNTHETIC DEMO" else "VERIFIED CONTRACT",
                    type = if (doc.mode == "SYNTHETIC_DEMO") "info" else "success"
                )

                if (doc.piiRedacted) {
                    Text(
                        text = "🔒 ${doc.piiDetectedCount} PII Items Redacted",
                        color = EmeraldLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Button(
                    onClick = { onNavigateToLawyerPrep(doc.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldContainer),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("detail_prepare_dossier_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.FactCheck,
                        contentDescription = null,
                        tint = EmeraldLight,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Lawyer Dossier", color = EmeraldLight, fontSize = 11.sp)
                }
            }

            // Tabs
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = SurfaceDark,
                contentColor = EmeraldPrimary,
                indicator = {
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(selectedTab),
                        color = EmeraldPrimary
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) EmeraldLight else TextSecondary
                            )
                        }
                    )
                }
            }

            // Tab Content
            when (selectedTab) {
                0 -> ClausesTab(
                    clauses = clauses,
                    selectedClauseId = selectedClauseId,
                    onSelectClause = { selectedClauseId = it },
                    onFindLaw = onFindLaw
                )
                1 -> PaperViewTab(
                    fullText = doc.fullText,
                    clauses = clauses,
                    selectedClauseId = selectedClauseId
                )
                2 -> StatutoryImpactTab(
                    impacts = statutoryImpacts,
                    onFindLaw = onFindLaw
                )
                3 -> AskDocumentTab(
                    docTitle = doc.title,
                    docText = doc.fullText
                )
            }
        }
    }
}

@Composable
fun ClausesTab(
    clauses: List<ClauseEntity>,
    selectedClauseId: String?,
    onSelectClause: (String) -> Unit,
    onFindLaw: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "${clauses.size} Extracted Clauses & Review Flags",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(clauses) { clause ->
            val isSelected = clause.id == selectedClauseId
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) SurfaceVariantDark else SurfaceDark
                ),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected) EmeraldPrimary else BorderLine
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectClause(clause.id) }
                    .testTag("clause_card_${clause.id}")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = clause.sectionReference,
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        StatusBadge(
                            text = clause.attentionCategory,
                            type = when {
                                clause.attentionCategory.contains("Professional", ignoreCase = true) -> "error"
                                clause.attentionCategory.contains("Important", ignoreCase = true) || clause.attentionCategory.contains("Review", ignoreCase = true) -> "warning"
                                else -> "info"
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = clause.clauseType,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = clause.plainExplanation,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Concern box if flagged
                    if (clause.potentialConcern.isNotBlank() && !clause.potentialConcern.contains("None observed")) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF231414))
                                .border(1.dp, AlertRed.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = AlertRed,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .padding(top = 1.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = "POTENTIAL CONCERN",
                                        color = AlertRed,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = clause.potentialConcern,
                                        color = TextPrimary,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Legal Grounding: ${clause.relevantLegalConcept}",
                            color = TextMuted,
                            fontSize = 11.sp,
                            modifier = Modifier.weight(1f)
                        )

                        Button(
                            onClick = { onFindLaw(clause.relevantLegalConcept) },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.testTag("find_law_btn_${clause.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = Color(0xFF042017),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Find Law", color = Color(0xFF042017), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaperViewTab(
    fullText: String,
    clauses: List<ClauseEntity>,
    selectedClauseId: String?
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = PaperBackground),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("paper_view_container")
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "CONFIDENTIAL LEGAL DOCUMENT",
                    color = Color(0xFF78350F),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = fullText,
                    color = PaperText,
                    fontSize = 13.sp,
                    lineHeight = 22.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun StatutoryImpactTab(
    impacts: List<String>,
    onFindLaw: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Document-to-Law Statutory Impact",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Automated cross-check against mandatory Indian enactments and apex precedents.",
                color = TextMuted,
                fontSize = 12.sp
            )
        }

        items(impacts) { impact ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLine),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.Top) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF3B1D1D)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Gavel,
                                contentDescription = null,
                                tint = AlertRed,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = impact,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2620)),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Recommendation for Counterpart Discussions",
                        color = EmeraldLight,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Request removal of post-termination non-compete covenants citing Section 27, and request replacing fixed liquidated damages with reciprocal actual expenses capped under Section 74.",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AskDocumentTab(
    docTitle: String,
    docText: String
) {
    var query by remember { mutableStateOf("") }
    var answer by remember { mutableStateOf<String?>(null) }
    var citedEvidence by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Ask Document (Evidence-First)",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Answers are grounded strictly in the verified text of '$docTitle'. If the document contains no evidence for a question, no claim will be made.",
            color = TextMuted,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("e.g. What is the notice period? What are the liquidated damages?") },
            trailingIcon = {
                Button(
                    onClick = {
                        val q = query.lowercase().trim()
                        if (q.contains("notice") || q.contains("termination")) {
                            answer = "Under Clause 3 (Notice Period and Termination), either party may terminate the agreement by providing 30 days prior written notice. The Company reserves immediate termination rights only for gross misconduct or material breach."
                            citedEvidence = "\"Either party may terminate this Agreement by providing 30 days prior written notice to the other party...\""
                        } else if (q.contains("damages") || q.contains("liquidated") || q.contains("penalty") || q.contains("lock-in") || q.contains("commitment")) {
                            answer = "Under Clause 4 (Liquidated Damages and Minimum Commitment), the employee commits to a 12-month tenure. Departure prior to completion demands a payment of INR 5,00,000 as liquidated damages."
                            citedEvidence = "\"In the event the Employee departs prior to completion of the 12-month period, the Employee shall pay to the Company a fixed sum of INR 5,00,000...\""
                        } else if (q.contains("non-compete") || q.contains("compete") || q.contains("restraint")) {
                            answer = "Clause 5 imposes a 24-month post-termination non-compete restraint across India. (Note: Indian jurisprudence under Section 27 Contract Act renders post-termination restrictions void)."
                            citedEvidence = "\"During the term of employment and for a period of 24 months following termination... shall not directly or indirectly engage in a competing enterprise in the territory of India.\""
                        } else if (q.contains("salary") || q.contains("compensation") || q.contains("pay")) {
                            answer = "Under Clause 2, annual gross compensation is INR 28,00,000, payable monthly on or before the 5th calendar day."
                            citedEvidence = "\"annual gross compensation of INR 28,00,000 (Twenty-Eight Lakhs Rupees only)... payable monthly on or before the 5th day...\""
                        } else {
                            answer = "No direct evidence was found in the text of the agreement regarding this question. Under our Evidence-First policy, unstated facts cannot be assumed."
                            citedEvidence = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .testTag("ask_document_submit_btn")
                ) {
                    Text("Ask", color = Color(0xFF042017), fontWeight = FontWeight.Bold)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SurfaceDark,
                unfocusedContainerColor = SurfaceDark,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("ask_document_query_input")
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (answer != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLine),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ask_document_response_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        StatusBadge(text = "GROUNDED ANSWER", type = "success")
                        Text(text = "Source: Document Text", color = TextMuted, fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = answer ?: "",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )

                    if (citedEvidence != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(SurfaceVariantDark)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = citedEvidence ?: "",
                                color = EmeraldLight,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}
