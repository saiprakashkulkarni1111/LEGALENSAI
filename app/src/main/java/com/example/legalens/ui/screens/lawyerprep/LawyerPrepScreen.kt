package com.example.legalens.ui.screens.lawyerprep

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.legalens.data.model.LawyerPrepPack
import com.example.legalens.data.repository.DocumentRepository
import com.example.legalens.data.seed.SeedData
import com.example.legalens.ui.components.StatusBadge
import com.example.legalens.ui.theme.AlertAmber
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
fun LawyerPrepScreen(
    initialDocumentId: String? = null,
    documentRepository: DocumentRepository
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val documents by documentRepository.getDocuments().collectAsState(initial = emptyList())

    var selectedDocId by remember {
        mutableStateOf(initialDocumentId ?: SeedData.DEMO_DOC_A_ID)
    }
    var prepPack by remember { mutableStateOf<LawyerPrepPack?>(null) }
    var expandedDropdown by remember { mutableStateOf(false) }

    fun generateDossier(docId: String) {
        scope.launch {
            prepPack = documentRepository.generateLawyerPrep(docId)
        }
    }

    LaunchedEffect(selectedDocId) {
        generateDossier(selectedDocId)
    }

    val selectedDoc = documents.find { it.id == selectedDocId }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Lawyer Prep Dossier",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Structured executive briefing pack formulated for consultation with legal counsel.",
                color = TextMuted,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Document Selector
            Box {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLine),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedDropdown = true }
                        .testTag("lawyer_prep_doc_selector")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Target Agreement for Consultation", color = TextMuted, fontSize = 10.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = selectedDoc?.title ?: "Select Document",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = EmeraldLight)
                    }
                }

                DropdownMenu(
                    expanded = expandedDropdown,
                    onDismissRequest = { expandedDropdown = false }
                ) {
                    documents.forEach { doc ->
                        DropdownMenuItem(
                            text = { Text(doc.title, fontSize = 13.sp) },
                            onClick = {
                                selectedDocId = doc.id
                                expandedDropdown = false
                                generateDossier(doc.id)
                            }
                        )
                    }
                }
            }
        }

        val pack = prepPack
        if (pack != null) {
            // Action buttons (Copy / Share)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val dossierText = buildDossierText(pack)
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Legalens Briefing Pack", dossierText)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Dossier copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("lawyer_prep_copy_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            tint = Color(0xFF042017),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Dossier", color = Color(0xFF042017), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            val dossierText = buildDossierText(pack)
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, dossierText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Briefing Pack"))
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("lawyer_prep_share_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = EmeraldLight,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export & Share", color = EmeraldLight, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                }
            }

            // Executive Summary
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLine),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            StatusBadge(text = "EXECUTIVE BRIEF", type = "success")
                            Text(text = pack.preparedAt, color = TextMuted, fontSize = 11.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = pack.executiveSummary,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            lineHeight = 19.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Key Verified Facts",
                            color = EmeraldLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        pack.keyFacts.forEach { fact ->
                            Text(
                                text = "• $fact",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(vertical = 1.dp)
                            )
                        }
                    }
                }
            }

            // Targeted Questions for Counsel
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLine),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Targeted Questions for Legal Counsel",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        pack.questionsForCounsel.forEachIndexed { i, q ->
                            Row(
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${i + 1}.",
                                    color = EmeraldLight,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(20.dp)
                                )
                                Text(
                                    text = q,
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }

            // Flagged Matters for Review
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLine),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Specific Clauses Flagged for Scrutiny",
                            color = AlertAmber,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        pack.mattersForReview.forEach { matter ->
                            Row(
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier.padding(vertical = 3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = AlertAmber,
                                    modifier = Modifier
                                        .size(14.dp)
                                        .padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = matter,
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }

            // Missing Documents & Ambiguities Checklist
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLine),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Document Checklist for Meeting",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        pack.missingInformation.forEach { item ->
                            Text(
                                text = "☐ $item",
                                color = TextMuted,
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Legal Disclaimer
            item {
                Text(
                    text = pack.legalDisclaimer,
                    color = TextMuted,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

private fun buildDossierText(pack: LawyerPrepPack): String {
    return """
LEGALENS COUNSEL BRIEFING DOSSIER
Prepared: ${pack.preparedAt}
Document: ${pack.documentTitle}

1. EXECUTIVE SUMMARY
${pack.executiveSummary}

2. VERIFIED FACTS
${pack.keyFacts.joinToString("\n") { "• $it" }}

3. MATTERS FLAGGED FOR LEGAL REVIEW
${pack.mattersForReview.joinToString("\n") { "• $it" }}

4. QUESTIONS FOR COUNSEL
${pack.questionsForCounsel.mapIndexed { i, q -> "${i + 1}. $q" }.joinToString("\n")}

5. MISSING INFORMATION & ANNEXURES
${pack.missingInformation.joinToString("\n") { "[ ] $it" }}

DISCLAIMER:
${pack.legalDisclaimer}
    """.trimIndent()
}
