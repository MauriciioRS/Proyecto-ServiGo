package com.example.proyectofinal11.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyectofinal11.data.local.entity.MensajeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MensajeDao {
    @Query("SELECT * FROM conversaciones ORDER BY id DESC")
    fun getAllConversaciones(): Flow<List<MensajeEntity>>

    @Query("SELECT * FROM conversaciones WHERE estado = :estado ORDER BY id DESC")
    fun getConversacionesByEstado(estado: String): Flow<List<MensajeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(mensajes: List<MensajeEntity>)

    @Query("DELETE FROM conversaciones")
    suspend fun deleteAll()
}
    