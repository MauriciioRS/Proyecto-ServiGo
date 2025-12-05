package com.example.proyectofinal11.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.proyectofinal11.data.local.entity.FavoritoEntity
import com.example.proyectofinal11.data.local.entity.UsuarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritoDao {

    @Insert
    suspend fun agregarFavorito(favorito: FavoritoEntity)

    @Query("DELETE FROM favoritos WHERE clienteUid = :clienteUid AND contratistaUid = :contratistaUid")
    suspend fun eliminarFavorito(clienteUid: String, contratistaUid: String)

    @Query("""
        SELECT COUNT(*) FROM favoritos 
        WHERE clienteUid = :clienteUid AND contratistaUid = :contratistaUid
    """)
    suspend fun esFavorito(clienteUid: String, contratistaUid: String): Int

    @Query("""
        SELECT u.* FROM usuarios u
        INNER JOIN favoritos f
        ON u.firebaseUid = f.contratistaUid
        WHERE f.clienteUid = :clienteUid
    """)
    fun obtenerFavoritos(clienteUid: String): Flow<List<UsuarioEntity>>
}
