package com.example.proyectofinal11

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.example.proyectofinal11.database.entities.UsuarioEntity
import com.example.proyectofinal11.utils.SharedPrefsHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class Registro3Activity : AppCompatActivity() {

    private lateinit var loginButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro3)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loginButton = findViewById(R.id.login_button)

        loginButton.setOnClickListener {
            saveUsuarioToDatabase()
        }
    }
    
    private fun saveUsuarioToDatabase() {
        val data = SharedPrefsHelper.getRegistroData(this)
        
        val usuarioId = UUID.randomUUID().toString()
        val usuario = UsuarioEntity(
            id = usuarioId,
            nombres = data[SharedPrefsHelper.KEY_REGISTRO_NOMBRES] ?: "",
            apellidos = data[SharedPrefsHelper.KEY_REGISTRO_APELLIDOS] ?: "",
            dni = data[SharedPrefsHelper.KEY_REGISTRO_DNI] ?: "",
            fechaNacimiento = data[SharedPrefsHelper.KEY_REGISTRO_FECHA] ?: "",
            direccion = data[SharedPrefsHelper.KEY_REGISTRO_DIRECCION] ?: "",
            distrito = data[SharedPrefsHelper.KEY_REGISTRO_DISTRITO] ?: "",
            email = data[SharedPrefsHelper.KEY_REGISTRO_EMAIL] ?: "",
            password = data[SharedPrefsHelper.KEY_REGISTRO_PASSWORD] ?: "",
            tipoCuenta = data[SharedPrefsHelper.KEY_REGISTRO_TIPO] ?: "Cliente",
            oficio = data[SharedPrefsHelper.KEY_REGISTRO_OFICIO].takeIf { it?.isNotEmpty() == true }
        )
        
        val app = application as ServiGoApplication
        CoroutineScope(Dispatchers.IO).launch {
            try {
                app.repository.insertUsuario(usuario)
                SharedPrefsHelper.saveCurrentUserId(this@Registro3Activity, usuarioId)
                SharedPrefsHelper.clearRegistroData(this@Registro3Activity)
                
                runOnUiThread {
                    Toast.makeText(this@Registro3Activity, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@Registro3Activity, InicioSesionActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@Registro3Activity, "Error al registrar usuario: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}


