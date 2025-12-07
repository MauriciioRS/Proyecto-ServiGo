package com.example.proyectofinal11.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyectofinal11.data.local.entity.ResenaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ResenaDao {
    // Escucha cambios en las reseñas para un profesional (para la lista)
    @Query("SELECT * FROM resenas WHERE profesionalUid = :uid ORDER BY fecha DESC")
    fun obtenerResenasPorProfesional(uid: String): Flow<List<ResenaEntity>>

    // ⭐ NUEVA FUNCIÓN: Obtiene la lista una sola vez (no es un Flow)
    // La usaremos para calcular el promedio de forma síncrona.
    @Query("SELECT * FROM resenas WHERE profesionalUid = :uid")
    suspend fun obtenerListaDeResenasPorProfesional(uid: String): List<ResenaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarResena(resena: ResenaEntity)
}
