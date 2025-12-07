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

@Database(
    entities = [
        HistorialEntity::class,
        MensajeEntity::class,
        UsuarioEntity::class,
        FavoritoEntity::class,
        PortafolioEntity::class,
        ResenaEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ServiGoDatabase : RoomDatabase() {

    abstract fun historialDao(): HistorialDao
    abstract fun mensajeDao(): MensajeDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun favoritoDao(): FavoritoDao
    abstract fun portafolioDao(): PortafolioDao
    abstract fun resenaDao(): ResenaDao

    private class ServiGoDatabaseCallback(
        private val context: Context
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Base de datos creada
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ServiGoDatabase? = null

        fun getDatabase(context: Context): ServiGoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ServiGoDatabase::class.java,
                    "servigo_db_final" // NOMBRE CAMBIADO PARA FORZAR RESET
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(ServiGoDatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}