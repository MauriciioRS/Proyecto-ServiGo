package com.example.proyectofinal11.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Esta entidad representa UNA CONVERSACIÓN o "TRABAJO" en tu lista de chats.
@Entity(tableName = "conversaciones")
data class MensajeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,             // "Trabajo 1", "Servicio de Pintura", etc.
    val nombreProfesional: String,  // Nombre de la otra persona en el chat
    val oficioProfesional: String?, // Oficio de la otra persona (puede ser nulo si es cliente)
    var ultimoMensaje: String,      // El texto del último mensaje para la vista previa
    var hora: String,               // La hora del último mensaje
    val estado: String,             // "Pendiente", "Proceso", "Terminado"
    val noLeidos: Int = 0,


    val conversacionId: String,  // ID único que se usará en Firestore (ej: uid1_uid2)
    val receptorUid: String      // El UID de Firebase de la otra persona
)
