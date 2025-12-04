package com.example.proyectofinal11.database

import android.content.Context
import com.example.proyectofinal11.database.entities.*
import kotlinx.coroutines.flow.Flow

class Repository(context: Context) {
    
    private val database = AppDatabase.getDatabase(context)
    private val usuarioDao = database.usuarioDao()
    private val profesionalDao = database.profesionalDao()
    private val trabajoDao = database.trabajoDao()
    private val favoritoDao = database.favoritoDao()
    
    // Usuario operations
    suspend fun insertUsuario(usuario: UsuarioEntity) = usuarioDao.insertUsuario(usuario)
    suspend fun getUsuarioById(id: String) = usuarioDao.getUsuarioById(id)
    suspend fun getUsuarioByEmail(email: String) = usuarioDao.getUsuarioByEmail(email)
    suspend fun login(email: String, password: String) = usuarioDao.login(email, password)
    suspend fun updateUsuario(usuario: UsuarioEntity) = usuarioDao.updateUsuario(usuario)
    fun getAllUsuarios(): Flow<List<UsuarioEntity>> = usuarioDao.getAllUsuarios()
    
    // Profesional operations
    fun getAllProfesionales(): Flow<List<ProfesionalEntity>> = profesionalDao.getAllProfesionales()
    suspend fun getProfesionalById(id: String) = profesionalDao.getProfesionalById(id)
    fun getProfesionalesByOficio(oficio: String): Flow<List<ProfesionalEntity>> = 
        profesionalDao.getProfesionalesByOficio(oficio)
    fun searchProfesionales(query: String): Flow<List<ProfesionalEntity>> = 
        profesionalDao.searchProfesionales(query)
    fun getProfesionalesByDistrito(distrito: String): Flow<List<ProfesionalEntity>> = 
        profesionalDao.getProfesionalesByDistrito(distrito)
    suspend fun insertProfesional(profesional: ProfesionalEntity) = profesionalDao.insertProfesional(profesional)
    suspend fun insertProfesionales(profesionales: List<ProfesionalEntity>) = 
        profesionalDao.insertProfesionales(profesionales)
    suspend fun updateProfesional(profesional: ProfesionalEntity) = profesionalDao.updateProfesional(profesional)
    
    // Trabajo operations
    fun getTrabajosByCliente(clienteId: String): Flow<List<TrabajoEntity>> = 
        trabajoDao.getTrabajosByCliente(clienteId)
    fun getTrabajosByProfesional(profesionalId: String): Flow<List<TrabajoEntity>> = 
        trabajoDao.getTrabajosByProfesional(profesionalId)
    suspend fun getTrabajoById(id: String) = trabajoDao.getTrabajoById(id)
    fun getTrabajosByClienteAndEstado(clienteId: String, estado: String): Flow<List<TrabajoEntity>> = 
        trabajoDao.getTrabajosByClienteAndEstado(clienteId, estado)
    suspend fun insertTrabajo(trabajo: TrabajoEntity) = trabajoDao.insertTrabajo(trabajo)
    suspend fun updateTrabajo(trabajo: TrabajoEntity) = trabajoDao.updateTrabajo(trabajo)
    
    // Favorito operations
    fun getFavoritosByUsuario(usuarioId: String): Flow<List<ProfesionalEntity>> = 
        favoritoDao.getFavoritosByUsuario(usuarioId)
    suspend fun isFavorito(usuarioId: String, profesionalId: String) = 
        favoritoDao.isFavorito(usuarioId, profesionalId)
    suspend fun addFavorito(usuarioId: String, profesionalId: String) {
        favoritoDao.insertFavorito(FavoritoEntity(usuarioId, profesionalId))
    }
    suspend fun removeFavorito(usuarioId: String, profesionalId: String) = 
        favoritoDao.removeFavorito(usuarioId, profesionalId)
}




