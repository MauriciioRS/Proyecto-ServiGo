package com.example.proyectofinal11.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profesionales")
data class ProfesionalEntity(
    @PrimaryKey
    val id: String,
    val nombre: String,
    val oficio: String,
    val calificacion: Float = 0f, // 0.0 a 5.0
    val precioHora: Double = 0.0,
    val ubicacion: String,
    val distrito: String,
    val descripcion: String = "",
    val fotoUrl: String? = null,
    val telefono: String? = null,
    val email: String? = null,
    val trabajosCompletados: Int = 0
) {
    val calificacionFormateada: String
        get() = String.format("%.1f", calificacion)
    
    val precioFormateado: String
        get() = "S/%.2f/hora".format(precioHora)
}




