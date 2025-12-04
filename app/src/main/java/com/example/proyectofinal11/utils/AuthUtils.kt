package com.example.proyectofinal11.utils

object AuthUtils {
    
    // Usuario administrador por defecto
    private const val ADMIN_EMAIL = "admin"
    private const val ADMIN_PASSWORD = "admin"
    
    /**
     * Valida las credenciales de inicio de sesión
     * @param email Email o nombre de usuario
     * @param password Contraseña
     * @return true si las credenciales son válidas, false en caso contrario
     */
    fun validateCredentials(email: String?, password: String?): Boolean {
        if (email.isNullOrBlank() || password.isNullOrBlank()) {
            return false
        }
        
        val emailTrimmed = email.trim()
        val passwordTrimmed = password.trim()
        
        // Verificar si es el usuario administrador
        if (emailTrimmed.equals(ADMIN_EMAIL, ignoreCase = true) && 
            passwordTrimmed == ADMIN_PASSWORD) {
            return true
        }
        
        // Aquí se pueden agregar más validaciones para otros usuarios
        // cuando se implemente un backend o base de datos
        
        return false
    }
    
    /**
     * Verifica si el usuario es administrador
     */
    fun isAdmin(email: String?): Boolean {
        return email?.trim()?.equals(ADMIN_EMAIL, ignoreCase = true) == true
    }
}




