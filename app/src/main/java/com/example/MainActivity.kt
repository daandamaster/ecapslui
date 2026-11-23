package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Html
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.data.InspectionDatabase
import com.example.data.InspectionRepository
import com.example.ui.AssistentMode
import com.example.ui.InspectionViewModel
import com.example.ui.MainTab
import com.example.ui.components.EcapBottomBar
import com.example.ui.components.EcapTopHeader
import com.example.ui.components.ZoomedPhotoDialog
import com.example.ui.screens.AssistentScreen
import com.example.ui.screens.HtmlPreviewScreen
import com.example.ui.screens.InstellingenScreen
import com.example.ui.screens.PuntenScreen
import com.example.ui.screens.RapportScreen
import com.example.ui.screens.RouteScreen
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandBlueSoft
import com.example.ui.theme.CanvasBackground
import com.example.ui.theme.EcapSluiTheme
import com.example.ui.theme.StatusMelding
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = Room.databaseBuilder(
            applicationContext,
            InspectionDatabase::class.java,
            "ecap_inspection_db"
        ).build()
        val repository = InspectionRepository(database.inspectionDao())

        setContent {
            EcapSluiTheme {
                val viewModel: InspectionViewModel = viewModel(
                    factory = InspectionViewModel.Factory(repository)
                )
                EcapInspectionApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EcapInspectionApp(viewModel: InspectionViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showQuickMenu by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.attachPhotoUri(uri.toString())
        }
    }

    LaunchedEffect(state.statusBannerMessage) {
        val message = state.statusBannerMessage
        if (!message.isNullOrBlank()) {
            snackbarHostState.showSnackbar(message)
            viewModel.dismissBanner()
        }
    }

    // Back navigation from secondary tabs returns to primary Punten checklist tab
    BackHandler(
        enabled = state.activeTab != MainTab.PUNTEN &&
            !state.showHtmlWebView &&
            state.zoomedPhoto == null &&
            !showQuickMenu
    ) {
        viewModel.selectTab(MainTab.PUNTEN)
    }

    if (state.showHtmlWebView) {
        HtmlPreviewScreen(
            onClose = { viewModel.setShowHtmlWebView(false) }
        )
        return
    }

    Scaffold(
        containerColor = CanvasBackground,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            EcapTopHeader(
                openCount = state.openCount,
                meldingCount = state.meldingCount,
                okCount = state.okCount,
                buildings = state.buildings,
                selectedBuildingId = state.selectedBuildingId,
                showLocationFilterTabs = state.activeTab == MainTab.PUNTEN,
                onMenuClick = { showQuickMenu = true },
                onSelectBuilding = viewModel::selectBuilding
            )
        },
        bottomBar = {
            EcapBottomBar(
                activeTab = state.activeTab,
                onSelectTab = viewModel::selectTab
            )
        },
        floatingActionButton = {
            if (state.activeTab == MainTab.PUNTEN) {
                ExtendedFloatingActionButton(
                    onClick = {
                        viewModel.setPhotoTargetPoint("zw_e02")
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.PhotoCamera,
                            contentDescription = stringResource(R.string.cd_camera)
                        )
                    },
                    text = {
                        Text(
                            text = stringResource(R.string.action_take_photo),
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    containerColor = BrandBlue,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.testTag("fab_take_photo")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 680.dp)
            ) {
                when (state.activeTab) {
                    MainTab.ROUTE -> {
                        RouteScreen(
                            state = state,
                            onSelectBuilding = viewModel::jumpToBuildingInPunten
                        )
                    }
                    MainTab.PUNTEN -> {
                        PuntenScreen(
                            state = state,
                            onSelectStatusFilter = viewModel::selectStatusFilter,
                            onTogglePointStatus = viewModel::togglePointStatus,
                            onAddPhotoForPoint = { pointId ->
                                viewModel.setPhotoTargetPoint(pointId)
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            onZoomPhoto = { photo -> viewModel.openZoomedPhoto(photo) },
                            onOpenAiVoice = {
                                viewModel.setAssistentMode(AssistentMode.CHAT)
                                viewModel.selectTab(MainTab.ASSISTENT)
                            },
                            onOpenAiVision = {
                                viewModel.setAssistentMode(AssistentMode.LIVE_VISION)
                                viewModel.selectTab(MainTab.ASSISTENT)
                            },
                            onOpenAiSummary = {
                                viewModel.selectTab(MainTab.RAPPORT)
                            },
                            onAddSignatureStroke = viewModel::addSignatureStroke,
                            onClearSignature = viewModel::clearSignature,
                            onFinalizeReport = viewModel::finalizeReport,
                            onGoToRapport = { viewModel.selectTab(MainTab.RAPPORT) }
                        )
                    }
                    MainTab.RAPPORT -> {
                        RapportScreen(
                            state = state,
                            onAcceptSummary = viewModel::acceptAiSummary,
                            onRegenerateSummary = viewModel::regenerateAiSummary,
                            onZoomPhoto = { photo -> viewModel.openZoomedPhoto(photo) },
                            onBackToPunten = { viewModel.selectTab(MainTab.PUNTEN) }
                        )
                    }
                    MainTab.ASSISTENT -> {
                        AssistentScreen(
                            state = state,
                            onSetMode = viewModel::setAssistentMode,
                            onSendMessage = viewModel::sendChatMessage,
                            onApplyFinding = viewModel::applyFindingToPoint,
                            onSelectVisionScenario = viewModel::selectVisionScenario,
                            onToggleArOverlay = viewModel::toggleArOverlay,
                            onZoomPhoto = { photo -> viewModel.openZoomedPhoto(photo) }
                        )
                    }
                    MainTab.INSTELLINGEN -> {
                        InstellingenScreen(
                            state = state,
                            onToggleGps = viewModel::toggleGpsValidated,
                            onToggleAutoCache = viewModel::toggleAutoCacheImages,
                            onOpenHtmlWebView = { viewModel.setShowHtmlWebView(true) },
                            onResetDemoData = viewModel::resetDemoData
                        )
                    }
                }
            }
        }
    }

    state.zoomedPhoto?.let { photo ->
        ZoomedPhotoDialog(
            photo = photo,
            onDismiss = { viewModel.openZoomedPhoto(null) }
        )
    }

    if (showQuickMenu) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showQuickMenu = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "ECAP SLUI Navigatie & Tools",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                QuickMenuItem(
                    icon = Icons.Outlined.Route,
                    title = "Route & Objecten",
                    subtitle = "${state.buildings.size} gebouwen • Overzicht",
                    onClick = {
                        viewModel.selectTab(MainTab.ROUTE)
                        showQuickMenu = false
                    }
                )
                QuickMenuItem(
                    icon = Icons.AutoMirrored.Outlined.FormatListBulleted,
                    title = "Inspectiepunten (ETB + WTB)",
                    subtitle = "${state.openCount} open • ${state.meldingCount} meldingen",
                    onClick = {
                        viewModel.selectTab(MainTab.PUNTEN)
                        showQuickMenu = false
                    }
                )
                QuickMenuItem(
                    icon = Icons.Outlined.Description,
                    title = "Dossier & Rapportage",
                    subtitle = "NEN 3140 / ISSO certificaat & export",
                    onClick = {
                        viewModel.selectTab(MainTab.RAPPORT)
                        showQuickMenu = false
                    }
                )
                QuickMenuItem(
                    icon = Icons.Outlined.AutoAwesome,
                    title = "AI Assistent & Live Vision",
                    subtitle = "Normcheck & AR camera-overlay",
                    onClick = {
                        viewModel.selectTab(MainTab.ASSISTENT)
                        showQuickMenu = false
                    }
                )
                QuickMenuItem(
                    icon = Icons.Outlined.Tune,
                    title = "Instellingen",
                    subtitle = "Inspecteurprofiel & cache",
                    onClick = {
                        viewModel.selectTab(MainTab.INSTELLINGEN)
                        showQuickMenu = false
                    }
                )
                HorizontalDivider(color = BorderSubtle, modifier = Modifier.padding(vertical = 4.dp))
                QuickMenuItem(
                    icon = Icons.Outlined.Html,
                    title = "Originele HTML WebView openen",
                    subtitle = "Bekijk de ingesloten HTML/CSS pagina direct in WebView",
                    tint = BrandBlue,
                    onClick = {
                        showQuickMenu = false
                        viewModel.setShowHtmlWebView(true)
                    }
                )
                QuickMenuItem(
                    icon = Icons.Outlined.RestartAlt,
                    title = "Beginstand herstellen",
                    subtitle = "Zet alle inspectiepunten terug naar beginstatus",
                    tint = StatusMelding,
                    onClick = {
                        showQuickMenu = false
                        viewModel.resetDemoData()
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun QuickMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    tint: Color = TextPrimary,
    onClick: () -> Unit
) {
    Surface(
        color = Color.Transparent,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                color = BrandBlueSoft,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (tint == TextPrimary) BrandBlue else tint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = tint
                )
                Text(
                    text = subtitle,
                    fontSize = 11.5.sp,
                    color = TextSecondary
                )
            }
        }
    }
}
