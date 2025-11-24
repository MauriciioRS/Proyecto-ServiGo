package com.example.proyectofinal11

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.proyectofinal11.data.local.database.ServiGoDatabase
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InicioSesionActivity : AppCompatActivity() {

    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: MaterialButton
    private lateinit var registerButton: Button

    // 1. Declarar la variable para la base de datos
    private lateinit var db: ServiGoDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inicio_sesion)

        // 2. Inicializar la base de datos
        db = ServiGoDatabase.getDatabase(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        emailEditText = findViewById(R.id.email_edit_text)
        passwordEditText = findViewById(R.id.password_edit_text)
        loginButton = findViewById(R.id.login_button)
        registerButton = findViewById(R.id.Registrate_button)

        // --- LÓGICA DE INICIO DE SESIÓN UNIDA ---
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val contrasena = passwordEditText.text.toString()

            // Validar que los campos no estén vacíos
            if (email.isBlank() || contrasena.isBlank()) {
                Toast.makeText(this, "Por favor, ingresa correo y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Verificar credenciales en la base de datos en un hilo secundario
            lifecycleScope.launch(Dispatchers.IO) {
                val usuario = db.usuarioDao().iniciarSesion(email, contrasena)

                // Volver al hilo principal para actualizar la UI
                withContext(Dispatchers.Main) {
                    if (usuario != null) {
                        // ¡Éxito! El usuario existe
                        Toast.makeText(this@InicioSesionActivity, "Bienvenido, ${usuario.nombre}", Toast.LENGTH_SHORT).show()

                        // Navegar a MainActivity y limpiar el historial de actividades
                        val intent = Intent(this@InicioSesionActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        // Error: credenciales incorrectas
                        Toast.makeText(this@InicioSesionActivity, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        registerButton.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }
    }
}
