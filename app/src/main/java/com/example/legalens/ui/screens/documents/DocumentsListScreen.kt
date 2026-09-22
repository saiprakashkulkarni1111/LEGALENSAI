package com.example.legalens.ui.screens.documents

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.legalens.data.model.DocumentEntity
import com.example.legalens.data.repository.DocumentRepository
import com.example.legalens.data.security.PiiSanitizer
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
fun DocumentsListScreen(
    documentRepository: DocumentRepository,
    onOpenDocument: (String) -> Unit
) {
    val documents by documentRepository.getDocuments().collectAsState(initial = emptyList())
    var showUploadDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showUploadDialog = true },
                containerColor = EmeraldPrimary,
                contentColor = Color(0xFF042017),
                modifier = Modifier.testTag("upload_document_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Document")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Analyze Contract", fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = BackgroundDark
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "Contract Intelligence Vault",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${documents.size} contracts analyzed • Automatic PII redaction active",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            items(documents) { doc ->
                DocumentItemCard(
                    doc = doc,
                    onClick = { onOpenDocument(doc.id) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(70.dp))
            }
        }
    }

    if (showUploadDialog) {
        UploadDocumentDialog(
            onDismiss = { showUploadDialog = false },
            onAnalyze = { title, text, isDemo ->
                scope.launch {
                    val newId = documentRepository.uploadAndAnalyze(title, text, isDemo)
                    showUploadDialog = false
                    onOpenDocument(newId)
                }
            }
        )
    }
}

@Composable
fun DocumentItemCard(
    doc: DocumentEntity,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLine),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("document_card_${doc.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                StatusBadge(
                    text = if (doc.mode == "SYNTHETIC_DEMO") "SYNTHETIC DEMO" else "REAL DOCUMENT",
                    type = if (doc.mode == "SYNTHETIC_DEMO") "info" else "success"
                )

                if (doc.piiRedacted) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "PII Sanitized",
                            tint = EmeraldLight,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "PII Sanitized",
                            color = EmeraldLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = doc.title,
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = doc.summary,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${doc.totalClauses} clauses • ${doc.totalObligations} obligations",
                    color = TextMuted,
                    fontSize = 12.sp
                )

                if (doc.itemsRequiringReview > 0) {
                    StatusBadge(
                        text = "${doc.itemsRequiringReview} REQUIRE REVIEW",
                        type = "warning"
                    )
                } else {
                    StatusBadge(text = "CLEAR", type = "success")
                }
            }
        }
    }
}

@Composable
fun UploadDocumentDialog(
    onDismiss: () -> Unit,
    onAnalyze: (title: String, text: String, isDemo: Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }

    val piiPreview = remember(text) {
        PiiSanitizer.sanitize(text)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Analyze Contract",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column {
                Text(
                    text = "Paste contractual text below. The system automatically redacts Indian PII (PAN, Aadhaar, bank accounts, phones) before analysis.",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Contract Title") },
                    placeholder = { Text("e.g. Master Consulting Agreement") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceVariantDark,
                        unfocusedContainerColor = SurfaceVariantDark,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("upload_title_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Contract Clauses / Full Text") },
                    placeholder = { Text("Paste clauses (Non-Compete, Notice Period, Liquidated Damages)...") },
                    minLines = 6,
                    maxLines = 10,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceVariantDark,
                        unfocusedContainerColor = SurfaceVariantDark,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("upload_text_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // PII redaction live feedback
                if (piiPreview.redactionCount > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(EmeraldContainer.copy(alpha = 0.4f))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "🛡️ ${piiPreview.redactionCount} PII elements detected & will be redacted: ${piiPreview.detectedTypes.joinToString(", ")}",
                            color = EmeraldLight,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && text.isNotBlank()) {
                        onAnalyze(title, text, false)
                    }
                },
                enabled = title.isNotBlank() && text.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                modifier = Modifier.testTag("confirm_analyze_button")
            ) {
                Text("Analyze Document", color = Color(0xFF042017), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = SurfaceDark
    )
}
