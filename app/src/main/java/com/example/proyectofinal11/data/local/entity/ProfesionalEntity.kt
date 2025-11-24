package com.example.proyectofinal11.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profesionales")
data class ProfesionalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val oficio: String,
    val rating: Double,
    val numeroReviews: Int,
    val imageUrl: String, // URL de la imagen de perfil
    val imagenFondoUrl: String, // URL de la imagen de fondo de la tarjeta
    val esFavorito: Boolean = false
)
