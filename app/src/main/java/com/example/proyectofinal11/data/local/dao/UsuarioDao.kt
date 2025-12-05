
package com.example.proyectofinal11.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.proyectofinal11.data.local.entity.UsuarioEntity
import kotlinx.coroutines.flow.Flow // <-- ¡LÍNEA AÑADIDA! El error desaparecerá.

@Dao
interface UsuarioDao {

    // --- FUNCIONES CORREGIDAS Y UNIFICADAS ---

    // Inserta un usuario. Si ya existe (misma firebaseUid), lo reemplaza.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUsuario(usuario: UsuarioEntity)

    @Update
    suspend fun actualizarUsuario(usuario: UsuarioEntity)

    // Busca a un usuario por su UID de Firebase (la clave primaria).
    @Query("SELECT * FROM usuarios WHERE firebaseUid = :firebaseUid")
    suspend fun obtenerUsuarioPorFirebaseUid(firebaseUid: String): UsuarioEntity?

    // Busca un usuario por su email.
    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun obtenerUsuarioPorEmail(email: String): UsuarioEntity?

    // Busca un usuario por email y contraseña para el inicio de sesión.
    @Query("SELECT * FROM usuarios WHERE email = :email AND contrasena = :contrasena LIMIT 1")
    suspend fun iniciarSesion(email: String, contrasena: String): UsuarioEntity?

    // --- CONSULTA CORREGIDA ---
    // Cuenta el número total de usuarios en la tabla.
    // Se usa COUNT(*) que es más estándar y eficiente que contar por una columna específica.
    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contarUsuarios(): Int

    // --- NUEVAS FUNCIONES DE BÚSQUEDA QUE PEDISTE ---

    // Busca todos los usuarios que tengan un oficio específico (ignorando mayúsculas/minúsculas).
    @Query("SELECT * FROM usuarios WHERE oficio LIKE :oficio")
    suspend fun buscarPorOficio(oficio: String): List<UsuarioEntity>

    // Busca todos los usuarios que se encuentren en un distrito específico.
    @Query("SELECT * FROM usuarios WHERE distrito LIKE :distrito")
    suspend fun buscarPorDistrito(distrito: String): List<UsuarioEntity>

    @Query("SELECT * FROM usuarios WHERE tipoCuenta = 'Contratista' OR tipoCuenta = 'Ambos'")
    fun obtenerTodosLosProfesionales(): Flow<List<UsuarioEntity>>

    // Tu propuesta (casi correcta):
    @Query("SELECT * FROM usuarios WHERE esFavorito = 1")
    fun obtenerFavoritos(): Flow<List<UsuarioEntity>>
}
// La llave extra que estaba aquí ha sido eliminada.
