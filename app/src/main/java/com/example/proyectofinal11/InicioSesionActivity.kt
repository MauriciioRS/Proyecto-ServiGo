
package com.example.proyectofinal11

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.proyectofinal11.data.local.database.ServiGoDatabase
import com.example.proyectofinal11.data.local.entity.UsuarioEntity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InicioSesionActivity : AppCompatActivity() {

    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: MaterialButton
    private lateinit var registerButton: Button
    private lateinit var googleButton: MaterialButton

    private lateinit var db: ServiGoDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    // Launcher para el flujo de Google Sign-In
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                // Google Sign-In fue exitoso, ahora autenticamos con Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("GoogleSignIn", "Obtenido token de Google: ${account.idToken != null}")

                // --- CORRECCIÓN #1: Crear la credencial de Google ---
                // Le pasamos el idToken a GoogleAuthProvider para crear una AuthCredential
                val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)

                // Ahora llamamos a la función con el tipo de dato correcto
                firebaseAuthWithGoogle(credential)

            } catch (e: ApiException) {
                Log.w("GoogleSignIn", "Fallo el inicio de sesión con Google", e)
                Toast.makeText(this, "Falló el inicio de sesión con Google.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_sesion)

        // Inicializar vistas y servicios
        inicializarVistas()
        setupEmailPasswordLogin()
        setupGoogleLogin()

        // Listener para el botón de registro
        registerButton.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }
    }

    private fun inicializarVistas() {
        db = ServiGoDatabase.getDatabase(this)
        auth = FirebaseAuth.getInstance()

        emailEditText = findViewById(R.id.email_edit_text)
        passwordEditText = findViewById(R.id.password_edit_text)
        loginButton = findViewById(R.id.login_button)
        registerButton = findViewById(R.id.Registrate_button)
        googleButton = findViewById(R.id.google_button)
    }

    // --- LÓGICA DE INICIO DE SESIÓN CON CORREO Y CONTRASEÑA ---
    private fun setupEmailPasswordLogin() {
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Por favor, ingresa correo y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Autenticación con Firebase Auth
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("EmailLogin", "Inicio de sesión con Firebase exitoso.")
                        irAPantallaPrincipal("Bienvenido de nuevo")
                    } else {
                        Log.w("EmailLogin", "Error en inicio de sesión", task.exception)
                        Toast.makeText(this, "Correo o contraseña incorrectos.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    // --- LÓGICA DE INICIO DE SESIÓN CON GOOGLE (UNIFICADA) ---
    private fun setupGoogleLogin() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        googleButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }
    }

    private fun firebaseAuthWithGoogle(credential: com.google.firebase.auth.AuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        // Verificamos y/o registramos el usuario en nuestra DB local (Room)
                        registrarUsuarioDeGoogleEnDbLocal(firebaseUser)
                    }
                } else {
                    Log.w("GoogleSignIn", "Falló la autenticación con Firebase", task.exception)
                    Toast.makeText(this, "Falló la autenticación con Firebase.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun registrarUsuarioDeGoogleEnDbLocal(firebaseUser: com.google.firebase.auth.FirebaseUser) {
        lifecycleScope.launch(Dispatchers.IO) {
            // Comprobamos si el usuario ya existe en nuestra base de datos local
            val usuarioExistente = db.usuarioDao().obtenerUsuarioPorFirebaseUid(firebaseUser.uid)

            if (usuarioExistente == null) {
                // --- EL USUARIO ES NUEVO: Lo creamos en Room ---
                Log.d("GoogleSignIn", "Usuario no encontrado en DB local. Creando entrada...")

                // --- CORRECCIÓN #2: Crear el UsuarioEntity con TODOS los campos ---
                // Dividimos el nombre para obtener nombre y apellido
                val nombreCompleto = firebaseUser.displayName?.split(" ") ?: listOf()
                val nombre = nombreCompleto.firstOrNull() ?: "Usuario"
                val apellido = nombreCompleto.drop(1).joinToString(" ")

                val nuevoUsuario = UsuarioEntity(
                    firebaseUid = firebaseUser.uid,
                    nombre = nombre,
                    apellido = apellido,
                    email = firebaseUser.email ?: "",
                    contrasena = null, // Correcto: no hay contraseña con Google Sign-In
                    fotoPerfilBase64 = firebaseUser.photoUrl?.toString(),
                    // Proveemos valores nulos o por defecto para los campos del formulario
                    dni = null,
                    fechaNacimiento = null,
                    direccion = null,
                    distrito = null,
                    tipoCuenta = "Cliente", // Un valor por defecto razonable
                    oficio = null,
                    estadoVerificacion = "Verificado por Google" ,


                    // --- CAMPOS AÑADIDOS PARA SOLUCIONAR EL ERROR ---
                    rating = null,
                    numeroReviews = null,
                    imagenFondoUrl = null
                )

                db.usuarioDao().insertarUsuario(nuevoUsuario)
                Log.d("GoogleSignIn", "Nuevo usuario de Google guardado en Room.")
            } else {
                Log.d("GoogleSignIn", "Usuario de Google ya existe en Room.")
                // Opcional: Podrías actualizar el nombre o la foto si han cambiado
            }

            // Finalmente, navegamos a la pantalla principal
            withContext(Dispatchers.Main) {
                irAPantallaPrincipal("Bienvenido, ${firebaseUser.displayName ?: "Usuario"}")
            }
        }
    }

    private fun irAPantallaPrincipal(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java) // Asegúrate que esta es tu actividad principal
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
