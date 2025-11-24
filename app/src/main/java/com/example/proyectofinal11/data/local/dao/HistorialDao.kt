package com.example.proyectofinal11.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyectofinal11.data.local.entity.HistorialEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistorialDao {

    // --- NUEVAS CONSULTAS INTELIGENTES ---

    // Obtiene los trabajos que YO solicité (soy el cliente)
    @Query("SELECT * FROM historial_trabajos WHERE clienteId = :usuarioIdActual AND estado = :estado ORDER BY id DESC")
    fun getMisSolicitudesPorEstado(usuarioIdActual: Int, estado: String): Flow<List<HistorialEntity>>

    // Obtiene los trabajos que YO debo atender (soy el contratista)
    @Query("SELECT * FROM historial_trabajos WHERE contratistaId = :usuarioIdActual AND estado = :estado ORDER BY id DESC")
    fun getMisAtendidosPorEstado(usuarioIdActual: Int, estado: String): Flow<List<HistorialEntity>>

    // --- Mantenemos estas para pruebas y otros usos ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(historial: List<HistorialEntity>)

    @Query("SELECT COUNT(*) FROM historial_trabajos")
    suspend fun contarHistorial(): Int // Una forma más eficiente de saber si está vacía
}
