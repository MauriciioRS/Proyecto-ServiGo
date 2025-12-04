package com.example.proyectofinal11.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.proyectofinal11.database.dao.*
import com.example.proyectofinal11.database.entities.*

@Database(
    entities = [
        UsuarioEntity::class,
        ProfesionalEntity::class,
        TrabajoEntity::class,
        FavoritoEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun usuarioDao(): UsuarioDao
    abstract fun profesionalDao(): ProfesionalDao
    abstract fun trabajoDao(): TrabajoDao
    abstract fun favoritoDao(): FavoritoDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "servigo_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}




