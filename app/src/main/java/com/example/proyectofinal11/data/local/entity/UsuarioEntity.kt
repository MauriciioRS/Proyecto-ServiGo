package com.example.proyectofinal11.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Datos del Paso 1
    val nombre: String,
    val apellido: String,
    val dni: String,
    val fechaNacimiento: String,
    val direccion: String,
    val distrito: String,

    // Datos del Paso 2
    val email: String,
    val tipoCuenta: String, // "Cliente", "Ambos", "Contratista"
    val oficio: String?,    // Puede ser nulo si es solo cliente
    val contrasena: String  // ¡IMPORTANTE! En una app real, esto debería estar encriptado.
)
