package com.example.proyectofinal11.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios") // El 'unique index' en email ya no es necesario con Firebase UID como PK
data class UsuarioEntity(
    @PrimaryKey val firebaseUid: String, // Clave Primaria Única de Firebase

    // --- DATOS BÁSICOS DE REGISTRO (Todos los tienen) ---
    val nombre: String,
    val apellido: String,
    val email: String,
    val dni: String?,
    val contrasena: String?,

    val tipoCuenta: String, // "Cliente", "Contratista", "Ambos"

    // --- DATOS DE PERFIL (Pueden ser nulos si no aplican) ---
    val fotoPerfilBase64: String?,
    val fechaNacimiento: String?,
    val direccion: String?,
    val distrito: String?,
    val estadoVerificacion: String,

    // --- DATOS DE PROFESIONAL (Solo para "Contratista" o "Ambos") ---
    val oficio: String?,
    val rating: Double?,
    val numeroReviews: Int?,
    val imagenFondoUrl: String?, // URL para la tarjeta de explorar
    val esFavorito: Boolean = false
)
