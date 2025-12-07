package com.example.proyectofinal11.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

// Modelo simple y único para un mensaje en Firestore
data class Message(
    val emisorUid: String = "",
    val texto: String = "",
    @ServerTimestamp val timestamp: Date? = null // Firestore asigna la hora del servidor
)
