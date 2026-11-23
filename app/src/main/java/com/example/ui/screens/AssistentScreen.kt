package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.AddTask
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material.icons.outlined.CenterFocusStrong
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.outlined.ZoomIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.data.ChatMessage
import com.example.data.EvidencePhoto
import com.example.data.InspectionStatus
import com.example.data.VisionArBox
import com.example.data.VisionScenario
import com.example.ui.AssistentMode
import com.example.ui.InspectionUiState
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandBlueBorder
import com.example.ui.theme.BrandBlueDark
import com.example.ui.theme.BrandBlueSoft
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.MutedSurface
import com.example.ui.theme.SegmentTrack
import com.example.ui.theme.StatusKritiek
import com.example.ui.theme.StatusKritiekSoft
import com.example.ui.theme.StatusOk
import com.example.ui.theme.StatusOkDot
import com.example.ui.theme.StatusOkSoft
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AssistentScreen(
    state: InspectionUiState,
    onSetMode: (AssistentMode) -> Unit,
    onSendMessage: (String) -> Unit,
    onApplyFinding: (String, InspectionStatus, String) -> Unit,
    onSelectVisionScenario: (Int) -> Unit,
    onToggleArOverlay: () -> Unit,
    onZoomPhoto: (EvidencePhoto) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(enabled = state.assistentMode == AssistentMode.LIVE_VISION) {
        onSetMode(AssistentMode.CHAT)
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Top Segmented Mode Switcher: AI Chat vs Live Vision AR
        Surface(
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Surface(
                        color = SegmentTrack,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            ModeSegmentTab(
                                label = "AI Chat",
                                icon = Icons.Outlined.Forum,
                                selected = state.assistentMode == AssistentMode.CHAT,
                                onClick = { onSetMode(AssistentMode.CHAT) },
                                tag = "assistent_mode_chat",
                                modifier = Modifier.weight(1f)
                            )
                            ModeSegmentTab(
                                label = "Live Vision AR",
                                icon = Icons.Outlined.CenterFocusStrong,
                                selected = state.assistentMode == AssistentMode.LIVE_VISION,
                                onClick = { onSetMode(AssistentMode.LIVE_VISION) },
                                tag = "assistent_mode_vision",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                HorizontalDivider(color = BorderSubtle, thickness = 1.dp)
            }
        }

        if (state.assistentMode == AssistentMode.CHAT) {
            AiChatContent(
                messages = state.chatMessages,
                onSendMessage = onSendMessage,
                onApplyFinding = onApplyFinding,
                onZoomPhoto = onZoomPhoto,
                modifier = Modifier.weight(1f)
            )
        } else {
            LiveVisionContent(
                scenarios = state.visionScenarios,
                activeIndex = state.activeVisionIndex,
                currentScenario = state.currentVisionScenario,
                arOverlayEnabled = state.arOverlayEnabled,
                onSelectScenario = onSelectVisionScenario,
                onToggleArOverlay = onToggleArOverlay,
                onApplyFinding = onApplyFinding,
                onZoomPhoto = onZoomPhoto,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ModeSegmentTab(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    tag: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (selected) Color.White else Color.Transparent,
        shape = RoundedCornerShape(9.dp),
        shadowElevation = if (selected) 1.dp else 0.dp,
        modifier = modifier
            .clip(RoundedCornerShape(9.dp))
            .clickable(onClick = onClick)
            .testTag(tag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 9.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) BrandBlue else TextSecondary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 12.5.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) BrandBlueDark else TextSecondary
            )
        }
    }
}

@Composable
private fun AiChatContent(
    messages: List<ChatMessage>,
    onSendMessage: (String) -> Unit,
    onApplyFinding: (String, InspectionStatus, String) -> Unit,
    onZoomPhoto: (EvidencePhoto) -> Unit,
    modifier: Modifier = Modifier
) {
    var draftText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    val suggestions = listOf(
        "Wat moet ik nog voor ETB?",
        "NEN 3140 norm checken",
        "Genereer concept rapportage",
        "Analyseer aardlekschakelaar 2F"
    )

    Column(modifier = modifier.fillMaxSize()) {
        // Quick prompt suggestions strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            suggestions.forEachIndexed { index, prompt ->
                Surface(
                    color = BrandBlueSoft,
                    shape = CircleShape,
                    border = BorderStroke(1.dp, BrandBlueBorder),
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onSendMessage(prompt) }
                        .testTag("chat_suggestion_$index")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AutoAwesome,
                            contentDescription = null,
                            tint = BrandBlue,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = prompt,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = BrandBlueDark
                        )
                    }
                }
            }
        }
        HorizontalDivider(color = BorderSubtle, thickness = 1.dp)

        // Messages list
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(messages, key = { it.id }) { msg ->
                ChatBubbleItem(
                    message = msg,
                    onApplyFinding = onApplyFinding,
                    onZoomPhoto = onZoomPhoto
                )
            }
        }

        // Composer Bar
        Surface(
            color = Color.White,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                HorizontalDivider(color = BorderSubtle, thickness = 1.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = draftText,
                        onValueChange = { draftText = it },
                        placeholder = {
                            Text(
                                text = "Stel een normvraag of dicteer gebrek...",
                                fontSize = 13.sp,
                                color = TextMuted
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandBlue,
                            unfocusedBorderColor = BorderSubtle,
                            focusedContainerColor = MutedSurface,
                            unfocusedContainerColor = MutedSurface,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (draftText.isNotBlank()) {
                                    onSendMessage(draftText)
                                    draftText = ""
                                }
                            }
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field")
                    )

                    Surface(
                        color = if (draftText.isNotBlank()) BrandBlue else MutedSurface,
                        shape = CircleShape,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable(enabled = draftText.isNotBlank()) {
                                onSendMessage(draftText)
                                draftText = ""
                            }
                            .testTag("chat_send_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Send,
                                contentDescription = "Verstuur bericht",
                                tint = if (draftText.isNotBlank()) Color.White else TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatBubbleItem(
    message: ChatMessage,
    onApplyFinding: (String, InspectionStatus, String) -> Unit,
    onZoomPhoto: (EvidencePhoto) -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
    ) {
        if (!message.isUser) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(BrandBlueSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = BrandBlue,
                        modifier = Modifier.size(12.dp)
                    )
                }
                Text(
                    text = "ECAP AI-Assistent",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )
                message.normLabel?.let { norm ->
                    Surface(
                        color = BrandBlueSoft,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = norm,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandBlueDark,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        Surface(
            color = if (message.isUser) BrandBlue else Color.White,
            shape = RoundedCornerShape(
                topStart = if (message.isUser) 16.dp else 4.dp,
                topEnd = if (message.isUser) 4.dp else 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            ),
            border = if (message.isUser) null else BorderStroke(1.dp, BorderSubtle),
            shadowElevation = if (message.isUser) 0.dp else 1.dp,
            modifier = Modifier.widthIn(max = 320.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (message.isVoiceNote) {
                    Surface(
                        color = if (message.isUser) Color(0x33FFFFFF) else MutedSurface,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (message.isUser) Icons.Outlined.Mic else Icons.Outlined.GraphicEq,
                                contentDescription = null,
                                tint = if (message.isUser) Color.White else BrandBlue,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = "Spraaknotitie ${message.voiceDuration ?: ""}",
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 10.5.sp,
                                color = if (message.isUser) Color.White else TextSecondary
                            )
                        }
                    }
                }

                // Hotlinked Evidence Image inside AI Chat Message
                if (message.imageUrl != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(155.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MutedSurface)
                            .clickable {
                                onZoomPhoto(
                                    EvidencePhoto(
                                        id = "chat_${message.id}",
                                        url = message.imageUrl,
                                        title = message.linkedPointTitle ?: "Inspectiefoto",
                                        timestamp = message.timestamp,
                                        badgeText = message.imageBadge ?: "AI Analyse"
                                    )
                                )
                            }
                    ) {
                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(message.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = message.linkedPointTitle ?: "AI analyse foto",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                            loading = {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(22.dp),
                                        strokeWidth = 2.dp,
                                        color = BrandBlue
                                    )
                                }
                            },
                            error = {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.BrokenImage,
                                        contentDescription = null,
                                        tint = TextMuted
                                    )
                                }
                            }
                        )

                        message.imageBadge?.let { badge ->
                            Surface(
                                color = StatusKritiek.copy(alpha = 0.92f),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = badge,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                Text(
                    text = message.text,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = if (message.isUser) Color.White else TextPrimary
                )

                // Interactive Draft Apply Button
                if (!message.isUser && message.linkedPointId != null && message.findingDraft != null) {
                    Button(
                        onClick = {
                            onApplyFinding(
                                message.linkedPointId,
                                message.suggestedStatus ?: InspectionStatus.MELDING,
                                message.findingDraft
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandBlueSoft,
                            contentColor = BrandBlueDark
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("chat_apply_${message.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AddTask,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Toevoegen aan ${message.linkedPointTitle ?: message.linkedPointId}",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Text(
                    text = message.timestamp,
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 9.5.sp,
                    color = if (message.isUser) Color(0xCCFFFFFF) else TextMuted,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
private fun LiveVisionContent(
    scenarios: List<VisionScenario>,
    activeIndex: Int,
    currentScenario: VisionScenario,
    arOverlayEnabled: Boolean,
    onSelectScenario: (Int) -> Unit,
    onToggleArOverlay: () -> Unit,
    onApplyFinding: (String, InspectionStatus, String) -> Unit,
    onZoomPhoto: (EvidencePhoto) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LazyColumn(
        contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Scenario Switcher Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                scenarios.forEachIndexed { idx, scenario ->
                    val selected = idx == activeIndex
                    Surface(
                        color = if (selected) BrandBlue else DarkSurface,
                        shape = CircleShape,
                        border = BorderStroke(1.dp, if (selected) BrandBlue else DarkBorder),
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onSelectScenario(idx) }
                            .testTag("vision_chip_${scenario.id}")
                    ) {
                        Text(
                            text = scenario.chipLabel,
                            fontSize = 12.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Camera AR HUD Container
        item {
            Surface(
                color = DarkSurface,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    // HUD Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(StatusKritiek)
                            )
                            Text(
                                text = "REC • ${currentScenario.locationLabel}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                color = Color(0xFF0F172A),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "FPS: 30",
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontSize = 10.sp,
                                    color = StatusOkDot,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                            Surface(
                                color = if (arOverlayEnabled) BrandBlue else Color(0xFF1E293B),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable(onClick = onToggleArOverlay)
                                    .testTag("toggle_ar_overlay_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = if (arOverlayEnabled) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                        contentDescription = "AR Overlay schakelen",
                                        tint = Color.White,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = if (arOverlayEnabled) "AR AAN" else "AR UIT",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    // Camera Viewport + Proportional AR Bounding Boxes
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(265.dp)
                            .background(Color.Black)
                    ) {
                        val containerW = maxWidth
                        val containerH = maxHeight

                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(currentScenario.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = currentScenario.locationLabel,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                            loading = {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = BrandBlue)
                                }
                            },
                            error = {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.BrokenImage,
                                        contentDescription = null,
                                        tint = TextMuted
                                    )
                                }
                            }
                        )

                        // Subtle dark gradient for readability
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color(0x33000000), Color.Transparent, Color(0x88000000))
                                    )
                                )
                        )

                        if (arOverlayEnabled) {
                            currentScenario.boxes.forEach { box ->
                                ArBoundingBoxOverlay(
                                    box = box,
                                    modifier = Modifier
                                        .offset(
                                            x = containerW * box.leftFraction,
                                            y = containerH * box.topFraction
                                        )
                                        .size(
                                            width = containerW * box.widthFraction,
                                            height = containerH * box.heightFraction
                                        )
                                )
                            }
                        }

                        // Norm badge top-left inside camera view
                        Surface(
                            color = Color(0xCC0F172A),
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, BrandBlueBorder.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Layers,
                                    contentDescription = null,
                                    tint = BrandBlueSoft,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = currentScenario.normBadge,
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        // Bottom HUD strip
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = currentScenario.normFooter,
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 10.sp,
                                color = Color(0xFFE2E8F0)
                            )
                            IconButton(
                                onClick = {
                                    onZoomPhoto(
                                        EvidencePhoto(
                                            id = "vis_${currentScenario.id}",
                                            url = currentScenario.imageUrl,
                                            title = currentScenario.locationLabel,
                                            timestamp = "Live AR",
                                            badgeText = currentScenario.normBadge
                                        )
                                    )
                                },
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x990F172A))
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ZoomIn,
                                    contentDescription = "Beeld vergroten",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Voice Prompt Card
        item {
            Surface(
                color = DarkSurface,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(BrandBlue.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Mic,
                            contentDescription = null,
                            tint = BrandBlueSoft,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "SPRAAKOPDRACHT INSPECTEUR",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                        Text(
                            text = currentScenario.userPrompt,
                            fontSize = 12.5.sp,
                            color = Color.White,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }

        // AI Diagnosis + Direct Save Action Card
        item {
            Surface(
                color = DarkSurface,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, BrandBlue.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AutoAwesome,
                                contentDescription = null,
                                tint = BrandBlueSoft,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Live Vision Diagnose",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Surface(
                            color = StatusKritiekSoft,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = currentScenario.aiBadge,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatusKritiek,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Text(
                        text = currentScenario.aiDiagnosis,
                        fontSize = 12.5.sp,
                        lineHeight = 19.sp,
                        color = Color(0xFFE2E8F0)
                    )

                    Button(
                        onClick = {
                            onApplyFinding(
                                currentScenario.targetPointId,
                                currentScenario.targetStatus,
                                currentScenario.aiDiagnosis
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandBlue,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(vertical = 12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("vision_apply_finding_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Verified,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = currentScenario.targetButtonLabel,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ArBoundingBoxOverlay(
    box: VisionArBox,
    modifier: Modifier = Modifier
) {
    val accent = if (box.isAlert) StatusKritiek else StatusOkDot
    val bgTint = if (box.isAlert) Color(0x33DC2626) else Color(0x2810B981)

    Box(
        modifier = modifier
            .border(width = 2.dp, color = accent, shape = RoundedCornerShape(8.dp))
            .background(bgTint, shape = RoundedCornerShape(8.dp))
    ) {
        Surface(
            color = accent,
            shape = RoundedCornerShape(topStart = 6.dp, bottomEnd = 6.dp),
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Column(modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)) {
                Text(
                    text = box.title,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                AnimatedVisibility(visible = box.subtitle.isNotBlank()) {
                    Text(
                        text = box.subtitle,
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 8.5.sp,
                        color = Color(0xFFF8FAFC),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
