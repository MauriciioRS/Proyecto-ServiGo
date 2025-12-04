// Archivo: ServiGoDatabase.kt
package com.example.proyectofinal11.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.proyectofinal11.data.local.dao.*
import com.example.proyectofinal11.data.local.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


//kdkf
@Database(
    entities = [
        ProfesionalEntity::class,
        HistorialEntity::class,
        MensajeEntity::class,
        UsuarioEntity::class
    ],
    // INCREMENTA LA VERSIÓN PARA FORZAR LA RECREACIÓN
    version = 20,
    exportSchema = false
)
abstract class ServiGoDatabase : RoomDatabase() {

    abstract fun profesionalDao(): ProfesionalDao
    abstract fun historialDao(): HistorialDao
    abstract fun mensajeDao(): MensajeDao
    abstract fun usuarioDao(): UsuarioDao

    companion object {
        @Volatile
        private var INSTANCE: ServiGoDatabase? = null

        fun getDatabase(context: Context): ServiGoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ServiGoDatabase::class.java,
                    "servigo_database"
                )
                    .fallbackToDestructiveMigration()
                    // Usamos un único callback que se encargará de todo
                    .addCallback(ServiGoDatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    // --- CALLBACK CENTRALIZADO Y ROBUSTO ---
    private class ServiGoDatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    poblarBaseDeDatos(database)
                }
            }
        }

        suspend fun poblarBaseDeDatos(db: ServiGoDatabase) {
            // Inserta el usuario de prueba
            val usuarioDao = db.usuarioDao()
            if (usuarioDao.contarUsuarios() == 0) {
                usuarioDao.insertarUsuario(getUsuarioDePrueba())
            }

            // Inserta el historial de prueba
            val historialDao = db.historialDao()
            if (historialDao.contarHistorial() == 0) {
                historialDao.insertAll(getHistorialDePrueba())
            }
            // Puedes añadir más datos para otras tablas aquí...
        }

        private fun getUsuarioDePrueba(): UsuarioEntity {
            return UsuarioEntity(
                nombre = "Mario",
                apellido = "Pérez",
                dni = "12345678",
                fechaNacimiento = "01/01/1990",
                direccion = "Av. Siempre Viva 123",
                distrito = "Lima",
                email = "mario.perez@gmail.com",
                contrasena = "12345",
                tipoCuenta = "Contratista",
                oficio = "Electricista"
            )
        }

        private fun getHistorialDePrueba(): List<HistorialEntity> {
            val idUsuarioActual = 1 // Asumimos que el ID del usuario "Mario" será 1
            return listOf(
                HistorialEntity(
                    clienteId = idUsuarioActual, contratistaId = 2, caso = "Pintar pared de sala",
                    titulo = "Servicio de Pintura", usuario = "Ana la Pintora", fecha = "15 Nov 2025",
                    precio = "S/120.00", estado = "En proceso"
                ),
                HistorialEntity(
                    clienteId = 3, contratistaId = idUsuarioActual, caso = "Instalar nueva ducha",
                    titulo = "Servicio de Gasfitería", usuario = "Lucía Campos", fecha = "14 Nov 2025",
                    precio = "S/80.00", estado = "Finalizado"
                ),
                HistorialEntity(
                    clienteId = 4, contratistaId = idUsuarioActual, caso = "Fuga en lavadero",
                    titulo = "Reparación Urgente", usuario = "Pedro Suárez", fecha = "12 Nov 2025",
                    precio = "S/50.00", estado = "En proceso"
                )
            )
        }
    }
}
