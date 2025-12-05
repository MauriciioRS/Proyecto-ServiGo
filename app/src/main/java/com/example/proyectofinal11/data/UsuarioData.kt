package com.example.proyectofinal11.data


import com.example.proyectofinal11.models.Usuario
object UsuarioData {

    fun obtenerUsuario(): Usuario {
        return Usuario(
            nombre = "Mario Pérez",
            oficio = "Electricista",
            email = "mario.perez@gmail.com",
            telefono = "987654321",
            ubicacion = "Lima, Perú",
            experiencia = "5 años",
            rating = 4.8,
            reviews = 128,
            urlFotoPerfil= null
        )
    }
}
