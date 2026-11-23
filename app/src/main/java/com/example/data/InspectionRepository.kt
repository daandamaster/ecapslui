package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class InspectionRepository(private val dao: InspectionDao) {

    val pointsFlow: Flow<List<InspectionPoint>> = dao.observeAllStates().map { overrides ->
        val byId = overrides.associateBy { it.pointId }
        SeedData.initialPoints.map { seed ->
            val saved = byId[seed.id]
            if (saved == null) {
                seed
            } else {
                val restoredStatus = runCatching {
                    InspectionStatus.valueOf(saved.statusName)
                }.getOrDefault(seed.status)

                val extraPhotos = saved.addedPhotosSerialized
                    .split("|")
                    .filter { it.isNotBlank() }
                    .mapIndexed { index, raw ->
                        val parts = raw.split("::", limit = 2)
                        val url = parts[0]
                        val time = parts.getOrElse(1) { "Nu" }
                        EvidencePhoto(
                            id = "user_${seed.id}_$index",
                            url = url,
                            title = "${seed.code} Toegevoegde foto",
                            timestamp = time,
                            badgeText = "Lokaal opgeslagen"
                        )
                    }

                seed.copy(
                    status = restoredStatus,
                    description = saved.customDescription ?: seed.description,
                    photos = seed.photos + extraPhotos
                )
            }
        }
    }

    suspend fun updatePointStatus(
        point: InspectionPoint,
        newStatus: InspectionStatus,
        customDescription: String? = point.description
    ) {
        val serializedExtras = serializeUserPhotos(point)
        dao.upsertState(
            InspectionPointStateEntity(
                pointId = point.id,
                statusName = newStatus.name,
                customDescription = customDescription,
                addedPhotosSerialized = serializedExtras
            )
        )
    }

    suspend fun attachPhotoToPoint(point: InspectionPoint, photoUri: String, timestamp: String) {
        val currentExtras = point.photos
            .filter { it.id.startsWith("user_") }
            .map { "${it.url}::${it.timestamp}" }
        val updatedExtras = (currentExtras + "$photoUri::$timestamp").joinToString("|")
        dao.upsertState(
            InspectionPointStateEntity(
                pointId = point.id,
                statusName = point.status.name,
                customDescription = point.description,
                addedPhotosSerialized = updatedExtras
            )
        )
    }

    suspend fun resetAllToSeed() {
        dao.clearAll()
    }

    private fun serializeUserPhotos(point: InspectionPoint): String {
        return point.photos
            .filter { it.id.startsWith("user_") }
            .joinToString("|") { "${it.url}::${it.timestamp}" }
    }
}
