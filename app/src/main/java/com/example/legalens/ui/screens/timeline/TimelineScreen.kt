package com.example.legalens.ui.screens.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.legalens.data.model.TimelineEventEntity
import com.example.legalens.data.repository.DocumentRepository
import com.example.legalens.ui.components.StatusBadge
import com.example.legalens.ui.theme.AlertAmber
import com.example.legalens.ui.theme.BackgroundDark
import com.example.legalens.ui.theme.BorderLine
import com.example.legalens.ui.theme.EmeraldLight
import com.example.legalens.ui.theme.EmeraldPrimary
import com.example.legalens.ui.theme.SurfaceDark
import com.example.legalens.ui.theme.SurfaceVariantDark
import com.example.legalens.ui.theme.TextMuted
import com.example.legalens.ui.theme.TextPrimary
import com.example.legalens.ui.theme.TextSecondary

@Composable
fun TimelineScreen(
    documentRepository: DocumentRepository
) {
    val events by documentRepository.getAllTimelineEvents().collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Contractual Timeline & Deadlines",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Chronological contractual milestones, lock-in commitments, and recurring obligations.",
                color = TextMuted,
                fontSize = 12.sp
            )
        }

        items(events) { event ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("timeline_item_${event.id}"),
                verticalAlignment = Alignment.Top
            ) {
                // Timeline left pillar
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(36.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                when (event.eventType) {
                                    "Effective Date" -> EmeraldPrimary
                                    "Deadline" -> AlertAmber
                                    "Payment" -> Color(0xFF60A5FA)
                                    else -> Color(0xFFA78BFA)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (event.eventType) {
                                "Payment" -> Icons.Default.Payment
                                "Deadline" -> Icons.Default.Schedule
                                else -> Icons.Default.Flag
                            },
                            contentDescription = null,
                            tint = Color(0xFF042017),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(60.dp)
                            .background(BorderLine)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Card details
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLine),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = event.dateStr,
                                color = EmeraldLight,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            StatusBadge(text = event.eventType, type = "info")
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = event.eventDescription,
                            color = TextPrimary,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Confidence: ${(event.confidence * 100).toInt()}% • Page ${event.pageNumber}",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}
