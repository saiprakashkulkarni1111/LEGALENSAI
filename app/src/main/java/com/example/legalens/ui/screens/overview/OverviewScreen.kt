package com.example.legalens.ui.screens.overview

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.legalens.data.model.DocumentEntity
import com.example.legalens.data.model.SourceHealthItem
import com.example.legalens.data.model.TimelineEventEntity
import com.example.legalens.data.repository.DocumentRepository
import com.example.legalens.data.repository.LegalRepository
import com.example.legalens.ui.components.MetricCard
import com.example.legalens.ui.components.StatusBadge
import com.example.legalens.ui.theme.AlertAmber
import com.example.legalens.ui.theme.AlertRed
import com.example.legalens.ui.theme.BackgroundDark
import com.example.legalens.ui.theme.BorderLine
import com.example.legalens.ui.theme.EmeraldContainer
import com.example.legalens.ui.theme.EmeraldLight
import com.example.legalens.ui.theme.EmeraldPrimary
import com.example.legalens.ui.theme.SurfaceDark
import com.example.legalens.ui.theme.SurfaceVariantDark
import com.example.legalens.ui.theme.TextMuted
import com.example.legalens.ui.theme.TextPrimary
import com.example.legalens.ui.theme.TextSecondary

@Composable
fun OverviewScreen(
    documentRepository: DocumentRepository,
    legalRepository: LegalRepository,
    onNavigateToDocuments: () -> Unit,
    onNavigateToResearch: (String?) -> Unit,
    onNavigateToCases: () -> Unit,
    onNavigateToCompare: () -> Unit,
    onNavigateToLawyerPrep: () -> Unit,
    onNavigateToGraph: () -> Unit,
    onNavigateToSources: () -> Unit,
    onNavigateToTimeline: () -> Unit,
    onOpenDocument: (String) -> Unit
) {
    val documents by documentRepository.getDocuments().collectAsState(initial = emptyList())
    val timelineEvents by documentRepository.getAllTimelineEvents().collectAsState(initial = emptyList())

    var searchInput by remember { mutableStateOf("") }

    val totalDocs = documents.size
    val totalReviewItems = documents.sumOf { it.itemsRequiringReview }
    val totalDeadlines = timelineEvents.size

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Banner
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = SurfaceDark
                ),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLine),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("overview_hero_banner")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF0F2620),
                                    Color(0xFF13171F)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            StatusBadge(text = "EVIDENCE-FIRST DOCTRINE", type = "success")
                            Text(
                                text = "JURISDICTION: INDIA",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Legal Intelligence with Verifiable Grounding",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 26.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Strict adherence to 'NO EVIDENCE -> NO CLAIM'. Analyze contracts, verify citations against India Code & Supreme Court precedents, and prepare counsel dossiers.",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Quick statutory search input
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = searchInput,
                                onValueChange = { searchInput = it },
                                placeholder = {
                                    Text(
                                        "Search provisions (e.g., Section 27 Contract Act, DPDP Act)",
                                        fontSize = 12.sp,
                                        color = TextMuted
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = EmeraldLight,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = SurfaceVariantDark,
                                    unfocusedContainerColor = SurfaceVariantDark,
                                    focusedBorderColor = EmeraldPrimary,
                                    unfocusedBorderColor = BorderLine,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("overview_quick_search_input")
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    val query = searchInput.ifBlank { "Section 27 Contract Act" }
                                    onNavigateToResearch(query)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = EmeraldPrimary
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("overview_search_button")
                            ) {
                                Text("Research", color = Color(0xFF042017), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Metrics Grid (2x2)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Documents Analyzed",
                    value = "$totalDocs",
                    subtitle = "Contracts in vault",
                    icon = Icons.Default.Description,
                    accentColor = EmeraldPrimary,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToDocuments
                )
                MetricCard(
                    title = "Review Required",
                    value = "$totalReviewItems",
                    subtitle = "Clauses flagged",
                    icon = Icons.Default.Warning,
                    accentColor = AlertAmber,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToDocuments
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Active Deadlines",
                    value = "$totalDeadlines",
                    subtitle = "Extracted milestones",
                    icon = Icons.Default.CalendarToday,
                    accentColor = Color(0xFF60A5FA),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToTimeline
                )
                MetricCard(
                    title = "Legal Sources",
                    value = "5 Active",
                    subtitle = "Live India Code & SC",
                    icon = Icons.Default.Security,
                    accentColor = EmeraldLight,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToSources
                )
            }
        }

        // Quick Navigation Tiles
        item {
            Text(
                text = "Intelligence Workflows",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                WorkflowButton(
                    title = "Analyze Doc",
                    icon = Icons.Default.Assignment,
                    color = EmeraldPrimary,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToDocuments
                )
                WorkflowButton(
                    title = "Research Law",
                    icon = Icons.Default.AccountBalance,
                    color = Color(0xFF38BDF8),
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateToResearch(null) }
                )
                WorkflowButton(
                    title = "Compare Drafts",
                    icon = Icons.Default.CompareArrows,
                    color = Color(0xFFA78BFA),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToCompare
                )
                WorkflowButton(
                    title = "Lawyer Prep",
                    icon = Icons.Default.FactCheck,
                    color = Color(0xFFFBBF24),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToLawyerPrep
                )
            }
        }

        // Recent Analyzed Documents
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Analyzed Contracts",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "View All (${documents.size})",
                    color = EmeraldLight,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onNavigateToDocuments() }
                )
            }
        }

        items(documents.take(3)) { doc ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLine),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenDocument(doc.id) }
                    .testTag("doc_item_${doc.id}")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(EmeraldContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = EmeraldLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = doc.title,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${doc.totalClauses} clauses • ${doc.itemsRequiringReview} flagged • ${doc.jurisdictionDetected}",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    if (doc.itemsRequiringReview > 0) {
                        StatusBadge(text = "${doc.itemsRequiringReview} Review", type = "warning")
                    } else {
                        StatusBadge(text = "Clear", type = "success")
                    }
                }
            }
        }

        // Legal Data Pulse Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLine),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToSources() }
                    .testTag("overview_sources_pulse_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldPrimary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Legal Data Pulse",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Check Status",
                            color = EmeraldLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Authoritative repositories connected: India Code (Central Acts), Supreme Court e-SCR (Landmark Decisions), eCourts Services, High Courts of India.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Responsible AI & Legal Disclaimer
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF161A22))
                    .border(1.dp, BorderLine, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Legalens AI provides legal assistance and statutory document analysis. It does not replace a qualified advocate enrolled with the Bar Council of India, predict judicial decisions, or fabricate citations.",
                        color = TextMuted,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun WorkflowButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLine),
        modifier = modifier
            .clickable { onClick() }
            .testTag("workflow_btn_${title.lowercase().replace(" ", "_")}")
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }
    }
}
