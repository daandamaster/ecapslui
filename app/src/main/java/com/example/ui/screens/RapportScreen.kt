package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.BuildCircle
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.FolderSpecial
import androidx.compose.material.icons.outlined.ForwardToInbox
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.PriorityHigh
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.EvidencePhoto
import com.example.data.InspectionStatus
import com.example.data.SeedData
import com.example.ui.InspectionUiState
import com.example.ui.components.HotlinkedPhotoThumbnail
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandBlueBorder
import com.example.ui.theme.BrandBlueDark
import com.example.ui.theme.BrandBlueSoft
import com.example.ui.theme.BrandNavy
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.MutedSurface
import com.example.ui.theme.StatusKritiek
import com.example.ui.theme.StatusKritiekSoft
import com.example.ui.theme.StatusMelding
import com.example.ui.theme.StatusMeldingSoft
import com.example.ui.theme.StatusOk
import com.example.ui.theme.StatusOkDot
import com.example.ui.theme.StatusOkSoft
import com.example.ui.theme.StatusTeal
import com.example.ui.theme.StatusTealSoft
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

private const val REPORT_SHA256 = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"

@Composable
fun RapportScreen(
    state: InspectionUiState,
    onAcceptSummary: () -> Unit,
    onRegenerateSummary: () -> Unit,
    onZoomPhoto: (EvidencePhoto) -> Unit,
    onBackToPunten: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var copiedHash by remember { mutableStateOf(false) }

    val allPhotos = remember(state.allPoints) {
        val fromPoints = state.allPoints.flatMap { it.photos }
        if (fromPoints.isNotEmpty()) {
            fromPoints
        } else {
            listOf(
                EvidencePhoto("d1", SeedData.IMG_DOSSIER_E02, "E02 Groepenkast", "11:44", "SHA-256 OK"),
                EvidencePhoto("d2", SeedData.IMG_DOSSIER_W01, "W01 Expansievat", "11:15", "0.8 bar • GPS Match"),
                EvidencePhoto("d3", SeedData.IMG_DOSSIER_W03, "W03 Spoeling", "11:50", "70.8°C Conform")
            )
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 14.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Certificate Header Banner
        item(key = "certificate_banner") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF059669),
                                Color(0xFF047857),
                                Color(0xFF115E59)
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Color(0x4D10B981),
                            shape = CircleShape
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Verified,
                                    contentDescription = null,
                                    tint = Color(0xFFD1FAE5),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = if (state.isReportFinalized) {
                                        "Status: Afgerond & Gecertificeerd"
                                    } else {
                                        "Status: Voorlopig Dossier"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFECFDF5)
                                )
                            }
                        }

                        Surface(
                            color = Color(0x33000000),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "SLUI-VAL-2025",
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 10.sp,
                                color = Color(0xFFA7F3D0),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = "Inspectie Afgerond & Ondertekend",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                        Text(
                            text = "Wettelijke dossierborging NEN 3140 / SCIOS Scope 8 / ISSO 55.2",
                            fontSize = 11.5.sp,
                            color = Color(0xFFD1FAE5)
                        )
                    }

                    Surface(
                        color = Color(0x2B000000),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "CERTIFICAAT ID",
                                        fontSize = 9.sp,
                                        color = Color(0xFFA7F3D0),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "#ECAP-2025-0418-NL-991",
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "TIJDSTEMPEL (CET)",
                                        fontSize = 9.sp,
                                        color = Color(0xFFA7F3D0),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "18 apr 2025 • 12:24:08",
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontSize = 11.sp,
                                        color = Color(0xFFECFDF5)
                                    )
                                }
                            }
                            HorizontalDivider(color = Color.White.copy(alpha = 0.14f))
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
                                        imageVector = Icons.Outlined.MyLocation,
                                        contentDescription = null,
                                        tint = Color(0xFF6EE7B7),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "Kantoorpand Zuidwest",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFFD1FAE5)
                                    )
                                }
                                Text(
                                    text = "GPS ±2.4m",
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontSize = 10.sp,
                                    color = Color(0xFFA7F3D0)
                                )
                            }
                        }
                    }
                }
            }
        }

        // AI Report Summary Card
        item(key = "ai_summary_card") {
            Surface(
                color = BrandBlueSoft,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, BrandBlueBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
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
                                tint = BrandBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "AI Rapport Samenvatting",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandBlueDark
                            )
                        }
                        Surface(color = Color(0xFFDBEAFE), shape = CircleShape) {
                            Text(
                                text = "${state.meldingCount} meldingen",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BrandBlueDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Text(
                        text = state.aiSummaryText,
                        fontSize = 12.sp,
                        color = TextPrimary,
                        lineHeight = 17.sp
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = onAcceptSummary,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (state.aiSummaryAccepted) StatusOk else BrandBlue
                            ),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(
                                text = if (state.aiSummaryAccepted) "Vastgelegd ✓" else "Akkoord",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        OutlinedButton(
                            onClick = onRegenerateSummary,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, BrandBlueBorder),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(
                                text = "Genereer opnieuw",
                                fontSize = 11.5.sp,
                                color = BrandBlueDark
                            )
                        }
                    }
                }
            }
        }

        // Resultaten Samenvatting Card
        item(key = "results_summary") {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, BorderSubtle),
                shadowElevation = 0.5.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(16.dp)
                                    .clip(CircleShape)
                                    .background(BrandBlue)
                            )
                            Text(
                                text = "RESULTATEN SAMENVATTING",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Surface(color = MutedSurface, shape = CircleShape) {
                            Text(
                                text = "${state.totalCount} Inspectiepunten",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // Segmented progress distribution bar
                    val total = state.totalCount.coerceAtLeast(1).toFloat()
                    val okFrac = (state.okCount / total).coerceAtLeast(0.05f)
                    val meldingFrac = (state.meldingCount / total).coerceAtLeast(0.05f)
                    val openFrac = (state.openCount / total).coerceAtLeast(0.05f)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape)
                            .background(MutedSurface)
                    ) {
                        Box(modifier = Modifier.weight(okFrac).height(8.dp).background(StatusOkDot))
                        Box(modifier = Modifier.weight(meldingFrac).height(8.dp).background(StatusMelding))
                        Box(modifier = Modifier.weight(openFrac).height(8.dp).background(Color(0xFF94A3B8)))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${state.okCount} Conform", fontSize = 10.5.sp, color = StatusOk, fontWeight = FontWeight.SemiBold)
                        Text("1 Direct Hersteld", fontSize = 10.5.sp, color = StatusTeal, fontWeight = FontWeight.Medium)
                        Text("${state.meldingCount} Meldingen", fontSize = 10.5.sp, color = StatusMelding, fontWeight = FontWeight.SemiBold)
                        Text("${state.openCount} Open", fontSize = 10.5.sp, color = TextSecondary)
                    }

                    // Breakdown Cards
                    BreakdownItem(
                        badgeText = "!",
                        badgeColor = StatusKritiek,
                        bgColor = StatusKritiekSoft,
                        title = "KRITIEKE MELDING • E02",
                        tag = "NEN 3140",
                        body = "Aardlekschakelaar 2F afgekeurd; installatiedeel veiliggesteld & direct spanning afgekoppeld."
                    )
                    BreakdownItem(
                        icon = Icons.Outlined.PriorityHigh,
                        badgeColor = StatusMelding,
                        bgColor = StatusMeldingSoft,
                        title = "AANDACHTSPUNTEN • W01 & W02",
                        tag = "ISSO 51 / 60",
                        body = "W01 expansievat 0.8 bar (navullen vereist) • W02 LBK filter F7 verzadiging 285 Pa."
                    )
                    BreakdownItem(
                        icon = Icons.Outlined.BuildCircle,
                        badgeColor = StatusTeal,
                        bgColor = StatusTealSoft,
                        title = "THERMISCH SPOELPROTOCOL • W03",
                        tag = "ISSO 55.2",
                        body = "W03 spoelprotocol geregistreerd: 20 min @ 70.8°C. Monstername conform NEN 1006."
                    )
                }
            }
        }

        // Digitale Handtekening & Validatie Card
        item(key = "signature_verification_card") {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, BorderSubtle),
                shadowElevation = 0.5.dp,
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
                                imageVector = Icons.Outlined.VerifiedUser,
                                contentDescription = null,
                                tint = BrandBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "DIGITALE HANDTEKENING & VALIDATIE",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Surface(color = StatusOkSoft, shape = CircleShape) {
                            Text(
                                text = "Gevalideerd",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatusOk,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    CredentialRow(
                        label = "GECERTIFICEERD INSPECTEUR",
                        title = state.inspectorName,
                        subtitle = state.inspectorCerts,
                        icon = Icons.Outlined.Badge
                    )
                    CredentialRow(
                        label = "OPDRACHTGEVER / LOCATIEHOUDER",
                        title = state.clientName,
                        subtitle = "Afd. Vastgoed, Energie & Facilitair",
                        icon = Icons.Outlined.Apartment
                    )

                    // PKI Signature Surface
                    Surface(
                        color = BrandBlueSoft,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, BrandBlueBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Stylus Capture • Pressure Vector 98.4%",
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontSize = 9.5.sp,
                                    color = TextSecondary
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Lock,
                                        contentDescription = null,
                                        tint = BrandBlueDark,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = "PKI Encrypted",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BrandBlueDark
                                    )
                                }
                            }

                            Canvas(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(74.dp)
                            ) {
                                val w = size.width
                                val h = size.height
                                if (state.usePresetSignature || state.signatureStrokes.isEmpty()) {
                                    val sigPath = Path().apply {
                                        moveTo(w * 0.08f, h * 0.65f)
                                        cubicTo(w * 0.14f, h * 0.20f, w * 0.19f, h * 0.25f, w * 0.22f, h * 0.55f)
                                        cubicTo(w * 0.25f, h * 0.72f, w * 0.30f, h * 0.75f, w * 0.35f, h * 0.42f)
                                        cubicTo(w * 0.40f, h * 0.12f, w * 0.46f, h * 0.60f, w * 0.53f, h * 0.55f)
                                        cubicTo(w * 0.60f, h * 0.45f, w * 0.68f, h * 0.60f, w * 0.76f, h * 0.42f)
                                        cubicTo(w * 0.84f, h * 0.30f, w * 0.90f, h * 0.45f, w * 0.94f, h * 0.32f)
                                    }
                                    drawPath(
                                        path = sigPath,
                                        color = BrandNavy,
                                        style = Stroke(width = 5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                    )
                                } else {
                                    state.signatureStrokes.forEach { stroke ->
                                        if (stroke.points.size > 1) {
                                            val path = Path().apply {
                                                moveTo(stroke.points.first().x * 0.85f, stroke.points.first().y * 0.6f)
                                                for (i in 1 until stroke.points.size) {
                                                    lineTo(stroke.points[i].x * 0.85f, stroke.points[i].y * 0.6f)
                                                }
                                            }
                                            drawPath(
                                                path = path,
                                                color = BrandNavy,
                                                style = Stroke(width = 4.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                            )
                                        }
                                    }
                                }
                            }

                            HorizontalDivider(color = BrandBlueBorder)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Biometrisch profiel 100% conform",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BrandNavy
                                )
                                Text(
                                    text = "18-04-2025 12:24:19 UTC+2",
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontSize = 9.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    // SHA-256 Strip
                    Surface(
                        color = Color(0xFF0F172A),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
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
                                        imageVector = Icons.Outlined.Fingerprint,
                                        contentDescription = null,
                                        tint = StatusOkDot,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "Cryptografische SHA-256 Hash",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFCBD5E1)
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            clipboard.setPrimaryClip(ClipData.newPlainText("SHA-256", REPORT_SHA256))
                                            copiedHash = true
                                        }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.ContentCopy,
                                        contentDescription = "Kopieer hash",
                                        tint = Color(0xFF60A5FA),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = if (copiedHash) "Gekopieerd" else "Kopieer",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF60A5FA)
                                    )
                                }
                            }
                            Surface(
                                color = Color(0xFF020617),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = REPORT_SHA256,
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontSize = 9.5.sp,
                                    color = Color(0xFFCBD5E1),
                                    lineHeight = 14.sp,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Dossier & Bijlagen (Hotlinked 3-column grid)
        item(key = "dossier_gallery") {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, BorderSubtle),
                shadowElevation = 0.5.dp,
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
                                imageVector = Icons.Outlined.FolderSpecial,
                                contentDescription = null,
                                tint = BrandBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "DOSSIER & BIJLAGEN (${allPhotos.size} Items)",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Surface(color = StatusOkSoft, shape = CircleShape) {
                            Text(
                                text = "EXIF Gevalideerd",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = StatusOk,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // 3-column grid rendered cleanly inside LazyColumn
                    val rows = allPhotos.chunked(3)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        rows.forEach { rowPhotos ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowPhotos.forEach { photo ->
                                    HotlinkedPhotoThumbnail(
                                        photo = photo,
                                        showBottomCaption = true,
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f),
                                        onClick = { onZoomPhoto(photo) }
                                    )
                                }
                                repeat(3 - rowPhotos.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }

                    Surface(
                        color = MutedSurface,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Terminal,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Column {
                                    Text(
                                        text = "GBS Automatiseringslog Geverifieerd",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Log ref: #SPOEL-W03-202505-884",
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontSize = 10.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                            Surface(color = Color(0xFFDBEAFE), shape = RoundedCornerShape(6.dp)) {
                                Text(
                                    text = "Synchroon",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandBlueDark,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Export & Share Actions
        item(key = "export_actions") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        shareReportText(context, state)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                    contentPadding = PaddingValues(vertical = 14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("share_report_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Exporteer & Deel Inspectierapport",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                OutlinedButton(
                    onClick = { shareReportText(context, state) },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderSubtle),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ForwardToInbox,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Deel Rapport via E-mail / GBS Koppeling",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextPrimary
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = onBackToPunten)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Terug naar Inspectiepunten",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BreakdownItem(
    badgeText: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    badgeColor: Color,
    bgColor: Color,
    title: String,
    tag: String,
    body: String
) {
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(badgeColor),
                contentAlignment = Alignment.Center
            ) {
                if (badgeText != null) {
                    Text(
                        text = badgeText,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                } else if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Surface(color = Color.White.copy(alpha = 0.7f), shape = RoundedCornerShape(4.dp)) {
                        Text(
                            text = tag,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = badgeColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                        )
                    }
                }
                Text(
                    text = body,
                    fontSize = 11.5.sp,
                    color = TextSecondary,
                    lineHeight = 15.5.sp
                )
            }
        }
    }
}

@Composable
private fun CredentialRow(
    label: String,
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Surface(
        color = MutedSurface,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = label,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Text(
                    text = title,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 10.5.sp,
                    color = TextSecondary
                )
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

private fun shareReportText(context: Context, state: InspectionUiState) {
    val meldingLines = state.allPoints
        .filter { it.status == InspectionStatus.MELDING }
        .joinToString("\n") { "- [${it.code}] ${it.title}: ${it.description ?: "Melding geregistreerd"}" }
        .ifBlank { "- Geen openstaande meldingen" }

    val reportText = buildString {
        appendLine("ECAP SLUI Inspectierapport (#ECAP-2025-0418-NL-991)")
        appendLine("Opdrachtgever: ${state.clientName}")
        appendLine("Inspecteur: ${state.inspectorName}")
        appendLine("Status: ${state.okCount} OK | ${state.meldingCount} Melding | ${state.openCount} Open")
        appendLine()
        appendLine("Samenvatting:")
        appendLine(state.aiSummaryText)
        appendLine()
        appendLine("Meldingen:")
        appendLine(meldingLines)
        appendLine()
        appendLine("SHA-256: $REPORT_SHA256")
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "ECAP SLUI Inspectierapport - ${state.clientName}")
        putExtra(Intent.EXTRA_TEXT, reportText)
    }
    context.startActivity(Intent.createChooser(intent, "Deel inspectierapport"))
}
