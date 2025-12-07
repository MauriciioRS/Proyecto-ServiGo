package com.example.proyectofinal11.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "historial_trabajos")
data class HistorialEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val clienteId: String,
    val contratistaId: String,
    val caso: String,
    val titulo: String,
    val usuario: String,
    val fecha: String,
    val precio: String,
    val estado: String,
    val avatarUrl: String = ""
)