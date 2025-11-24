package com.example.proyectofinal11.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.proyectofinal11.data.local.entity.ProfesionalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfesionalDao {
    @Query("SELECT * FROM profesionales ORDER BY nombre ASC")
    fun getAllProfesionales(): Flow<List<ProfesionalEntity>> // Usar Flow para datos reactivos

    @Query("SELECT * FROM profesionales WHERE esFavorito = 1")
    fun getFavoritos(): Flow<List<ProfesionalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(profesionales: List<ProfesionalEntity>)

    @Update
    suspend fun updateProfesional(profesional: ProfesionalEntity)

    @Query("DELETE FROM profesionales")
    suspend fun deleteAll()
}
