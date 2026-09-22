package com.example.legalens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.legalens.data.local.LegalensDatabase
import com.example.legalens.data.repository.DocumentRepository
import com.example.legalens.data.repository.LegalRepository
import com.example.legalens.ui.components.LegalensTopBar
import com.example.legalens.ui.components.StatusBadge
import com.example.legalens.ui.screens.cases.CaseExplorerScreen
import com.example.legalens.ui.screens.compare.CompareScreen
import com.example.legalens.ui.screens.documents.DocumentDetailScreen
import com.example.legalens.ui.screens.documents.DocumentsListScreen
import com.example.legalens.ui.screens.graph.KnowledgeGraphScreen
import com.example.legalens.ui.screens.lawyerprep.LawyerPrepScreen
import com.example.legalens.ui.screens.overview.OverviewScreen
import com.example.legalens.ui.screens.research.ResearchScreen
import com.example.legalens.ui.screens.sources.SourcesScreen
import com.example.legalens.ui.screens.timeline.TimelineScreen
import com.example.legalens.ui.theme.BackgroundDark
import com.example.legalens.ui.theme.BorderLine
import com.example.legalens.ui.theme.EmeraldContainer
import com.example.legalens.ui.theme.EmeraldLight
import com.example.legalens.ui.theme.EmeraldPrimary
import com.example.legalens.ui.theme.LegalensTheme
import com.example.legalens.ui.theme.SurfaceDark
import com.example.legalens.ui.theme.SurfaceVariantDark
import com.example.legalens.ui.theme.TextMuted
import com.example.legalens.ui.theme.TextPrimary
import com.example.legalens.ui.theme.TextSecondary
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = LegalensDatabase.getInstance(this)
        val documentRepository = DocumentRepository(db.documentDao())
        val legalRepository = LegalRepository(db.legalDao())

        setContent {
            LegalensTheme {
                LegalensApp(
                    documentRepository = documentRepository,
                    legalRepository = legalRepository
                )
            }
        }
    }
}

enum class Screen {
    OVERVIEW,
    DOCUMENTS,
    DOCUMENT_DETAIL,
    RESEARCH,
    COMPARE,
    CASES,
    LAWYER_PREP,
    GRAPH,
    SOURCES,
    TIMELINE,
    MORE_HUB
}

@Composable
fun LegalensApp(
    documentRepository: DocumentRepository,
    legalRepository: LegalRepository
) {
    val scope = rememberCoroutineScope()
    var currentScreen by remember { mutableStateOf(Screen.OVERVIEW) }
    var selectedDocumentId by remember { mutableStateOf<String?>(null) }
    var researchQueryParam by remember { mutableStateOf<String?>(null) }
    var showAboutDialog by remember { mutableStateOf(false) }

    // Ensure initial demo data is populated
    LaunchedEffect(Unit) {
        scope.launch {
            documentRepository.ensureSeeded()
        }
    }

    Scaffold(
        topBar = {
            if (currentScreen != Screen.DOCUMENT_DETAIL) {
                LegalensTopBar(
                    title = when (currentScreen) {
                        Screen.OVERVIEW -> "Legalens AI"
                        Screen.DOCUMENTS -> "Contracts Vault"
                        Screen.RESEARCH -> "Research Law"
                        Screen.COMPARE -> "Compare Documents"
                        Screen.CASES -> "Case Explorer"
                        Screen.LAWYER_PREP -> "Lawyer Prep"
                        Screen.GRAPH -> "Knowledge Ontology"
                        Screen.SOURCES -> "Source Registry"
                        Screen.TIMELINE -> "Deadlines & Milestones"
                        Screen.MORE_HUB -> "Legal Intelligence Hub"
                        Screen.DOCUMENT_DETAIL -> "Contract Analysis"
                    },
                    subtitle = "Evidence-first legal intelligence for India",
                    showBack = currentScreen != Screen.OVERVIEW &&
                               currentScreen != Screen.DOCUMENTS &&
                               currentScreen != Screen.RESEARCH &&
                               currentScreen != Screen.COMPARE &&
                               currentScreen != Screen.MORE_HUB,
                    onBack = { currentScreen = Screen.OVERVIEW },
                    onInfoClick = { showAboutDialog = true }
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceDark,
                contentColor = TextPrimary,
                modifier = Modifier.testTag("main_navigation_bar")
            ) {
                NavigationBarItem(
                    selected = currentScreen == Screen.OVERVIEW,
                    onClick = { currentScreen = Screen.OVERVIEW },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Overview") },
                    label = { Text("Overview", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF042017),
                        selectedTextColor = EmeraldLight,
                        indicatorColor = EmeraldPrimary,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("nav_overview")
                )

                NavigationBarItem(
                    selected = currentScreen == Screen.DOCUMENTS || currentScreen == Screen.DOCUMENT_DETAIL,
                    onClick = {
                        selectedDocumentId = null
                        currentScreen = Screen.DOCUMENTS
                    },
                    icon = { Icon(Icons.Default.Description, contentDescription = "Contracts") },
                    label = { Text("Contracts", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF042017),
                        selectedTextColor = EmeraldLight,
                        indicatorColor = EmeraldPrimary,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("nav_documents")
                )

                NavigationBarItem(
                    selected = currentScreen == Screen.RESEARCH,
                    onClick = { currentScreen = Screen.RESEARCH },
                    icon = { Icon(Icons.Default.Search, contentDescription = "Research") },
                    label = { Text("Research", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF042017),
                        selectedTextColor = EmeraldLight,
                        indicatorColor = EmeraldPrimary,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("nav_research")
                )

                NavigationBarItem(
                    selected = currentScreen == Screen.COMPARE,
                    onClick = { currentScreen = Screen.COMPARE },
                    icon = { Icon(Icons.Default.CompareArrows, contentDescription = "Compare") },
                    label = { Text("Compare", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF042017),
                        selectedTextColor = EmeraldLight,
                        indicatorColor = EmeraldPrimary,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("nav_compare")
                )

                NavigationBarItem(
                    selected = currentScreen == Screen.MORE_HUB ||
                               currentScreen == Screen.CASES ||
                               currentScreen == Screen.LAWYER_PREP ||
                               currentScreen == Screen.GRAPH ||
                               currentScreen == Screen.SOURCES ||
                               currentScreen == Screen.TIMELINE,
                    onClick = { currentScreen = Screen.MORE_HUB },
                    icon = { Icon(Icons.Default.Apps, contentDescription = "Hub") },
                    label = { Text("Hub", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF042017),
                        selectedTextColor = EmeraldLight,
                        indicatorColor = EmeraldPrimary,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("nav_hub")
                )
            }
        },
        containerColor = BackgroundDark
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentScreen) {
                Screen.OVERVIEW -> OverviewScreen(
                    documentRepository = documentRepository,
                    legalRepository = legalRepository,
                    onNavigateToDocuments = { currentScreen = Screen.DOCUMENTS },
                    onNavigateToResearch = { query ->
                        researchQueryParam = query
                        currentScreen = Screen.RESEARCH
                    },
                    onNavigateToCases = { currentScreen = Screen.CASES },
                    onNavigateToCompare = { currentScreen = Screen.COMPARE },
                    onNavigateToLawyerPrep = { currentScreen = Screen.LAWYER_PREP },
                    onNavigateToGraph = { currentScreen = Screen.GRAPH },
                    onNavigateToSources = { currentScreen = Screen.SOURCES },
                    onNavigateToTimeline = { currentScreen = Screen.TIMELINE },
                    onOpenDocument = { docId ->
                        selectedDocumentId = docId
                        currentScreen = Screen.DOCUMENT_DETAIL
                    }
                )

                Screen.DOCUMENTS -> DocumentsListScreen(
                    documentRepository = documentRepository,
                    onOpenDocument = { docId ->
                        selectedDocumentId = docId
                        currentScreen = Screen.DOCUMENT_DETAIL
                    }
                )

                Screen.DOCUMENT_DETAIL -> {
                    val docId = selectedDocumentId ?: "doc_technova_employment_v1"
                    DocumentDetailScreen(
                        documentId = docId,
                        documentRepository = documentRepository,
                        onBack = { currentScreen = Screen.DOCUMENTS },
                        onFindLaw = { lawQuery ->
                            researchQueryParam = lawQuery
                            currentScreen = Screen.RESEARCH
                        },
                        onNavigateToLawyerPrep = {
                            selectedDocumentId = docId
                            currentScreen = Screen.LAWYER_PREP
                        }
                    )
                }

                Screen.RESEARCH -> ResearchScreen(
                    initialQuery = researchQueryParam,
                    legalRepository = legalRepository
                )

                Screen.COMPARE -> CompareScreen(
                    documentRepository = documentRepository
                )

                Screen.CASES -> CaseExplorerScreen(
                    legalRepository = legalRepository
                )

                Screen.LAWYER_PREP -> LawyerPrepScreen(
                    initialDocumentId = selectedDocumentId,
                    documentRepository = documentRepository
                )

                Screen.GRAPH -> KnowledgeGraphScreen()

                Screen.SOURCES -> SourcesScreen(
                    legalRepository = legalRepository
                )

                Screen.TIMELINE -> TimelineScreen(
                    documentRepository = documentRepository
                )

                Screen.MORE_HUB -> MoreHubScreen(
                    onSelectScreen = { screen -> currentScreen = screen }
                )
            }
        }
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = {
                Text("About Legalens AI", fontWeight = FontWeight.Bold, color = TextPrimary)
            },
            text = {
                Column {
                    Text(
                        text = "Legalens AI is an evidence-first legal intelligence platform designed for the Indian legal ecosystem.",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "• Strict 'NO EVIDENCE -> NO CLAIM' policy.\n• Direct citation entailment against India Code legislative repository.\n• Verified landmark precedents from the Supreme Court of India.\n• Automatic local PII sanitization for PAN, Aadhaar, and phone numbers.",
                        color = TextMuted,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Disclaimer: Legalens AI does not replace qualified counsel enrolled with the Bar Council of India.",
                        color = Color(0xFFFCD34D),
                        fontSize = 11.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Close", color = EmeraldLight)
                }
            },
            containerColor = SurfaceDark
        )
    }
}

@Composable
fun MoreHubScreen(
    onSelectScreen: (Screen) -> Unit
) {
    val hubModules = listOf(
        HubItem("Case Explorer", "Landmark Supreme Court & High Court judgments", Icons.Default.Gavel, Color(0xFFFBBF24), Screen.CASES),
        HubItem("Lawyer Prep Dossier", "Synthesize executive briefing packs for counsel", Icons.Default.FactCheck, EmeraldPrimary, Screen.LAWYER_PREP),
        HubItem("Knowledge Ontology", "Interactive statutory and judicial relationship graph", Icons.Default.Hub, Color(0xFFA78BFA), Screen.GRAPH),
        HubItem("Contract Deadlines", "Extracted milestones, lock-ins, and notice expiries", Icons.Default.CalendarMonth, Color(0xFF60A5FA), Screen.TIMELINE),
        HubItem("Source Registry", "Live health and freshness of official government portals", Icons.Default.Security, EmeraldLight, Screen.SOURCES)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Legal Intelligence Hub",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Specialized legal workflows and official verification registries.",
                color = TextMuted,
                fontSize = 12.sp
            )
        }

        items(hubModules.size) { index ->
            val item = hubModules[index]
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLine),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectScreen(item.target) }
                    .testTag("hub_item_${item.title.lowercase().replace(" ", "_")}")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(item.color.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(item.icon, contentDescription = null, tint = item.color, modifier = Modifier.size(22.dp))
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = item.title, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = item.subtitle, color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

data class HubItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    val target: Screen
)
