package com.example.proyectofinal11.utils

import android.content.Context
import android.content.SharedPreferences

object SharedPrefsHelper {
    private const val PREFS_NAME = "ServiGoPrefs"
    const val KEY_REGISTRO_NOMBRES = "registro_nombres"
    const val KEY_REGISTRO_APELLIDOS = "registro_apellidos"
    const val KEY_REGISTRO_DNI = "registro_dni"
    const val KEY_REGISTRO_FECHA = "registro_fecha"
    const val KEY_REGISTRO_DIRECCION = "registro_direccion"
    const val KEY_REGISTRO_DISTRITO = "registro_distrito"
    const val KEY_REGISTRO_EMAIL = "registro_email"
    const val KEY_REGISTRO_PASSWORD = "registro_password"
    const val KEY_REGISTRO_TIPO = "registro_tipo"
    const val KEY_REGISTRO_OFICIO = "registro_oficio"
    private const val KEY_CURRENT_USER_ID = "current_user_id"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // Guardar datos de registro temporal
    fun saveRegistroData(context: Context, data: Map<String, String>) {
        val prefs = getPrefs(context).edit()
        data.forEach { (key, value) ->
            prefs.putString(key, value)
        }
        prefs.apply()
    }

    fun getRegistroData(context: Context): Map<String, String> {
        val prefs = getPrefs(context)
        return mapOf(
            KEY_REGISTRO_NOMBRES to (prefs.getString(KEY_REGISTRO_NOMBRES, "") ?: ""),
            KEY_REGISTRO_APELLIDOS to (prefs.getString(KEY_REGISTRO_APELLIDOS, "") ?: ""),
            KEY_REGISTRO_DNI to (prefs.getString(KEY_REGISTRO_DNI, "") ?: ""),
            KEY_REGISTRO_FECHA to (prefs.getString(KEY_REGISTRO_FECHA, "") ?: ""),
            KEY_REGISTRO_DIRECCION to (prefs.getString(KEY_REGISTRO_DIRECCION, "") ?: ""),
            KEY_REGISTRO_DISTRITO to (prefs.getString(KEY_REGISTRO_DISTRITO, "") ?: ""),
            KEY_REGISTRO_EMAIL to (prefs.getString(KEY_REGISTRO_EMAIL, "") ?: ""),
            KEY_REGISTRO_PASSWORD to (prefs.getString(KEY_REGISTRO_PASSWORD, "") ?: ""),
            KEY_REGISTRO_TIPO to (prefs.getString(KEY_REGISTRO_TIPO, "") ?: ""),
            KEY_REGISTRO_OFICIO to (prefs.getString(KEY_REGISTRO_OFICIO, "") ?: "")
        )
    }

    fun clearRegistroData(context: Context) {
        getPrefs(context).edit().clear().apply()
    }

    fun saveCurrentUserId(context: Context, userId: String) {
        getPrefs(context).edit().putString(KEY_CURRENT_USER_ID, userId).apply()
    }

    fun getCurrentUserId(context: Context): String? {
        return getPrefs(context).getString(KEY_CURRENT_USER_ID, null)
    }

    fun clearCurrentUser(context: Context) {
        getPrefs(context).edit().remove(KEY_CURRENT_USER_ID).apply()
    }
}
