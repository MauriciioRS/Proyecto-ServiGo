package com.example.proyectofinal11

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

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

        setupTipoCuentaButtons()

        findViewById<android.widget.ImageButton>(R.id.back_button).setOnClickListener {
            finish()
        }

        continuarButton.setOnClickListener {
            val intent = Intent(this, Registro3Activity::class.java)
            startActivity(intent)
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
            }
        }
    }
}

