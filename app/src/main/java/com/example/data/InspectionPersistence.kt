package com.example.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "inspection_point_states")
data class InspectionPointStateEntity(
    @PrimaryKey val pointId: String,
    val statusName: String,
    val customDescription: String? = null,
    val addedPhotosSerialized: String = "",
    val updatedAtMillis: Long = System.currentTimeMillis()
)

@Dao
interface InspectionDao {
    @Query("SELECT * FROM inspection_point_states")
    fun observeAllStates(): Flow<List<InspectionPointStateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertState(state: InspectionPointStateEntity)

    @Query("DELETE FROM inspection_point_states")
    suspend fun clearAll()
}

@Database(entities = [InspectionPointStateEntity::class], version = 1, exportSchema = false)
abstract class InspectionDatabase : RoomDatabase() {
    abstract fun inspectionDao(): InspectionDao
}
