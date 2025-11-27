package com.example.proyectofinal11.models

data class Mensaje(
    val id: String,
    val nombre: String,
    val profesion: String?,
    val ultimoMensaje: String,
    val hora: String,
    val estado: String, // "Pendiente", "Proceso", "Terminado"
    val unreadCount: Int = 0,
    val avatarUrl: String? = null
) {
    val displayName: String
        get() = if (!profesion.isNullOrBlank()) "$nombre - $profesion" else nombre
}