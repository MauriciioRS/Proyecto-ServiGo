package com.example.proyectofinal11

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.proyectofinal11.data.local.database.ServiGoDatabase
import com.example.proyectofinal11.data.local.entity.UsuarioEntity
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Registro2Activity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var clienteButton: Button
    private lateinit var ambosButton: Button
    private lateinit var contratistaButton: Button
    private lateinit var oficioAutoComplete: AutoCompleteTextView
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var continuarButton: MaterialButton

    // Añade la variable para la base de datos
    private lateinit var db: ServiGoDatabase

    private val oficios = listOf(
        "Jardinería", "Peluquería", "Mecánica", "Carpintería",
        "Gasfitería", "Electricidad", "Pintura", "Albañilería",
        "Limpieza", "Cocina", "Plomería", "Otro"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro2)

        // Inicializa la base de datos
        db = ServiGoDatabase.getDatabase(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        emailEditText = findViewById(R.id.email_edit_text)
        clienteButton = findViewById(R.id.cliente_button)
        ambosButton = findViewById(R.id.ambos_button)
        contratistaButton = findViewById(R.id.contratista_button)
        oficioAutoComplete = findViewById(R.id.oficio_auto_complete)
        passwordEditText = findViewById(R.id.password_edit_text)
        confirmPasswordEditText = findViewById(R.id.confirm_password_edit_text)
        continuarButton = findViewById(R.id.continuar_button)

        val oficioAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, oficios)
        oficioAutoComplete.setAdapter(oficioAdapter)

        setupTipoCuentaButtons()

        findViewById<android.widget.ImageButton>(R.id.back_button).setOnClickListener {
            finish()
        }

        // ===== CÓDIGO UNIDO Y MEJORADO =====
        continuarButton.setOnClickListener {
            // 1. Recibir los datos del Paso 1
            val nombre = intent.getStringExtra("NOMBRE") ?: ""
            val apellido = intent.getStringExtra("APELLIDO") ?: ""
            val dni = intent.getStringExtra("DNI") ?: ""
            val fechaNac = intent.getStringExtra("FECHA_NAC") ?: ""
            val direccion = intent.getStringExtra("DIRECCION") ?: ""
            val distrito = intent.getStringExtra("DISTRITO") ?: ""

            // 2. Recolectar los datos del Paso 2
            val email = emailEditText.text.toString().trim()
            val contrasena = passwordEditText.text.toString()
            val confirmarContrasena = confirmPasswordEditText.text.toString()
            val oficio = oficioAutoComplete.text.toString()
            val tipoCuenta = when {
                clienteButton.isSelected -> "Cliente"
                ambosButton.isSelected -> "Ambos"
                contratistaButton.isSelected -> "Contratista"
                else -> "" // Nunca debería pasar si uno está seleccionado por defecto
            }

            // Validaciones
            if(email.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Correo y contraseña son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(contrasena != confirmarContrasena) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 3. Crear el objeto UsuarioEntity
            val nuevoUsuario = UsuarioEntity(
                nombre = nombre,
                apellido = apellido,
                dni = dni,
                fechaNacimiento = fechaNac,
                direccion = direccion,
                distrito = distrito,
                email = email,
                contrasena = contrasena, // IMPORTANTE: En una app real, esto debe encriptarse
                tipoCuenta = tipoCuenta,
                oficio = if (tipoCuenta != "Cliente") oficio else null // El oficio es nulo si es solo cliente
            )

            // 4. Guardar en la base de datos usando una corutina
            lifecycleScope.launch(Dispatchers.IO) {
                db.usuarioDao().insertarUsuario(nuevoUsuario)
                // Opcional: Podrías verificar si el email ya existe antes de insertar
            }

            // 5. Navegar al paso final
            val intent = Intent(this, Registro3Activity::class.java)
            // Limpiamos las actividades anteriores para que el usuario no pueda volver al registro
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish() // Cierra esta actividad
        }
    }

    private fun setupTipoCuentaButtons() {
        val buttons = listOf(clienteButton, ambosButton, contratistaButton)

        // Seleccionamos "Cliente" por defecto para que sea más intuitivo
        clienteButton.isSelected = true
        clienteButton.setTextColor(getColor(R.color.white))
        ambosButton.setTextColor(getColor(R.color.text_primary))
        contratistaButton.setTextColor(getColor(R.color.text_primary))

        buttons.forEach { button ->
            button.setOnClickListener {
                buttons.forEach { it.isSelected = false }
                buttons.forEach { it.setTextColor(getColor(R.color.text_primary)) }

                button.isSelected = true
                button.setTextColor(getColor(R.color.white))
            }
        }
    }
}
