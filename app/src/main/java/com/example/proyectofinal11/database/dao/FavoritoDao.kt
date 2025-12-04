package com.example.proyectofinal11.database.dao

import androidx.room.*
import com.example.proyectofinal11.database.entities.FavoritoEntity
import com.example.proyectofinal11.database.entities.ProfesionalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritoDao {
    
    @Query("""
        SELECT p.* FROM profesionales p
        INNER JOIN favoritos f ON p.id = f.profesionalId
        WHERE f.usuarioId = :usuarioId
        ORDER BY f.fechaAgregado DESC
    """)
    fun getFavoritosByUsuario(usuarioId: String): Flow<List<ProfesionalEntity>>
    
    @Query("SELECT * FROM favoritos WHERE usuarioId = :usuarioId AND profesionalId = :profesionalId")
    suspend fun isFavorito(usuarioId: String, profesionalId: String): FavoritoEntity?
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavorito(favorito: FavoritoEntity)
    
    @Delete
    suspend fun deleteFavorito(favorito: FavoritoEntity)
    
    @Query("DELETE FROM favoritos WHERE usuarioId = :usuarioId AND profesionalId = :profesionalId")
    suspend fun removeFavorito(usuarioId: String, profesionalId: String)
}




