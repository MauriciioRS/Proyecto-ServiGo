
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
    entities = [ HistorialEntity::class, MensajeEntity::class, UsuarioEntity::class , FavoritoEntity::class , PortafolioEntity::class, // <-- AÑADIDO
        ResenaEntity::class  ],
    version = 24,
    exportSchema = false
)
abstract class ServiGoDatabase : RoomDatabase() {

    abstract fun historialDao(): HistorialDao
    abstract fun mensajeDao(): MensajeDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun favoritoDao(): FavoritoDao
    // ... dentro de la clase ServiGoDatabase
    abstract fun portafolioDao(): PortafolioDao
    abstract fun resenaDao(): ResenaDao

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
                    .addCallback(ServiGoDatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class ServiGoDatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
            // El usuario de prueba ya no necesita el campo 'contrasena'
             //   getDatabase(context).usuarioDao().insertarUsuario(getUsuarioDePrueba())
                getDatabase(context).historialDao().insertAll(getHistorialDePrueba())
            }
        }

        // ... dentro de la clase ServiGoDatabaseCallback
       // private fun getUsuarioDePrueba(): UsuarioEntity {
            // Creamos un usuario de prueba que SÍ es un profesional.
        //    return UsuarioEntity(
        //        firebaseUid = "uid_de_prueba_jhair_botella", // ID único de prueba
        //        estadoVerificacion = "Verificado (Prueba)",
        //        nombre = "Jhair",
        //        apellido = "Botella",
        //        dni = "87654321",
        //        fechaNacimiento = "01/01/1995",
        //        direccion = "Calle Falsa 456",
        //        distrito = "Miraflores",
        //        email = "jhair.botella@gmail.com",
         //       contrasena = null, // No guardamos contraseñas reales
         //       fotoPerfilBase64 = null, // Dejamos la foto nula por ahora

                // --- ¡LA PARTE MÁS IMPORTANTE! ---
        //        tipoCuenta = "Contratista", // ¡Este usuario es un profesional!
        //        oficio = "Diseñador Gráfico",

                // --- Datos de profesional que antes estaban en la otra tabla ---
        //        rating = 4.9,
        //        numeroReviews = 88,
                // Una URL de imagen de fondo de ejemplo para que la tarjeta se vea bien
        //        imagenFondoUrl = "https://images.pexels.com/photos/3184454/pexels-photo-3184454.jpeg",
        //        esFavorito = false
       //     )
       // }


        private fun getHistorialDePrueba(): List<HistorialEntity> {
            return listOf(
                HistorialEntity(
                    clienteId = 1, contratistaId = 2, caso = "Pintar pared de sala",
                    titulo = "Servicio de Pintura", usuario = "Ana la Pintora", fecha = "15 Nov 2025",
                    precio = "S/120.00", estado = "En proceso"
                )
            )
        }
    }
}