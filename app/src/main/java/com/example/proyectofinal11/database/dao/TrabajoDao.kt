package com.example.proyectofinal11.database.dao

import androidx.room.*
import com.example.proyectofinal11.database.entities.TrabajoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrabajoDao {
    
    @Query("SELECT * FROM trabajos WHERE clienteId = :clienteId ORDER BY fecha DESC")
    fun getTrabajosByCliente(clienteId: String): Flow<List<TrabajoEntity>>
    
    @Query("SELECT * FROM trabajos WHERE profesionalId = :profesionalId ORDER BY fecha DESC")
    fun getTrabajosByProfesional(profesionalId: String): Flow<List<TrabajoEntity>>
    
    @Query("SELECT * FROM trabajos WHERE id = :id")
    suspend fun getTrabajoById(id: String): TrabajoEntity?
    
    @Query("SELECT * FROM trabajos WHERE clienteId = :clienteId AND estado = :estado ORDER BY fecha DESC")
    fun getTrabajosByClienteAndEstado(clienteId: String, estado: String): Flow<List<TrabajoEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrabajo(trabajo: TrabajoEntity)
    
    @Update
    suspend fun updateTrabajo(trabajo: TrabajoEntity)
    
    @Delete
    suspend fun deleteTrabajo(trabajo: TrabajoEntity)
}




