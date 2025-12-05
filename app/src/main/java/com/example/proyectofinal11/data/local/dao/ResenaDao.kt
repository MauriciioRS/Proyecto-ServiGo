package com.example.proyectofinal11.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyectofinal11.data.local.entity.ResenaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ResenaDao {
    @Query("SELECT * FROM resenas WHERE profesionalUid = :uid ORDER BY timestamp DESC")
    fun obtenerResenasPorProfesional(uid: String): Flow<List<ResenaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarResena(resena: ResenaEntity)
}
