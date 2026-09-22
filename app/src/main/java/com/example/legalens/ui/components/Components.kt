package com.example.legalens.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.legalens.data.model.EvidenceItem
import com.example.legalens.ui.theme.AlertAmber
import com.example.legalens.ui.theme.AlertBlue
import com.example.legalens.ui.theme.AlertRed
import com.example.legalens.ui.theme.BorderLine
import com.example.legalens.ui.theme.EmeraldContainer
import com.example.legalens.ui.theme.EmeraldLight
import com.example.legalens.ui.theme.EmeraldPrimary
import com.example.legalens.ui.theme.OnEmeraldContainer
import com.example.legalens.ui.theme.SurfaceDark
import com.example.legalens.ui.theme.SurfaceVariantDark
import com.example.legalens.ui.theme.TextMuted
import com.example.legalens.ui.theme.TextPrimary
import com.example.legalens.ui.theme.TextSecondary

@Composable
fun StatusBadge(
    text: String,
    type: String = "info", // "success", "warning", "error", "info", "gold"
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, borderColor) = when (type.lowercase()) {
        "success", "live", "tier 1", "verified" -> Triple(
            Color(0xFF064E3B).copy(alpha = 0.6f),
            EmeraldLight,
            EmeraldPrimary.copy(alpha = 0.5f)
        )
        "warning", "review", "important review", "recent" -> Triple(
            Color(0xFF78350F).copy(alpha = 0.6f),
            Color(0xFFFCD34D),
            AlertAmber.copy(alpha = 0.5f)
        )
        "error", "professional review recommended", "void" -> Triple(
            Color(0xFF7F1D1D).copy(alpha = 0.6f),
            Color(0xFFFCA5A5),
            AlertRed.copy(alpha = 0.5f)
        )
        "gold", "landmark" -> Triple(
            Color(0xFF451A03).copy(alpha = 0.6f),
            Color(0xFFFDE047),
            Color(0xFFEAB308).copy(alpha = 0.5f)
        )
        else -> Triple(
            SurfaceVariantDark,
            TextSecondary,
            BorderLine
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color = EmeraldPrimary,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLine),
        modifier = modifier
            .testTag("metric_card_${title.lowercase().replace(" ", "_")}")
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = TextMuted,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun EvidenceCard(
    evidence: EvidenceItem,
    modifier: Modifier = Modifier,
    onOpenUrl: ((String) -> Unit)? = null
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLine),
        modifier = modifier
            .fillMaxWidth()
            .testTag("evidence_card_${evidence.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                StatusBadge(text = evidence.authorityTier, type = "success")
                Text(
                    text = evidence.effectivePeriod,
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = evidence.sourceTitle,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = evidence.sectionOrPara,
                color = EmeraldLight,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Verbatim official text box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceVariantDark)
                    .border(1.dp, BorderLine, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = "\"${evidence.exactText}\"",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Authority: ${evidence.authority}",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    modifier = Modifier.weight(1f)
                )

                if (onOpenUrl != null && evidence.officialUrl.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { onOpenUrl(evidence.officialUrl) }
                            .padding(4.dp)
                    ) {
                        Text(
                            text = "Official Portal",
                            color = EmeraldLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = "Open portal",
                            tint = EmeraldLight,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
