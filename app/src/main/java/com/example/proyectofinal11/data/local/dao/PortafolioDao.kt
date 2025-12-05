package com.example.proyectofinal11.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyectofinal11.data.local.entity.PortafolioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PortafolioDao {
    @Query("SELECT * FROM portafolio_items WHERE usuarioUid = :uid")
    fun obtenerPortafolioPorUsuario(uid: String): Flow<List<PortafolioEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarItem(item: PortafolioEntity)
}
