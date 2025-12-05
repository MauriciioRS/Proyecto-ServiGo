package com.example.proyectofinal11.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Esta entidad representa UN SOLO mensaje dentro de una conversación.
@Entity(tableName = "mensajes_chat")
data class MensajeChatEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conversacionId: String, // ID para agrupar todos los mensajes de un mismo chat
    val emisorUid: String,      // UID de Firebase de quien envía
    val receptorUid: String,    // UID de Firebase de quien recibe
    val texto: String,          // El contenido del mensaje
    val timestamp: Long,        // La hora en que se envió, para ordenarlos
    var estado: String = "Enviado" // "Enviado", "Leído", etc.
)
