package com.example.proyectofinal11

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectofinal11.database.Repository
import com.example.proyectofinal11.utils.AuthUtils
import com.example.proyectofinal11.utils.SharedPrefsHelper
import com.example.proyectofinal11.utils.ValidationUtils
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InicioSesionActivity : AppCompatActivity() {
    
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var loginButton: MaterialButton
    private lateinit var registerButton: Button
    private lateinit var googleButton: MaterialButton
    private lateinit var facebookButton: MaterialButton
    
    private lateinit var repository: Repository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inicio_sesion)
        
        repository = Repository(this)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        emailInputLayout = findViewById(R.id.email_input_layout)
        passwordInputLayout = findViewById(R.id.password_input_layout)
        emailEditText = findViewById(R.id.email_edit_text)
        passwordEditText = findViewById(R.id.password_edit_text)
        loginButton = findViewById(R.id.login_button)
        registerButton = findViewById(R.id.Registrate_button)
        googleButton = findViewById(R.id.google_button)
        facebookButton = findViewById(R.id.facebook_button)
        
        loginButton.setOnClickListener {
            attemptLogin()
        }
        
        registerButton.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }
        
        googleButton.setOnClickListener {
            openWebsite("https://www.google.com")
        }
        
        facebookButton.setOnClickListener {
            openWebsite("https://www.facebook.com")
        }
    }

    private fun attemptLogin() {
        val email = emailEditText.text?.toString() ?: ""
        val password = passwordEditText.text?.toString() ?: ""
        
        // 1. Check Admin (Hardcoded check)
        if (AuthUtils.validateCredentials(email, password)) {
            // Guardar un ID temporal para el admin para que no se cierre la sesión
            SharedPrefsHelper.saveCurrentUserId(this, "admin_id")
            startMainActivity()
            return
        }
        
        // 2. Validate Input Format
        if (!validateInputFormat(email, password)) {
            return
        }
        
        // 3. Check Database
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val user = repository.login(email, password)
                withContext(Dispatchers.Main) {
                    if (user != null) {
                        SharedPrefsHelper.saveCurrentUserId(this@InicioSesionActivity, user.id)
                        startMainActivity()
                    } else {
                        Toast.makeText(this@InicioSesionActivity, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@InicioSesionActivity, "Error al iniciar sesión: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun validateInputFormat(email: String, password: String): Boolean {
        var isValid = true
        
        if (!ValidationUtils.isValidEmail(email)) {
            emailInputLayout.error = "Ingresa un correo electrónico válido"
            isValid = false
        } else {
            emailInputLayout.error = null
        }
        
        if (!ValidationUtils.isNotEmpty(password)) {
            passwordInputLayout.error = "La contraseña es requerida"
            isValid = false
        } else {
            passwordInputLayout.error = null
        }
        
        return isValid
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun openWebsite(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "No se pudo abrir el sitio web", Toast.LENGTH_SHORT).show()
        }
    }
}
