package com.example.proyectofinal11.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "resenas",
    foreignKeys = [ForeignKey(
        entity = UsuarioEntity::class,
        parentColumns = ["firebaseUid"],
        childColumns = ["profesionalUid"], // El UID del perfil que está siendo reseñado
        onDelete = ForeignKey.CASCADE
    )]
)
data class ResenaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val profesionalUid: String, // UID del perfil reseñado
    val autorNombre: String,      // Nombre de quien escribe la reseña
    val autorFotoUrl: String?,    // Foto de quien escribe la reseña
    val comentario: String,
    val rating: Float,
    val timestamp: Long
)
