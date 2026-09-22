package com.example.legalens.ui.screens.compare

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.legalens.data.model.CompareResult
import com.example.legalens.data.model.DiffItem
import com.example.legalens.data.model.DocumentEntity
import com.example.legalens.data.repository.DocumentRepository
import com.example.legalens.data.seed.SeedData
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
import kotlinx.coroutines.launch

@Composable
fun CompareScreen(
    documentRepository: DocumentRepository
) {
    val documents by documentRepository.getDocuments().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    var selectedDocAId by remember { mutableStateOf(SeedData.DEMO_DOC_A_ID) }
    var selectedDocBId by remember { mutableStateOf(SeedData.DEMO_DOC_B_ID) }
    var compareResult by remember { mutableStateOf<CompareResult?>(null) }

    var expandedMenuA by remember { mutableStateOf(false) }
    var expandedMenuB by remember { mutableStateOf(false) }

    fun runComparison(idA: String, idB: String) {
        scope.launch {
            compareResult = documentRepository.compareDocuments(idA, idB)
        }
    }

    LaunchedEffect(documents) {
        if (documents.size >= 2) {
            runComparison(selectedDocAId, selectedDocBId)
        }
    }

    val docA = documents.find { it.id == selectedDocAId }
    val docB = documents.find { it.id == selectedDocBId }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Document Comparison & Redline",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Clause-by-clause delta analysis identifying added, omitted, and altered contractual terms.",
                color = TextMuted,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Selector row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Doc A Selector
                Box(modifier = Modifier.weight(1f)) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLine),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedMenuA = true }
                            .testTag("compare_doc_a_dropdown")
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Base Document (A)", color = TextMuted, fontSize = 10.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = docA?.title ?: "Select Doc A",
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextSecondary)
                            }
                        }
                    }

                    DropdownMenu(
                        expanded = expandedMenuA,
                        onDismissRequest = { expandedMenuA = false }
                    ) {
                        documents.forEach { d ->
                            DropdownMenuItem(
                                text = { Text(d.title, fontSize = 13.sp) },
                                onClick = {
                                    selectedDocAId = d.id
                                    expandedMenuA = false
                                    runComparison(d.id, selectedDocBId)
                                }
                            )
                        }
                    }
                }

                Icon(
                    imageVector = Icons.Default.CompareArrows,
                    contentDescription = null,
                    tint = EmeraldLight,
                    modifier = Modifier.size(24.dp)
                )

                // Doc B Selector
                Box(modifier = Modifier.weight(1f)) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLine),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedMenuB = true }
                            .testTag("compare_doc_b_dropdown")
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Revised Document (B)", color = TextMuted, fontSize = 10.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = docB?.title ?: "Select Doc B",
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextSecondary)
                            }
                        }
                    }

                    DropdownMenu(
                        expanded = expandedMenuB,
                        onDismissRequest = { expandedMenuB = false }
                    ) {
                        documents.forEach { d ->
                            DropdownMenuItem(
                                text = { Text(d.title, fontSize = 13.sp) },
                                onClick = {
                                    selectedDocBId = d.id
                                    expandedMenuB = false
                                    runComparison(selectedDocAId, d.id)
                                }
                            )
                        }
                    }
                }
            }
        }

        val res = compareResult
        if (res != null) {
            // Summary metrics
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatBox(
                        label = "Differences",
                        count = "${res.totalDifferences}",
                        color = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    StatBox(
                        label = "Added",
                        count = "+${res.addedCount}",
                        color = EmeraldLight,
                        modifier = Modifier.weight(1f)
                    )
                    StatBox(
                        label = "Removed",
                        count = "-${res.removedCount}",
                        color = AlertRed,
                        modifier = Modifier.weight(1f)
                    )
                    StatBox(
                        label = "Modified",
                        count = "${res.modifiedCount}",
                        color = AlertAmber,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Diffs list
            item {
                Text(
                    text = "Clause Redline Breakdown",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            items(res.diffs) { diff ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLine),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = diff.clauseType,
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            StatusBadge(
                                text = diff.changeType,
                                type = when (diff.changeType) {
                                    "ADDED" -> "success"
                                    "REMOVED" -> "error"
                                    else -> "warning"
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = diff.deltaSummary,
                            color = TextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Legal significance
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(SurfaceVariantDark)
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(
                                    text = "LEGAL SIGNIFICANCE",
                                    color = EmeraldLight,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = diff.potentialSignificance,
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }

                        // Text comparison snippet if modified
                        if (diff.docAText != null && diff.docBText != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Original: ${diff.docAText}",
                                    color = TextMuted,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 2
                                )
                                Text(
                                    text = "Revised: ${diff.docBText}",
                                    color = EmeraldLight,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatBox(
    label: String,
    count: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceDark)
            .border(1.dp, BorderLine, RoundedCornerShape(8.dp))
            .padding(vertical = 10.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = count, color = color, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = label, color = TextMuted, fontSize = 10.sp)
        }
    }
}
