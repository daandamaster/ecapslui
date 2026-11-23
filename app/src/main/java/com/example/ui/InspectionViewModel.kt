package com.example.ui

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.BuildingInfo
import com.example.data.ChatMessage
import com.example.data.EvidencePhoto
import com.example.data.InspectionPoint
import com.example.data.InspectionRepository
import com.example.data.InspectionStatus
import com.example.data.SeedData
import com.example.data.VisionScenario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class MainTab(val route: String) {
    ROUTE("route"),
    PUNTEN("punten"),
    RAPPORT("rapport"),
    ASSISTENT("assistent"),
    INSTELLINGEN("instellingen")
}

enum class StatusFilter {
    ALLE,
    OPEN,
    MELDING,
    OK
}

enum class AssistentMode {
    CHAT,
    LIVE_VISION
}

data class SignatureStroke(
    val points: List<Offset>
)

data class InspectionUiState(
    val activeTab: MainTab = MainTab.PUNTEN,
    val selectedBuildingId: String? = null, // null = Alle
    val selectedStatusFilter: StatusFilter = StatusFilter.ALLE,
    val buildings: List<BuildingInfo> = SeedData.buildings,
    val allPoints: List<InspectionPoint> = SeedData.initialPoints,
    val signatureStrokes: List<SignatureStroke> = emptyList(),
    val usePresetSignature: Boolean = true,
    val isReportFinalized: Boolean = false,
    val assistentMode: AssistentMode = AssistentMode.CHAT,
    val activeVisionIndex: Int = 0,
    val arOverlayEnabled: Boolean = true,
    val chatMessages: List<ChatMessage> = SeedData.initialChatMessages,
    val aiSummaryAccepted: Boolean = false,
    val aiSummaryText: String = "",
    val inspectorName: String = "R. van der Meer",
    val inspectorCerts: String = "Cert. NEN 3140 / SCIOS Scope 8 / ISSO Reg. #8832-B",
    val clientName: String = "Gemeente Beheer B.V.",
    val gpsValidated: Boolean = true,
    val autoCacheImages: Boolean = true,
    val showHtmlWebView: Boolean = false,
    val zoomedPhoto: EvidencePhoto? = null,
    val activePhotoTargetPointId: String = "zw_e02",
    val statusBannerMessage: String? = null
) {
    val openCount: Int get() = allPoints.count { it.status == InspectionStatus.OPEN }
    val meldingCount: Int get() = allPoints.count { it.status == InspectionStatus.MELDING }
    val okCount: Int get() = allPoints.count { it.status == InspectionStatus.OK }
    val totalCount: Int get() = allPoints.size

    val totalPhotosCount: Int get() = allPoints.sumOf { it.photos.size }

    val filteredPoints: List<InspectionPoint>
        get() = allPoints.filter { point ->
            val matchesBuilding = selectedBuildingId == null || point.buildingId == selectedBuildingId
            val matchesStatus = when (selectedStatusFilter) {
                StatusFilter.ALLE -> true
                StatusFilter.OPEN -> point.status == InspectionStatus.OPEN
                StatusFilter.MELDING -> point.status == InspectionStatus.MELDING
                StatusFilter.OK -> point.status == InspectionStatus.OK
            }
            matchesBuilding && matchesStatus
        }

    val visionScenarios: List<VisionScenario> get() = SeedData.visionScenarios
    val currentVisionScenario: VisionScenario
        get() = visionScenarios[activeVisionIndex.coerceIn(0, visionScenarios.lastIndex)]
}

class InspectionViewModel(
    private val repository: InspectionRepository
) : ViewModel() {

    private val localUiState = MutableStateFlow(InspectionUiState())

    val uiState: StateFlow<InspectionUiState> = combine(
        localUiState,
        repository.pointsFlow
    ) { state, points ->
        val summary = state.aiSummaryText.ifBlank { buildDynamicSummary(points) }
        state.copy(
            allPoints = points,
            aiSummaryText = summary
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = localUiState.value.copy(
            aiSummaryText = buildDynamicSummary(SeedData.initialPoints)
        )
    )

    fun selectTab(tab: MainTab) {
        localUiState.update { it.copy(activeTab = tab, showHtmlWebView = false) }
    }

    fun selectBuilding(buildingId: String?) {
        localUiState.update { it.copy(selectedBuildingId = buildingId) }
    }

    fun jumpToBuildingInPunten(buildingId: String) {
        localUiState.update {
            it.copy(
                activeTab = MainTab.PUNTEN,
                selectedBuildingId = buildingId,
                selectedStatusFilter = StatusFilter.ALLE
            )
        }
    }

    fun selectStatusFilter(filter: StatusFilter) {
        localUiState.update { it.copy(selectedStatusFilter = filter) }
    }

    fun togglePointStatus(point: InspectionPoint, tappedStatus: InspectionStatus) {
        val nextStatus = if (point.status == tappedStatus) {
            InspectionStatus.OPEN
        } else {
            tappedStatus
        }
        viewModelScope.launch {
            repository.updatePointStatus(point, nextStatus)
        }
    }

    fun applyFindingToPoint(pointId: String, status: InspectionStatus, note: String) {
        val point = uiState.value.allPoints.find { it.id == pointId } ?: return
        viewModelScope.launch {
            repository.updatePointStatus(point, status, note)
            localUiState.update {
                it.copy(statusBannerMessage = "Bijgewerkt: ${point.code} opgeslagen als ${status.name}")
            }
        }
    }

    fun setPhotoTargetPoint(pointId: String) {
        localUiState.update { it.copy(activePhotoTargetPointId = pointId) }
    }

    fun attachPhotoUri(uriString: String) {
        val targetId = localUiState.value.activePhotoTargetPointId
        val point = uiState.value.allPoints.find { it.id == targetId }
            ?: uiState.value.allPoints.firstOrNull()
            ?: return
        val timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        viewModelScope.launch {
            repository.attachPhotoToPoint(point, uriString, timestamp)
            localUiState.update {
                it.copy(statusBannerMessage = "Foto toegevoegd aan ${point.code}")
            }
        }
    }

    fun addSignatureStroke(points: List<Offset>) {
        if (points.size < 2) return
        localUiState.update {
            it.copy(
                usePresetSignature = false,
                signatureStrokes = it.signatureStrokes + SignatureStroke(points)
            )
        }
    }

    fun clearSignature() {
        localUiState.update {
            it.copy(
                usePresetSignature = false,
                signatureStrokes = emptyList(),
                isReportFinalized = false
            )
        }
    }

    fun finalizeReport() {
        localUiState.update {
            it.copy(
                isReportFinalized = true,
                statusBannerMessage = "Inspectierapport succesvol verzonden & gecertificeerd"
            )
        }
    }

    fun regenerateAiSummary() {
        val freshSummary = buildDynamicSummary(uiState.value.allPoints)
        localUiState.update {
            it.copy(
                aiSummaryText = freshSummary,
                aiSummaryAccepted = false,
                statusBannerMessage = "Samenvatting opnieuw opgesteld op basis van actuele punten"
            )
        }
    }

    fun acceptAiSummary() {
        localUiState.update {
            it.copy(
                aiSummaryAccepted = true,
                statusBannerMessage = "Samenvatting vastgelegd in inspectierapport"
            )
        }
    }

    fun setAssistentMode(mode: AssistentMode) {
        localUiState.update { it.copy(assistentMode = mode) }
    }

    fun selectVisionScenario(index: Int) {
        localUiState.update { it.copy(activeVisionIndex = index) }
    }

    fun toggleArOverlay() {
        localUiState.update { it.copy(arOverlayEnabled = !it.arOverlayEnabled) }
    }

    fun sendChatMessage(rawText: String) {
        val text = rawText.trim()
        if (text.isEmpty()) return
        val now = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val userMsg = ChatMessage(
            id = "u_${System.currentTimeMillis()}",
            isUser = true,
            text = text,
            timestamp = now
        )
        val reply = buildContextualNormReply(text, uiState.value.allPoints, now)
        localUiState.update {
            it.copy(chatMessages = it.chatMessages + userMsg + reply)
        }
    }

    fun openZoomedPhoto(photo: EvidencePhoto?) {
        localUiState.update { it.copy(zoomedPhoto = photo) }
    }

    fun setShowHtmlWebView(show: Boolean) {
        localUiState.update { it.copy(showHtmlWebView = show) }
    }

    fun toggleAutoCacheImages() {
        localUiState.update { it.copy(autoCacheImages = !it.autoCacheImages) }
    }

    fun toggleGpsValidated() {
        localUiState.update { it.copy(gpsValidated = !it.gpsValidated) }
    }

    fun resetDemoData() {
        viewModelScope.launch {
            repository.resetAllToSeed()
            localUiState.update {
                it.copy(
                    isReportFinalized = false,
                    usePresetSignature = true,
                    signatureStrokes = emptyList(),
                    statusBannerMessage = "Inspectie teruggezet naar beginstand"
                )
            }
        }
    }

    fun dismissBanner() {
        localUiState.update { it.copy(statusBannerMessage = null) }
    }

    private fun buildDynamicSummary(points: List<InspectionPoint>): String {
        val meldingen = points.filter { it.status == InspectionStatus.MELDING }
        val okCount = points.count { it.status == InspectionStatus.OK }
        val openCount = points.count { it.status == InspectionStatus.OPEN }
        val codes = meldingen.joinToString(", ") { it.code }.ifEmpty { "geen" }
        return "${meldingen.size} meldingen geregistreerd ($codes), $okCount punten akkoord en $openCount punten nog open."
    }

    private fun buildContextualNormReply(
        query: String,
        points: List<InspectionPoint>,
        timestamp: String
    ): ChatMessage {
        val lower = query.lowercase(Locale.getDefault())
        return when {
            lower.contains("etb") || lower.contains("elektra") || lower.contains("nog") -> {
                val openEtb = points.filter {
                    it.status == InspectionStatus.OPEN && it.discipline.name == "ELEKTRA"
                }
                val listText = if (openEtb.isEmpty()) {
                    "Alle Elektra (ETB) inspectiepunten zijn reeds beoordeeld."
                } else {
                    "Nog ${openEtb.size} open ETB-punten: " +
                        openEtb.joinToString("; ") { "${it.code} (${it.title})" } + "."
                }
                ChatMessage(
                    id = "a_${System.currentTimeMillis()}",
                    isUser = false,
                    text = listText,
                    timestamp = timestamp,
                    normLabel = "ETB Checklist"
                )
            }
            lower.contains("nen 3140") || lower.contains("aardlek") || lower.contains("norm") -> {
                ChatMessage(
                    id = "a_${System.currentTimeMillis()}",
                    isUser = false,
                    text = "Conform NEN 3140 art. 5.3 & SCIOS Scope 8 moet een 30mA aardlekschakelaar binnen 300ms uitschakelen (max. 150ms bij 5×IΔn). Bij weigering direct markeren als Kritiek en spanningsloos stellen.",
                    timestamp = timestamp,
                    normLabel = "NEN 3140 art. 5.3",
                    linkedPointId = "zw_e02",
                    linkedPointTitle = "E02 Aardlekschakelaars",
                    findingDraft = "Aardlekschakelaar 2F voldoet niet aan NEN 3140 uitschakeltijd.",
                    suggestedStatus = InspectionStatus.MELDING
                )
            }
            lower.contains("rapport") || lower.contains("samenvatting") -> {
                ChatMessage(
                    id = "a_${System.currentTimeMillis()}",
                    isUser = false,
                    text = "Concept samenvatting: ${buildDynamicSummary(points)} Je kunt dit direct vastleggen onder het tabblad Rapport.",
                    timestamp = timestamp,
                    normLabel = "Rapportage"
                )
            }
            else -> {
                ChatMessage(
                    id = "a_${System.currentTimeMillis()}",
                    isUser = false,
                    text = "Notitie geregistreerd: \"$query\". Gekoppeld aan het actieve dossier voor Kantoorpand Zuidwest (${points.count { it.status == InspectionStatus.MELDING }} meldingen actief).",
                    timestamp = timestamp,
                    normLabel = "Dossierlog"
                )
            }
        }
    }

    class Factory(private val repository: InspectionRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return InspectionViewModel(repository) as T
        }
    }
}
