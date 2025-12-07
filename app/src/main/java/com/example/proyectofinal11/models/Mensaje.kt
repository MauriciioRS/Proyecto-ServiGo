package com.example.proyectofinal11.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

// Renombrado a MensajeModelo para evitar conflicto con 'Message'
data class MensajeModelo(
    val emisorUid: String = "",
    val texto: String = "",
    @ServerTimestamp val timestamp: Date? = null
)
