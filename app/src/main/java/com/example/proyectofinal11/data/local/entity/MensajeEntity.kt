package com.example.proyectofinal11.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversaciones")
data class MensajeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val nombreProfesional: String,
    val oficioProfesional: String,
    val ultimoMensaje: String,
    val hora: String,
    val estado: String,
    val noLeidos: Int = 0
)
    