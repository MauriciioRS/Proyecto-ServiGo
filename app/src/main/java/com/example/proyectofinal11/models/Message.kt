package com.example.proyectofinal11.models

import java.util.Date

data class Message(
    val emisorUid: String = "",
    val texto: String = "",
    val timestamp: Date = Date()
)
