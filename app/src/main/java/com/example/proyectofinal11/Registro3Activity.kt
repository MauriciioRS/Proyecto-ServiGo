// C:/Users/miyuk/AndroidStudioProjects/Proyecto-ServiGo/app/src/main/java/com/example/proyectofinal11/Registro3Activity.kt
// VERSIÓN FINAL QUE NO USA FIREBASE STORAGE

package com.example.proyectofinal11

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.proyectofinal11.data.local.database.ServiGoDatabase
import com.example.proyectofinal11.data.local.entity.UsuarioEntity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
// Se elimina la importación de FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream

class Registro3Activity : AppCompatActivity() {

    // Vistas
    private lateinit var previewFoto: ImageView
    private lateinit var botonSubirFoto: MaterialButton
    private lateinit var finalizarButton: MaterialButton
    private lateinit var progressBar: ProgressBar

    // Lógica
    private var imagenUri: Uri? = null

    // Servicios
    private lateinit var auth: FirebaseAuth
    // Se elimina la variable 'storage'
    private lateinit var db: ServiGoDatabase

    private val galeriaLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            imagenUri = uri
            previewFoto.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro3)

        inicializarVistas()
        inicializarServicios()
        configurarListeners()
    }

    private fun inicializarVistas() {
        previewFoto = findViewById(R.id.preview_foto)
        botonSubirFoto = findViewById(R.id.boton_subir_foto)
        finalizarButton = findViewById(R.id.finalizar_button)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun inicializarServicios() {
        auth = FirebaseAuth.getInstance()
        // Se elimina la inicialización de 'storage'
        db = ServiGoDatabase.getDatabase(this)
    }

    private fun configurarListeners() {
        botonSubirFoto.setOnClickListener {
            galeriaLauncher.launch("image/*")
        }
        finalizarButton.setOnClickListener {
            finalizarRegistroYVerificacion()
        }
    }

    private fun finalizarRegistroYVerificacion() {
        val uri = imagenUri
        val dni = intent.getStringExtra("DNI")

        if (uri == null) {
            Toast.makeText(this, "Por favor, selecciona una foto de perfil", Toast.LENGTH_SHORT).show()
            return
        }
        if (dni.isNullOrEmpty()) {
            Toast.makeText(this, "Error: No se recibió el DNI del paso anterior", Toast.LENGTH_LONG).show()
            return
        }

        mostrarCargando(true)

        lifecycleScope.launch {
            try {
                // --- CREAMOS EL USUARIO EN FIREBASE AUTH (ESTO NO CAMBIA) ---
                val email = intent.getStringExtra("EMAIL") ?: ""
                val contrasena = intent.getStringExtra("CONTRASENA") ?: ""

                val authResult = auth.createUserWithEmailAndPassword(email, contrasena).await()
                val firebaseUser = authResult.user ?: throw IllegalStateException("Error al crear la cuenta en Firebase.")

                // --- NUEVA LÓGICA: CONVERTIR IMAGEN A TEXTO (BASE64) ---
                val fotoEnBase64 = withContext(Dispatchers.IO) {
                    convertirUriABase64(uri)
                }
                if (fotoEnBase64 == null) {
                    throw Exception("No se pudo procesar la imagen seleccionada.")
                }

                // --- GUARDAMOS EL USUARIO EN LA BASE DE DATOS LOCAL (ROOM) ---
                // --- GUARDAMOS EL USUARIO EN LA BASE DE DATOS LOCAL (ROOM) ---
                val usuarioFinal = UsuarioEntity(
                    firebaseUid = firebaseUser.uid,
                    nombre = intent.getStringExtra("NOMBRE") ?: "",
                    apellido = intent.getStringExtra("APELLIDO") ?: "",
                    email = email,
                    contrasena = contrasena,
                    fotoPerfilBase64 = fotoEnBase64,
                    dni = dni,
                    // --- CORRECCIÓN: Añadir el operador Elvis (?:) para manejar valores nulos ---
                    fechaNacimiento = intent.getStringExtra("FECHA_NAC") ?: "",
                    direccion = intent.getStringExtra("DIRECCION") ?: "",
                    distrito = intent.getStringExtra("DISTRITO") ?: "",
                    tipoCuenta = intent.getStringExtra("TIPO_CUENTA") ?: "",
                    oficio = intent.getStringExtra("OFICIO") ?: "", // Asumiendo que 'oficio' también puede ser nulo
                    estadoVerificacion = "Aprobado",

                    rating = null,
                    numeroReviews = null,
                    imagenFondoUrl = null   )


                withContext(Dispatchers.IO) {
                    db.usuarioDao().insertarUsuario(usuarioFinal)
                }

                // --- NAVEGAR A LA PANTALLA DE ÉXITO ---
                val exitoIntent = Intent(this@Registro3Activity, MensajeActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(exitoIntent)
                finish()

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    mostrarCargando(false)
                    Toast.makeText(this@Registro3Activity, "Error en el registro: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // --- NUEVA FUNCIÓN AUXILIAR PARA CONVERTIR LA IMAGEN A TEXTO ---
    private fun convertirUriABase64(uri: Uri): String? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val byteArrayOutputStream = ByteArrayOutputStream()
            // Comprime la imagen a formato JPEG con calidad 80 (buen balance entre calidad y tamaño)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            // Convierte los bytes de la imagen a un string de texto
            Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // --- Las funciones de simulación y de carga no necesitan cambios ---
    private suspend fun simularVerificacionIdentidad(dni: String, fotoUri: Uri): Boolean {
        return dni.isNotEmpty() && fotoUri.toString().isNotEmpty()
    }

    private suspend fun simularVerificacionAntecedentes(dni: String): Boolean {
        if (dni == "12345678") {
            return true // Tiene antecedentes
        }
        return false // No tiene antecedentes
    }

    private fun mostrarCargando(estaCargando: Boolean) {
        progressBar.visibility = if (estaCargando) View.VISIBLE else View.GONE
        finalizarButton.isEnabled = !estaCargando
        botonSubirFoto.isEnabled = !estaCargando
    }
}
