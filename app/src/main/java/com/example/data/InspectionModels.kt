package com.example.data

enum class InspectionStatus {
    OPEN,
    MELDING,
    OK
}

enum class SeverityBadge(val label: String) {
    NONE(""),
    KRITIEK("Kritiek"),
    BELANGRIJK("Belangrijk"),
    ADVIES("Advies"),
    AANDACHT("Aandacht"),
    OPGELOST("Opgelost")
}

enum class Discipline(val label: String) {
    ELEKTRA("Elektra"),
    WERKTUIGBOUWKUNDIG("Werktuigbouwkundig")
}

data class EvidencePhoto(
    val id: String,
    val url: String,
    val title: String,
    val timestamp: String,
    val badgeText: String = "SHA-256 OK"
)

data class BuildingInfo(
    val id: String,
    val name: String,
    val disciplineTag: String,
    val address: String,
    val gpsAccuracy: String
)

data class InspectionPoint(
    val id: String,
    val buildingId: String,
    val code: String,
    val discipline: Discipline,
    val title: String,
    val status: InspectionStatus,
    val severity: SeverityBadge = SeverityBadge.NONE,
    val description: String? = null,
    val aiAnalysis: String? = null,
    val extraNote: String? = null,
    val photos: List<EvidencePhoto> = emptyList(),
    val lastUpdated: String? = null
)

data class ChatMessage(
    val id: String,
    val isUser: Boolean,
    val text: String,
    val timestamp: String,
    val isVoiceNote: Boolean = false,
    val voiceDuration: String? = null,
    val imageUrl: String? = null,
    val imageBadge: String? = null,
    val linkedPointId: String? = null,
    val linkedPointTitle: String? = null,
    val normLabel: String? = null,
    val findingDraft: String? = null,
    val suggestedStatus: InspectionStatus? = null
)

data class VisionArBox(
    val title: String,
    val subtitle: String,
    val isAlert: Boolean,
    val leftFraction: Float,
    val topFraction: Float,
    val widthFraction: Float,
    val heightFraction: Float
)

data class VisionScenario(
    val id: String,
    val chipLabel: String,
    val locationLabel: String,
    val normBadge: String,
    val normFooter: String,
    val imageUrl: String,
    val userPrompt: String,
    val aiDiagnosis: String,
    val aiBadge: String,
    val targetPointId: String,
    val targetButtonLabel: String,
    val targetStatus: InspectionStatus,
    val boxes: List<VisionArBox>
)
