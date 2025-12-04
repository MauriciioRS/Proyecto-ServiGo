package com.example.proyectofinal11.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "trabajos",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["clienteId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProfesionalEntity::class,
            parentColumns = ["id"],
            childColumns = ["profesionalId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class TrabajoEntity(
    @PrimaryKey
    val id: String,
    val clienteId: String,
    val profesionalId: String?,
    val titulo: String,
    val descripcion: String,
    val servicio: String, // Tipo de servicio/oficio
    val fecha: String,
    val precio: Double,
    val estado: String, // "Pendiente", "En Proceso", "Finalizado", "Cancelado"
    val direccion: String? = null,
    val calificacion: Float? = null
) {
    val precioFormateado: String
        get() = "S/%.2f".format(precio)
}




