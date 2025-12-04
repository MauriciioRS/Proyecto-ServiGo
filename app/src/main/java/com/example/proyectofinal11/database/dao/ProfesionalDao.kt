package com.example.proyectofinal11.database.dao

import androidx.room.*
import com.example.proyectofinal11.database.entities.ProfesionalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfesionalDao {
    
    @Query("SELECT * FROM profesionales")
    fun getAllProfesionales(): Flow<List<ProfesionalEntity>>
    
    @Query("SELECT * FROM profesionales WHERE id = :id")
    suspend fun getProfesionalById(id: String): ProfesionalEntity?
    
    @Query("SELECT * FROM profesionales WHERE oficio LIKE '%' || :oficio || '%'")
    fun getProfesionalesByOficio(oficio: String): Flow<List<ProfesionalEntity>>
    
    @Query("SELECT * FROM profesionales WHERE nombre LIKE '%' || :query || '%' OR oficio LIKE '%' || :query || '%' OR distrito LIKE '%' || :query || '%'")
    fun searchProfesionales(query: String): Flow<List<ProfesionalEntity>>
    
    @Query("SELECT * FROM profesionales WHERE distrito LIKE '%' || :distrito || '%'")
    fun getProfesionalesByDistrito(distrito: String): Flow<List<ProfesionalEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfesional(profesional: ProfesionalEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfesionales(profesionales: List<ProfesionalEntity>)
    
    @Update
    suspend fun updateProfesional(profesional: ProfesionalEntity)
    
    @Delete
    suspend fun deleteProfesional(profesional: ProfesionalEntity)
}




