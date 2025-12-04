package com.example.proyectofinal11.utils

import android.util.Patterns
import java.util.regex.Pattern

object ValidationUtils {

    /**
     * Valida si un email tiene un formato válido
     */
    fun isValidEmail(email: String?): Boolean {
        return !email.isNullOrBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Valida si un DNI peruano es válido (8 dígitos)
     */
    fun isValidDNI(dni: String?): Boolean {
        if (dni.isNullOrBlank()) return false
        // DNI peruano tiene 8 dígitos
        val dniPattern = Pattern.compile("^[0-9]{8}$")
        return dniPattern.matcher(dni.trim()).matches()
    }

    /**
     * Valida si una contraseña cumple con los requisitos mínimos
     * - Mínimo 6 caracteres
     * - Al menos una letra y un número
     */
    fun isValidPassword(password: String?): Boolean {
        if (password.isNullOrBlank()) return false
        if (password.length < 6) return false
        
        val hasLetter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }
        
        return hasLetter && hasDigit
    }

    /**
     * Valida si dos contraseñas coinciden
     */
    fun passwordsMatch(password: String?, confirmPassword: String?): Boolean {
        if (password.isNullOrBlank() || confirmPassword.isNullOrBlank()) return false
        return password == confirmPassword
    }

    /**
     * Valida si un texto no está vacío
     */
    fun isNotEmpty(text: String?): Boolean {
        return !text.isNullOrBlank()
    }

    /**
     * Valida si un nombre es válido (solo letras y espacios, mínimo 2 caracteres)
     */
    fun isValidName(name: String?): Boolean {
        if (name.isNullOrBlank()) return false
        val trimmed = name.trim()
        if (trimmed.length < 2) return false
        
        // Solo letras, espacios y algunos caracteres especiales comunes en nombres
        val namePattern = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$")
        return namePattern.matcher(trimmed).matches()
    }

    /**
     * Valida si una fecha de nacimiento tiene formato válido
     * Formato esperado: DD / MM / YY
     */
    fun isValidDate(date: String?): Boolean {
        if (date.isNullOrBlank()) return false
        val trimmed = date.trim()
        
        // Formato: DD / MM / YY
        val datePattern = Pattern.compile("^\\d{2}\\s*/\\s*\\d{2}\\s*/\\s*\\d{2}$")
        if (!datePattern.matcher(trimmed).matches()) return false
        
        // Validar que los valores sean razonables
        val parts = trimmed.split("/").map { it.trim().toIntOrNull() ?: return false }
        if (parts.size != 3) return false
        
        val day = parts[0]
        val month = parts[1]
        val year = parts[2]
        
        // Validar rangos
        if (day < 1 || day > 31) return false
        if (month < 1 || month > 12) return false
        if (year < 0 || year > 99) return false
        
        return true
    }

    /**
     * Valida si una dirección es válida (mínimo 5 caracteres)
     */
    fun isValidAddress(address: String?): Boolean {
        if (address.isNullOrBlank()) return false
        return address.trim().length >= 5
    }

    /**
     * Valida si un distrito es válido (mínimo 2 caracteres)
     */
    fun isValidDistrict(district: String?): Boolean {
        if (district.isNullOrBlank()) return false
        return district.trim().length >= 2
    }
}

