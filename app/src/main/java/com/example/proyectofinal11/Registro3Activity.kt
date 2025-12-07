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
import com.google.firebase.auth.FirebaseUser
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
            var firebaseUser: FirebaseUser? = null
            try {
                // --- 1. CREAMOS EL USUARIO EN FIREBASE AUTH ---
                val email = intent.getStringExtra("EMAIL") ?: ""
                val contrasena = intent.getStringExtra("CONTRASENA") ?: ""

                val authResult = auth.createUserWithEmailAndPassword(email, contrasena).await()
                firebaseUser = authResult.user ?: throw IllegalStateException("Error al crear la cuenta en Firebase.")

                try {
                    // --- 2. CONVERTIR IMAGEN A TEXTO (BASE64) ---
                    val fotoEnBase64 = withContext(Dispatchers.IO) {
                        convertirUriABase64(uri)
                    }
                    if (fotoEnBase64 == null) {
                        throw Exception("No se pudo procesar la imagen seleccionada. Intenta con otra foto.")
                    }

                    // --- 3. GUARDAMOS EL USUARIO EN LA BASE DE DATOS LOCAL (ROOM) ---
                    val usuarioFinal = UsuarioEntity(
                        firebaseUid = firebaseUser.uid,
                        nombre = intent.getStringExtra("NOMBRE") ?: "",
                        apellido = intent.getStringExtra("APELLIDO") ?: "",
                        email = email,
                        contrasena = contrasena,
                        fotoPerfilBase64 = fotoEnBase64,
                        dni = dni,
                        fechaNacimiento = intent.getStringExtra("FECHA_NAC") ?: "",
                        direccion = intent.getStringExtra("DIRECCION") ?: "",
                        distrito = intent.getStringExtra("DISTRITO") ?: "",
                        tipoCuenta = intent.getStringExtra("TIPO_CUENTA") ?: "",
                        oficio = intent.getStringExtra("OFICIO") ?: "",
                        estadoVerificacion = "Aprobado",
                        rating = null,
                        numeroReviews = null,
                        imagenFondoUrl = null
                    )

                    withContext(Dispatchers.IO) {
                        db.usuarioDao().insertarUsuario(usuarioFinal)
                    }

                } catch (e: Exception) {
                    // --- ROLLBACK: Si falla la BD o la Imagen, borramos el usuario de Firebase ---
                    // Esto permite al usuario intentar de nuevo con el mismo correo sin recibir el error "Email en uso".
                    try {
                        firebaseUser.delete().await()
                    } catch (deleteEx: Exception) {
                        // Si falla el borrado, no podemos hacer mucho más, pero al menos lo intentamos.
                        deleteEx.printStackTrace()
                    }
                    throw e // Re-lanzamos la excepción original para mostrar el mensaje correcto al usuario
                }

                // --- 4. NAVEGAR A LA PANTALLA DE ÉXITO ---
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

    private fun convertirUriABase64(uri: Uri): String? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            
            // Redimensionar si es muy grande para evitar errores de memoria o base de datos
            val maxDimension = 1024
            val scale = Math.min(maxDimension.toFloat() / bitmap.width, maxDimension.toFloat() / bitmap.height)
            val finalBitmap = if (scale < 1) {
                 Bitmap.createScaledBitmap(bitmap, (bitmap.width * scale).toInt(), (bitmap.height * scale).toInt(), true)
            } else {
                 bitmap
            }

            val byteArrayOutputStream = ByteArrayOutputStream()
            // Comprime la imagen a formato JPEG
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun mostrarCargando(estaCargando: Boolean) {
        progressBar.visibility = if (estaCargando) View.VISIBLE else View.GONE
        finalizarButton.isEnabled = !estaCargando
        botonSubirFoto.isEnabled = !estaCargando
    }
}
