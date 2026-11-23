package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BuildingInfo
import com.example.data.Discipline
import com.example.data.EvidencePhoto
import com.example.data.InspectionPoint
import com.example.data.InspectionStatus
import com.example.data.SeverityBadge
import com.example.ui.InspectionUiState
import com.example.ui.StatusFilter
import com.example.ui.components.HotlinkedPhotoThumbnail
import com.example.ui.components.SignaturePadSection
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandBlueBorder
import com.example.ui.theme.BrandBlueDark
import com.example.ui.theme.BrandBlueSoft
import com.example.ui.theme.MutedSurface
import com.example.ui.theme.SegmentTrack
import com.example.ui.theme.StatusKritiek
import com.example.ui.theme.StatusKritiekBorder
import com.example.ui.theme.StatusKritiekSoft
import com.example.ui.theme.StatusMelding
import com.example.ui.theme.StatusMeldingBorder
import com.example.ui.theme.StatusMeldingDot
import com.example.ui.theme.StatusMeldingSoft
import com.example.ui.theme.StatusOk
import com.example.ui.theme.StatusOkBorder
import com.example.ui.theme.StatusOkDot
import com.example.ui.theme.StatusOkSoft
import com.example.ui.theme.StatusOpenDot
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PuntenScreen(
    state: InspectionUiState,
    onSelectStatusFilter: (StatusFilter) -> Unit,
    onTogglePointStatus: (InspectionPoint, InspectionStatus) -> Unit,
    onAddPhotoForPoint: (String) -> Unit,
    onZoomPhoto: (EvidencePhoto) -> Unit,
    onOpenAiVoice: () -> Unit,
    onOpenAiVision: () -> Unit,
    onOpenAiSummary: () -> Unit,
    onAddSignatureStroke: (List<Offset>) -> Unit,
    onClearSignature: () -> Unit,
    onFinalizeReport: () -> Unit,
    onGoToRapport: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buildingsToDisplay: List<BuildingInfo> = state.buildings.filter { building ->
        (state.selectedBuildingId == null || building.id == state.selectedBuildingId) &&
            state.filteredPoints.any { it.buildingId == building.id }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 12.dp, bottom = 104.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Segmented Status Filter Bar
        item(key = "segmented_status_filter") {
            SegmentedStatusFilterBar(
                totalCount = state.totalCount,
                openCount = state.openCount,
                meldingCount = state.meldingCount,
                okCount = state.okCount,
                selectedFilter = state.selectedStatusFilter,
                onSelect = onSelectStatusFilter
            )
        }

        // AI Quick Bar
        item(key = "ai_quick_bar") {
            AiQuickBar(
                onVoiceClick = onOpenAiVoice,
                onVisionClick = onOpenAiVision,
                onSummaryClick = onOpenAiSummary
            )
        }

        // Empty State if filter yields 0 points
        if (buildingsToDisplay.isEmpty()) {
            item(key = "empty_points") {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = null,
                            tint = StatusOk,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "Geen inspectiepunten in deze selectie",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary
                        )
                        Text(
                            text = "Kies een ander filter om alle punten te bekijken.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Building sections
        buildingsToDisplay.forEach { building ->
            val buildingPoints = state.filteredPoints.filter { it.buildingId == building.id }
            item(key = "building_header_${building.id}") {
                BuildingSectionHeader(building = building)
            }

            val elektraPoints = buildingPoints.filter { it.discipline == Discipline.ELEKTRA }
            if (elektraPoints.isNotEmpty()) {
                item(key = "discipline_elektra_${building.id}") {
                    DisciplineHeader(title = Discipline.ELEKTRA.label)
                }
                items(elektraPoints, key = { it.id }) { point ->
                    InspectionPointCard(
                        point = point,
                        onToggleStatus = { status -> onTogglePointStatus(point, status) },
                        onAddPhoto = { onAddPhotoForPoint(point.id) },
                        onZoomPhoto = onZoomPhoto,
                        onOpenVoice = onOpenAiVoice
                    )
                }
            }

            val wtbPoints = buildingPoints.filter { it.discipline == Discipline.WERKTUIGBOUWKUNDIG }
            if (wtbPoints.isNotEmpty()) {
                item(key = "discipline_wtb_${building.id}") {
                    DisciplineHeader(title = Discipline.WERKTUIGBOUWKUNDIG.label)
                }
                items(wtbPoints, key = { it.id }) { point ->
                    InspectionPointCard(
                        point = point,
                        onToggleStatus = { status -> onTogglePointStatus(point, status) },
                        onAddPhoto = { onAddPhotoForPoint(point.id) },
                        onZoomPhoto = onZoomPhoto,
                        onOpenVoice = onOpenAiVoice
                    )
                }
            }
        }

        // Signoff & Signature Section
        item(key = "signoff_section") {
            Spacer(modifier = Modifier.height(6.dp))
            SignaturePadSection(
                inspectorName = state.inspectorName,
                clientName = state.clientName,
                totalPhotosCount = state.totalPhotosCount,
                gpsValidated = state.gpsValidated,
                strokes = state.signatureStrokes,
                usePresetSignature = state.usePresetSignature,
                isFinalized = state.isReportFinalized,
                onAddStroke = onAddSignatureStroke,
                onClearSignature = onClearSignature,
                onOpenAttachments = onGoToRapport,
                onFinalize = onFinalizeReport
            )
        }
    }
}

@Composable
private fun SegmentedStatusFilterBar(
    totalCount: Int,
    openCount: Int,
    meldingCount: Int,
    okCount: Int,
    selectedFilter: StatusFilter,
    onSelect: (StatusFilter) -> Unit
) {
    Surface(
        color = SegmentTrack,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            val segments = listOf(
                Triple(StatusFilter.ALLE, "Alle $totalCount", "status_filter_alle"),
                Triple(StatusFilter.OPEN, "Open $openCount", "status_filter_open"),
                Triple(StatusFilter.MELDING, "Melding $meldingCount", "status_filter_melding"),
                Triple(StatusFilter.OK, "OK $okCount", "status_filter_ok")
            )
            segments.forEach { (filter, title, tag) ->
                val active = selectedFilter == filter
                Surface(
                    color = if (active) Color.White else Color.Transparent,
                    shape = RoundedCornerShape(10.dp),
                    shadowElevation = if (active) 1.dp else 0.dp,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onSelect(filter) }
                        .testTag(tag)
                ) {
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
                        color = if (active) TextPrimary else TextSecondary,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AiQuickBar(
    onVoiceClick: () -> Unit,
    onVisionClick: () -> Unit,
    onSummaryClick: () -> Unit
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BrandBlueBorder),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
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
                        tint = BrandBlue,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "AI ASSISTENT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandBlueDark
                    )
                }
                Text(
                    text = "Snelle hulp",
                    fontSize = 10.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AiQuickTile(
                    icon = Icons.Outlined.Mic,
                    title = "Spraak-inspectie",
                    subtitle = "Spreek punt in",
                    onClick = onVoiceClick,
                    tag = "ai_quick_voice",
                    modifier = Modifier.weight(1f)
                )
                AiQuickTile(
                    icon = Icons.Outlined.CameraAlt,
                    title = "AI Foto-scan",
                    subtitle = "Herken gebrek",
                    onClick = onVisionClick,
                    tag = "ai_quick_vision",
                    modifier = Modifier.weight(1f)
                )
                AiQuickTile(
                    icon = Icons.Outlined.Description,
                    title = "Auto-samenvatting",
                    subtitle = "Vul rapport in",
                    onClick = onSummaryClick,
                    tag = "ai_quick_summary",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun AiQuickTile(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    tag: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = BrandBlueSoft,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BrandBlueBorder),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag(tag)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 9.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = BrandBlue,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = BrandBlueDark,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                fontSize = 9.5.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun BuildingSectionHeader(building: BuildingInfo) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = building.name,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        Surface(
            color = SegmentTrack,
            shape = RoundedCornerShape(6.dp)
        ) {
            Text(
                text = building.disciplineTag,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
    }
}

@Composable
private fun DisciplineHeader(title: String) {
    Text(
        text = title.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        color = TextMuted,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
    )
}

@Composable
private fun InspectionPointCard(
    point: InspectionPoint,
    onToggleStatus: (InspectionStatus) -> Unit,
    onAddPhoto: () -> Unit,
    onZoomPhoto: (EvidencePhoto) -> Unit,
    onOpenVoice: () -> Unit
) {
    val dotColor = when (point.status) {
        InspectionStatus.OK -> StatusOkDot
        InspectionStatus.MELDING -> StatusMeldingDot
        InspectionStatus.OPEN -> StatusOpenDot
    }

    val cardBorderColor = when {
        point.status == InspectionStatus.MELDING -> StatusMeldingBorder
        point.status == InspectionStatus.OK && point.photos.isNotEmpty() -> StatusOkBorder
        else -> BorderSubtle
    }

    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, cardBorderColor),
        shadowElevation = 0.5.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_${point.id}")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(dotColor)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = point.code,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted
                            )
                            if (point.severity != SeverityBadge.NONE) {
                                SeverityChip(point.severity)
                            }
                        }
                        Text(
                            text = point.title,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary,
                            lineHeight = 18.sp
                        )
                        if (!point.description.isNullOrBlank()) {
                            Text(
                                text = point.description,
                                fontSize = 11.5.sp,
                                color = if (point.status == InspectionStatus.OK) StatusOk else StatusMelding,
                                lineHeight = 16.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Action buttons (OK / Melding + optional mic for open)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (point.status == InspectionStatus.OPEN && point.code in listOf("E03", "W03")) {
                        Surface(
                            color = BrandBlueSoft,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, BrandBlueBorder),
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable(onClick = onOpenVoice)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.Mic,
                                    contentDescription = "Spraakinvoer AI",
                                    tint = BrandBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    StatusActionButton(
                        label = "OK",
                        selected = point.status == InspectionStatus.OK,
                        activeColor = StatusOk,
                        activeBg = StatusOkSoft,
                        onClick = { onToggleStatus(InspectionStatus.OK) },
                        tag = "btn_ok_${point.id}"
                    )
                    StatusActionButton(
                        label = "Melding",
                        selected = point.status == InspectionStatus.MELDING,
                        activeColor = StatusMelding,
                        activeBg = StatusMeldingSoft,
                        onClick = { onToggleStatus(InspectionStatus.MELDING) },
                        tag = "btn_melding_${point.id}"
                    )
                }
            }

            // Evidence Photo Gallery + AI Analysis if present
            AnimatedVisibility(
                visible = point.photos.isNotEmpty() || point.aiAnalysis != null || point.status == InspectionStatus.MELDING
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    HorizontalDivider(color = MutedSurface)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.PhotoCamera,
                                contentDescription = null,
                                tint = if (point.status == InspectionStatus.OK) StatusOk else StatusMelding,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = when (point.photos.size) {
                                    0 -> "Nog geen bewijsfoto toegevoegd"
                                    1 -> "1 bewijsfoto bijgevoegd"
                                    else -> "${point.photos.size} bewijsfoto's bijgevoegd"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary
                            )
                        }
                        if (!point.lastUpdated.isNullOrBlank()) {
                            Text(
                                text = "Laatste: ${point.lastUpdated}",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }

                    // Hotlinked thumbnails row + Add Photo button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        point.photos.forEach { photo ->
                            HotlinkedPhotoThumbnail(
                                photo = photo,
                                modifier = Modifier
                                    .width(84.dp)
                                    .height(66.dp),
                                onClick = { onZoomPhoto(photo) }
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(84.dp)
                                .height(66.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(BrandBlueSoft)
                                .border(1.5.dp, BrandBlue, RoundedCornerShape(10.dp))
                                .clickable(onClick = onAddPhoto)
                                .testTag("add_photo_${point.id}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Add,
                                    contentDescription = "Foto toevoegen",
                                    tint = BrandBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "+ Foto",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BrandBlueDark
                                )
                            }
                        }
                    }

                    // AI Analysis Pill
                    if (!point.aiAnalysis.isNullOrBlank()) {
                        val isOk = point.status == InspectionStatus.OK
                        Surface(
                            color = if (isOk) StatusOkSoft else BrandBlueSoft,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (isOk) StatusOkBorder else BrandBlueBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.AutoAwesome,
                                    contentDescription = null,
                                    tint = if (isOk) StatusOk else BrandBlue,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append("AI Analyse: ")
                                        }
                                        append(point.aiAnalysis)
                                    },
                                    fontSize = 11.sp,
                                    color = if (isOk) StatusOk else BrandBlueDark,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    // Extra Warning Note if present
                    if (!point.extraNote.isNullOrBlank()) {
                        Surface(
                            color = StatusMeldingSoft,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, StatusMeldingBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.WarningAmber,
                                    contentDescription = null,
                                    tint = StatusMelding,
                                    modifier = Modifier
                                        .padding(top = 1.dp)
                                        .size(14.dp)
                                )
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append("Extra opmerking: ")
                                        }
                                        append(point.extraNote)
                                    },
                                    fontSize = 11.sp,
                                    color = Color(0xFF92400E),
                                    lineHeight = 15.sp
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
private fun SeverityChip(severity: SeverityBadge) {
    val (bg, border, textColor) = when (severity) {
        SeverityBadge.KRITIEK -> Triple(StatusKritiekSoft, StatusKritiekBorder, StatusKritiek)
        SeverityBadge.BELANGRIJK, SeverityBadge.AANDACHT -> Triple(StatusMeldingSoft, StatusMeldingBorder, StatusMelding)
        SeverityBadge.ADVIES -> Triple(BrandBlueSoft, BrandBlueBorder, BrandBlueDark)
        SeverityBadge.OPGELOST -> Triple(StatusOkSoft, StatusOkBorder, StatusOk)
        SeverityBadge.NONE -> Triple(MutedSurface, BorderSubtle, TextSecondary)
    }

    Surface(
        color = bg,
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, border)
    ) {
        Text(
            text = severity.label,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
        )
    }
}

@Composable
private fun StatusActionButton(
    label: String,
    selected: Boolean,
    activeColor: Color,
    activeBg: Color,
    onClick: () -> Unit,
    tag: String
) {
    Surface(
        color = if (selected) activeBg else Color.White,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(if (selected) 1.8.dp else 1.dp, if (selected) activeColor else BorderSubtle),
        modifier = Modifier
            .height(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .testTag(tag)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 11.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) activeColor else TextPrimary
            )
        }
    }
}
