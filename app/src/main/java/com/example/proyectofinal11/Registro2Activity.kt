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
import com.google.android.material.button.MaterialButton
import com.example.proyectofinal11.utils.ValidationUtils
import com.example.proyectofinal11.utils.SharedPrefsHelper

class Registro2Activity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var clienteButton: Button
    private lateinit var ambosButton: Button
    private lateinit var contratistaButton: Button
    private lateinit var oficioAutoComplete: AutoCompleteTextView
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var continuarButton: MaterialButton

    private val oficios = listOf(
        "Jardinería", "Peluquería", "Mecánica", "Carpintería",
        "Gasfitería", "Electricidad", "Pintura", "Albañilería",
        "Limpieza", "Cocina", "Plomería", "Otro"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro2)

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
        
        // Configurar para que se despliegue al hacer clic
        oficioAutoComplete.setOnClickListener {
            oficioAutoComplete.showDropDown()
        }
        oficioAutoComplete.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                oficioAutoComplete.showDropDown()
            }
        }

        setupTipoCuentaButtons()

        findViewById<android.widget.ImageButton>(R.id.back_button).setOnClickListener {
            finish()
        }

        continuarButton.setOnClickListener {
            if (validateForm()) {
                // Guardar datos del paso 2
                val data = SharedPrefsHelper.getRegistroData(this).toMutableMap()
                data[SharedPrefsHelper.KEY_REGISTRO_EMAIL] = emailEditText.text.toString()
                data[SharedPrefsHelper.KEY_REGISTRO_PASSWORD] = passwordEditText.text.toString()
                data[SharedPrefsHelper.KEY_REGISTRO_TIPO] = getSelectedAccountType()
                data[SharedPrefsHelper.KEY_REGISTRO_OFICIO] = oficioAutoComplete.text.toString()
                SharedPrefsHelper.saveRegistroData(this, data)
                
                val intent = Intent(this, Registro3Activity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun validateForm(): Boolean {
        val email = emailEditText.text?.toString() ?: ""
        val password = passwordEditText.text?.toString() ?: ""
        val confirmPassword = confirmPasswordEditText.text?.toString() ?: ""
        val oficio = oficioAutoComplete.text?.toString() ?: ""
        val tipoCuenta = getSelectedAccountType()
        
        var isValid = true

        // Validar email
        if (!ValidationUtils.isValidEmail(email)) {
            emailEditText.error = "Ingresa un correo electrónico válido"
            isValid = false
        } else {
            emailEditText.error = null
        }

        // Validar contraseña
        if (!ValidationUtils.isValidPassword(password)) {
            passwordEditText.error = "La contraseña debe tener al menos 6 caracteres e incluir letras y números"
            isValid = false
        } else {
            passwordEditText.error = null
        }

        // Validar confirmación de contraseña
        if (!ValidationUtils.passwordsMatch(password, confirmPassword)) {
            confirmPasswordEditText.error = "Las contraseñas no coinciden"
            isValid = false
        } else {
            confirmPasswordEditText.error = null
        }

        // Validar oficio (solo si es Contratista o Ambos)
        if ((tipoCuenta == "Contratista" || tipoCuenta == "Ambos") && !ValidationUtils.isNotEmpty(oficio)) {
            oficioAutoComplete.error = "Selecciona un oficio"
            isValid = false
        } else {
            oficioAutoComplete.error = null
        }

        if (!isValid) {
            Toast.makeText(this, "Por favor completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
        }

        return isValid
    }

    private fun getSelectedAccountType(): String {
        return when {
            contratistaButton.isSelected -> "Contratista"
            ambosButton.isSelected -> "Ambos"
            clienteButton.isSelected -> "Cliente"
            else -> "Contratista" // Por defecto
        }
    }

    private fun setupTipoCuentaButtons() {
        val buttons = listOf(clienteButton, ambosButton, contratistaButton)
        
        contratistaButton.isSelected = true
        contratistaButton.setTextColor(getColor(R.color.white))
        clienteButton.setTextColor(getColor(R.color.text_primary))
        ambosButton.setTextColor(getColor(R.color.text_primary))

        buttons.forEach { button ->
            button.setOnClickListener {
                buttons.forEach { it.isSelected = false }
                buttons.forEach { it.setTextColor(getColor(R.color.text_primary)) }
                
                button.isSelected = true
                button.setTextColor(getColor(R.color.white))
                
                // Limpiar error de oficio cuando cambia el tipo de cuenta
                oficioAutoComplete.error = null
            }
        }
    }
}
