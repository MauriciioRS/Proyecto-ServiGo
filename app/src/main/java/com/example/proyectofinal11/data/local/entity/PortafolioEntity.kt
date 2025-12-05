package com.example.proyectofinal11.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "portafolio_items",
    foreignKeys = [ForeignKey(
        entity = UsuarioEntity::class,
        parentColumns = ["firebaseUid"],
        childColumns = ["usuarioUid"],
        onDelete = ForeignKey.CASCADE // Si se borra el usuario, se borran sus fotos.
    )]
)
data class PortafolioEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioUid: String, // Para saber a qué usuario pertenece esta foto
    val imageUrl: String,
    val descripcion: String?
)
