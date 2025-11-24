package com.example.proyectofinal11.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyectofinal11.data.local.entity.UsuarioEntity

@Dao
interface UsuarioDao {

    // Inserta un nuevo usuario en la base de datos.
    // Si el email ya existe, reemplaza los datos (aunque podríamos querer evitarlo).
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUsuario(usuario: UsuarioEntity)

    // Busca un usuario por su email. Devuelve el usuario si lo encuentra, o null si no.
    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun getUsuarioPorEmail(email: String): UsuarioEntity?

    // Busca un usuario por email y contraseña para el inicio de sesión.
    @Query("SELECT * FROM usuarios WHERE email = :email AND contrasena = :contrasena LIMIT 1")
    suspend fun iniciarSesion(email: String, contrasena: String): UsuarioEntity?
}
