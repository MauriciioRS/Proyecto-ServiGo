package com.example.proyectofinal11.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "portafolio_items")
data class PortafolioEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioUid: String,
    val imageUrl: String,
    val descripcion: String?
)