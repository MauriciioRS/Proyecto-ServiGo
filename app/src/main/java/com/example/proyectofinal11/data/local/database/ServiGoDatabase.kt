package com.example.proyectofinal11.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.proyectofinal11.data.local.dao.HistorialDao
import com.example.proyectofinal11.data.local.dao.MensajeDao
import com.example.proyectofinal11.data.local.dao.ProfesionalDao
import com.example.proyectofinal11.data.local.dao.UsuarioDao
import com.example.proyectofinal11.data.local.entity.HistorialEntity
import com.example.proyectofinal11.data.local.entity.MensajeEntity
import com.example.proyectofinal11.data.local.entity.ProfesionalEntity
import com.example.proyectofinal11.data.local.entity.UsuarioEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ProfesionalEntity::class,
        HistorialEntity::class,
        MensajeEntity::class,
        UsuarioEntity::class
    ],
    version = 13,
    exportSchema = false
)
abstract class ServiGoDatabase : RoomDatabase() {

    abstract fun profesionalDao(): ProfesionalDao
    abstract fun historialDao(): HistorialDao
    abstract fun mensajeDao(): MensajeDao
    abstract fun usuarioDao(): UsuarioDao

    // En ServiGoDatabase.kt

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
                    // ===== CAMBIO CLAVE: Usamos una clase separada para el Callback =====
                    .addCallback(ServiGoDatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // --- SEPARAMOS EL CALLBACK PARA MAYOR CLARIDAD Y SEGURIDAD ---
        private class ServiGoDatabaseCallback(
            private val context: Context
        ) : RoomDatabase.Callback() {

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Obtenemos la instancia de la base de datos de forma segura
                // y lanzamos la corutina desde un scope más estable.
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        database.usuarioDao().insertarUsuario(getUsuarioDePrueba())
                    }
                }
            }
        }

        // --- La función que crea el usuario de prueba se mantiene igual ---
        private fun getUsuarioDePrueba(): UsuarioEntity {
            return UsuarioEntity(
                id = 1,
                nombre = "Mario",
                apellido = "Pérez",
                dni = "12345678",
                fechaNacimiento = "01 / 01 / 1990",
                direccion = "Av. Siempre Viva 123",
                distrito = "Lima",
                email = "mario.perez@gmail.com",
                contrasena = "1234", // Contraseña simple para pruebas
                tipoCuenta = "Contratista",
                oficio = "Electricista"
            )
        }
    }

}
