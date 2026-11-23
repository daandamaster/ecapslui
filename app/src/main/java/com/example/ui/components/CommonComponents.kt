package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.BuildingInfo
import com.example.data.EvidencePhoto
import com.example.ui.MainTab
import com.example.ui.SignatureStroke
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandBlueBorder
import com.example.ui.theme.BrandBlueDark
import com.example.ui.theme.BrandBlueSoft
import com.example.ui.theme.BrandNavy
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.MutedSurface
import com.example.ui.theme.StatusMeldingDot
import com.example.ui.theme.StatusOk
import com.example.ui.theme.StatusOkBorder
import com.example.ui.theme.StatusOkDot
import com.example.ui.theme.StatusOkSoft
import com.example.ui.theme.StatusOpenDot
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun EcapTopHeader(
    openCount: Int,
    meldingCount: Int,
    okCount: Int,
    buildings: List<BuildingInfo>,
    selectedBuildingId: String?,
    showLocationFilterTabs: Boolean,
    onMenuClick: () -> Unit,
    onSelectBuilding: (String?) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.97f),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        color = BrandBlue,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable(onClick = onMenuClick)
                            .testTag("menu_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.Menu,
                                contentDescription = stringResource(R.string.cd_menu),
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "ECAP SLUI",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            maxLines = 1
                        )
                        Text(
                            text = stringResource(R.string.app_subtitle),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }

                // Live Inspection Metrics Counters Pill
                Surface(
                    color = MutedSurface,
                    shape = CircleShape,
                    border = BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier.testTag("metrics_pill")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricDotLabel(dotColor = StatusOpenDot, text = "$openCount open")
                        MetricDotLabel(dotColor = StatusMeldingDot, text = "$meldingCount melding")
                        MetricDotLabel(dotColor = StatusOkDot, text = "$okCount ok")
                    }
                }
            }

            if (showLocationFilterTabs) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(start = 12.dp, end = 12.dp, bottom = 10.dp, top = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BuildingFilterChip(
                        label = "Alle",
                        selected = selectedBuildingId == null,
                        onClick = { onSelectBuilding(null) },
                        tag = "filter_building_alle"
                    )
                    buildings.forEach { building ->
                        BuildingFilterChip(
                            label = building.name,
                            selected = selectedBuildingId == building.id,
                            onClick = { onSelectBuilding(building.id) },
                            tag = "filter_building_${building.id}"
                        )
                    }
                }
            }
            HorizontalDivider(color = BorderSubtle, thickness = 1.dp)
        }
    }
}

@Composable
private fun MetricDotLabel(dotColor: Color, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun BuildingFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    tag: String
) {
    Surface(
        shape = CircleShape,
        color = if (selected) BrandBlueSoft else Color.White,
        border = BorderStroke(1.dp, if (selected) BrandBlue else BorderSubtle),
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .testTag(tag)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) BrandBlueDark else TextSecondary,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            maxLines = 1
        )
    }
}

@Composable
fun HotlinkedPhotoThumbnail(
    photo: EvidencePhoto,
    modifier: Modifier = Modifier,
    showBottomCaption: Boolean = false,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MutedSurface)
            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data(photo.url)
                .crossfade(true)
                .build(),
            contentDescription = photo.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            loading = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = BrandBlue
                    )
                }
            },
            error = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MutedSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.BrokenImage,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        )

        if (showBottomCaption) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color(0xE60F172A))
                        )
                    )
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
                Column {
                    Text(
                        text = photo.title,
                        color = Color.White,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = photo.badgeText,
                        color = Color(0xFFCBD5E1),
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 8.5.sp,
                        maxLines = 1
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color(0x990F172A))
                    .padding(vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = photo.timestamp,
                    color = Color.White,
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 9.sp
                )
            }
        }
    }
}

@Composable
fun ZoomedPhotoDialog(
    photo: EvidencePhoto,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            color = Color(0xF00B1220),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = photo.title,
                            color = Color.White,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = "${photo.timestamp} • ${photo.badgeText}",
                            color = Color(0xFF94A3B8),
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 11.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Sluiten",
                            tint = Color.White
                        )
                    }
                }

                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(photo.url)
                        .crossfade(true)
                        .build(),
                    contentDescription = photo.title,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .clip(RoundedCornerShape(16.dp)),
                    loading = {
                        Box(
                            modifier = Modifier.height(260.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SignaturePadSection(
    inspectorName: String,
    clientName: String,
    totalPhotosCount: Int,
    gpsValidated: Boolean,
    strokes: List<SignatureStroke>,
    usePresetSignature: Boolean,
    isFinalized: Boolean,
    onAddStroke: (List<Offset>) -> Unit,
    onClearSignature: () -> Unit,
    onOpenAttachments: () -> Unit,
    onFinalize: () -> Unit
) {
    val activeStroke = remember { mutableStateListOf<Offset>() }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Inspectie afronding & Handtekening",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Text(
                    text = "Formele verificatie en oplevering van rapport",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            Surface(
                color = StatusOkSoft,
                shape = CircleShape
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = StatusOk,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = if (isFinalized) "Ondertekend" else "Klaar voor paraaf",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StatusOk
                    )
                }
            }
        }

        Surface(
            color = Color.White,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, BorderSubtle),
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Inspector & Client grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "INSPECTEUR",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(BrandBlueSoft),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "RV",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandBlueDark
                                )
                            }
                            Text(
                                text = inspectorName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Text(
                            text = "Cert. NEN 3140 / SCIOS",
                            fontSize = 10.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "OPDRACHTGEVER / LOCATIE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Business,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = clientName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Text(
                            text = "18 april 2025 • 12:15",
                            fontSize = 10.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                HorizontalDivider(color = MutedSurface)

                // Signature Canvas Box
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Digitale Handtekening Inspecteur",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = stringResource(R.string.action_clear),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = BrandBlue,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable(onClick = onClearSignature)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("clear_signature_button")
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF8FAFC))
                            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { start ->
                                        activeStroke.clear()
                                        activeStroke.add(start)
                                    },
                                    onDrag = { change, _ ->
                                        change.consume()
                                        activeStroke.add(change.position)
                                    },
                                    onDragEnd = {
                                        if (activeStroke.size >= 2) {
                                            onAddStroke(activeStroke.toList())
                                        }
                                        activeStroke.clear()
                                    },
                                    onDragCancel = {
                                        activeStroke.clear()
                                    }
                                )
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                                .align(Alignment.TopCenter),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Edit,
                                    contentDescription = null,
                                    tint = BrandBlue,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "Teken hier met vinger of stylus",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }
                            if (usePresetSignature || strokes.isNotEmpty()) {
                                Surface(
                                    color = StatusOkSoft,
                                    shape = RoundedCornerShape(4.dp),
                                    border = BorderStroke(1.dp, StatusOkBorder)
                                ) {
                                    Text(
                                        text = "Geverifieerd",
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = StatusOk,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // Dashed baseline
                            drawLine(
                                color = Color(0xFFCBD5E1),
                                start = Offset(size.width * 0.14f, size.height * 0.72f),
                                end = Offset(size.width * 0.86f, size.height * 0.72f),
                                strokeWidth = 2f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                            )

                            if (usePresetSignature && strokes.isEmpty()) {
                                val w = size.width
                                val h = size.height
                                val presetPath = Path().apply {
                                    moveTo(w * 0.12f, h * 0.58f)
                                    cubicTo(w * 0.17f, h * 0.30f, w * 0.21f, h * 0.25f, w * 0.24f, h * 0.48f)
                                    cubicTo(w * 0.26f, h * 0.65f, w * 0.30f, h * 0.68f, w * 0.34f, h * 0.42f)
                                    cubicTo(w * 0.38f, h * 0.22f, w * 0.43f, h * 0.55f, w * 0.48f, h * 0.50f)
                                    cubicTo(w * 0.54f, h * 0.44f, w * 0.58f, h * 0.55f, w * 0.64f, h * 0.46f)
                                    cubicTo(w * 0.72f, h * 0.35f, w * 0.78f, h * 0.45f, w * 0.88f, h * 0.42f)
                                }
                                drawPath(
                                    path = presetPath,
                                    color = BrandNavy,
                                    style = Stroke(width = 5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                )
                            } else {
                                val allToDraw = strokes.map { it.points } +
                                    if (activeStroke.size > 1) listOf(activeStroke.toList()) else emptyList()
                                allToDraw.forEach { pts ->
                                    val path = Path().apply {
                                        moveTo(pts.first().x, pts.first().y)
                                        for (i in 1 until pts.size) {
                                            lineTo(pts[i].x, pts[i].y)
                                        }
                                    }
                                    drawPath(
                                        path = path,
                                        color = BrandNavy,
                                        style = Stroke(width = 5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .align(Alignment.BottomCenter),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Akkoord conform NEN normering",
                                fontSize = 9.5.sp,
                                color = TextMuted
                            )
                            Text(
                                text = "ID: ECAP-2025-04-18-91",
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 9.5.sp,
                                color = TextMuted
                            )
                        }
                    }
                }

                // Attachments & GPS Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MutedSurface,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable(onClick = onOpenAttachments)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.PhotoLibrary,
                                contentDescription = null,
                                tint = BrandBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Foto's bijlagen ($totalPhotosCount)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = if (gpsValidated) StatusOk else TextMuted,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = if (gpsValidated) "GPS Gevalideerd" else "GPS Uit",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (gpsValidated) StatusOk else TextMuted
                        )
                    }
                }

                // Primary Complete Button
                Button(
                    onClick = onFinalize,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFinalized) StatusOk else BrandBlue,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(vertical = 13.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("finalize_report_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isFinalized) {
                            "Inspectierapport succesvol verzonden"
                        } else {
                            stringResource(R.string.action_finalize)
                        },
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
fun EcapBottomBar(
    activeTab: MainTab,
    onSelectTab: (MainTab) -> Unit
) {
    NavigationBar(
        containerColor = Color.White.copy(alpha = 0.98f),
        tonalElevation = 0.dp,
        windowInsets = WindowInsets.navigationBars,
        modifier = Modifier
            .fillMaxWidth()
            .border(BorderStroke(0.5.dp, BorderSubtle))
    ) {
        val items = listOf(
            Triple(MainTab.ROUTE, stringResource(R.string.nav_route), Icons.Outlined.Route),
            Triple(MainTab.PUNTEN, stringResource(R.string.nav_punten), Icons.AutoMirrored.Outlined.FormatListBulleted),
            Triple(MainTab.RAPPORT, stringResource(R.string.nav_rapport), Icons.Outlined.Description),
            Triple(MainTab.ASSISTENT, stringResource(R.string.nav_assistent), Icons.Outlined.AutoAwesome),
            Triple(MainTab.INSTELLINGEN, stringResource(R.string.nav_instellingen), Icons.Outlined.Tune)
        )

        items.forEach { (tab, label, icon) ->
            val selected = activeTab == tab
            NavigationBarItem(
                selected = selected,
                onClick = { onSelectTab(tab) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BrandBlue,
                    selectedTextColor = BrandBlue,
                    indicatorColor = BrandBlueSoft,
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextSecondary
                ),
                modifier = Modifier.testTag("nav_${tab.route}")
            )
        }
    }
}
