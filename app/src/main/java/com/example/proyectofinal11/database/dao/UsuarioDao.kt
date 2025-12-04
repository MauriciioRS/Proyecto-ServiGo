package com.example.proyectofinal11.database.dao

import androidx.room.*
import com.example.proyectofinal11.database.entities.UsuarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    
    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun getUsuarioById(id: String): UsuarioEntity?
    
    @Query("SELECT * FROM usuarios WHERE email = :email")
    suspend fun getUsuarioByEmail(email: String): UsuarioEntity?
    
    @Query("SELECT * FROM usuarios WHERE email = :email AND password = :password")
    suspend fun login(email: String, password: String): UsuarioEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsuario(usuario: UsuarioEntity)
    
    @Update
    suspend fun updateUsuario(usuario: UsuarioEntity)
    
    @Delete
    suspend fun deleteUsuario(usuario: UsuarioEntity)
    
    @Query("SELECT * FROM usuarios")
    fun getAllUsuarios(): Flow<List<UsuarioEntity>>
}




