package com.example.proyectofinal11.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "historial_trabajos")
data class HistorialEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // --- NUEVOS CAMPOS CLAVE ---
    val clienteId: Int,       // ID del usuario que solicita el trabajo
    val contratistaId: Int,   // ID del usuario que realiza el trabajo

    // --- Mantenemos los otros datos ---
    val caso: String,
    val titulo: String,
    val usuario: String, // Este campo ahora representará a la "otra persona" en la transacción
    val fecha: String,
    val precio: String,
    val estado: String,       // "En proceso", "Finalizado", "Cancelado"
    val avatarUrl: String = ""
)
