package com.example.proyectofinal11

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class Registro2Activity : AppCompatActivity() {

    // Vistas
    private lateinit var emailEditText: TextInputEditText
    private lateinit var tipoCuentaToggleGroup: MaterialButtonToggleGroup
    private lateinit var oficioTextInputLayout: TextInputLayout
    private lateinit var oficioAutoComplete: AutoCompleteTextView
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var confirmPasswordEditText: TextInputEditText
    private lateinit var continuarButton: com.google.android.material.button.MaterialButton

    private val oficiosDomesticos = listOf(
        "Gasfitería", "Electricidad", "Pintura", "Albañilería", "Jardinería",
        "Limpieza de hogar", "Carpintería", "Cerrajería",
        "Reparación de electrodomésticos", "Cuidado de ancianos", "Otro"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro2)

        inicializarVistas()
        configurarListeners()

        val oficioAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, oficiosDomesticos)
        oficioAutoComplete.setAdapter(oficioAdapter)
    }

    private fun inicializarVistas() {
        emailEditText = findViewById(R.id.email_edit_text)
        tipoCuentaToggleGroup = findViewById(R.id.tipo_cuenta_segmented)
        oficioTextInputLayout = findViewById(R.id.oficio_text_input_layout)
        oficioAutoComplete = findViewById(R.id.oficio_auto_complete)
        passwordEditText = findViewById(R.id.password_edit_text)
        confirmPasswordEditText = findViewById(R.id.confirm_password_edit_text)
        continuarButton = findViewById(R.id.continuar_button)
    }

    private fun configurarListeners() {
        findViewById<android.widget.ImageButton>(R.id.back_button).setOnClickListener { finish() }

        tipoCuentaToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                oficioTextInputLayout.visibility = if (checkedId == R.id.cliente_button) View.GONE else View.VISIBLE
            }
        }
        tipoCuentaToggleGroup.check(R.id.cliente_button) // Estado inicial

        continuarButton.setOnClickListener {
            // Su única responsabilidad es validar y pasar los datos al siguiente paso
            validarYContinuar()
        }
    }

    private fun validarYContinuar() {
        val email = emailEditText.text.toString().trim()
        val contrasena = passwordEditText.text.toString()
        val confirmarContrasena = confirmPasswordEditText.text.toString()
        val oficio = oficioAutoComplete.text.toString()
        val tipoCuenta = when (tipoCuentaToggleGroup.checkedButtonId) {
            R.id.cliente_button -> "Cliente"
            R.id.ambos_button -> "Ambos"
            R.id.contratista_button -> "Contratista"
            else -> ""
        }

        // Validación de los campos de esta pantalla
        if (email.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Correo y contraseña son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }
        if (contrasena.length < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            return
        }
        if (contrasena != confirmarContrasena) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return
        }
        if (tipoCuenta != "Cliente" && oficio.isEmpty()) {
            Toast.makeText(this, "Por favor, selecciona un oficio", Toast.LENGTH_SHORT).show()
            return
        }

        // --- EMPAQUETAR Y ENVIAR TODOS LOS DATOS ---
        val intentPaso3 = Intent(this, Registro3Activity::class.java).apply {
            // 1. Pasa los datos que recibiste del Paso 1
            putExtras(intent.extras ?: Bundle()) // Esto reenvía todos los extras del intent anterior

            // 2. Añade los nuevos datos del Paso 2
            putExtra("EMAIL", email)
            putExtra("CONTRASENA", contrasena)
            putExtra("TIPO_CUENTA", tipoCuenta)
            putExtra("OFICIO", if (tipoCuenta != "Cliente") oficio else null)
        }
        startActivity(intentPaso3)
    }
}
