package com.example.proyectofinal11.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "resenas")
data class ResenaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val profesionalUid: String,
    val clienteUid: String,
    val clienteNombre: String,
    val clienteFotoUrl: String?,
    val calificacion: Double,
    val comentario: String,
    val fecha: Long
)