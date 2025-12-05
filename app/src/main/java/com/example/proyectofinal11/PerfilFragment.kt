// CÓDIGO CORREGIDO Y MEJORADO
package com.example.proyectofinal11

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.proyectofinal11.data.local.database.ServiGoDatabase
import com.example.proyectofinal11.data.local.entity.UsuarioEntity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PerfilFragment : Fragment() {

    // --- Servicios de Firebase y Base de Datos Local ---
    private lateinit var auth: FirebaseAuth
    private lateinit var db: ServiGoDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = FirebaseAuth.getInstance()
        db = ServiGoDatabase.getDatabase(requireContext())
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cargarDatosUsuario(view)

        // El resto de tu lógica de listeners está bien y se mantiene
        setupListeners(view)
    }

    private fun cargarDatosUsuario(view: View) {
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            // No hay usuario logueado, lo ideal es enviarlo al login
            irAPantallaDeLogin()
            return
        }

        // Usamos una corrutina para buscar al usuario en Room usando su UID de Firebase
        lifecycleScope.launch(Dispatchers.IO) {
            Log.d("PerfilFragment", "Buscando usuario con UID: ${firebaseUser.uid}")
            val usuarioEntity = db.usuarioDao().obtenerUsuarioPorFirebaseUid(firebaseUser.uid)

            withContext(Dispatchers.Main) {
                if (usuarioEntity != null) {
                    Log.d("PerfilFragment", "Usuario encontrado: ${usuarioEntity.nombre}. Mostrando datos.")
                    // ¡Éxito! Mostramos los datos del usuario encontrado
                    mostrarDatosEnPantalla(view, usuarioEntity)
                } else {
                    // Esto puede pasar si el registro falló al guardar en Room
                    Log.e("PerfilFragment", "¡ERROR! Usuario existe en Firebase pero no en la base de datos local (Room).")
                    Toast.makeText(requireContext(), "Error crítico al cargar los datos del perfil.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun mostrarDatosEnPantalla(view: View, usuario: UsuarioEntity) {
        // --- Referencias a las vistas del XML ---
        val nameText = view.findViewById<TextView>(R.id.name_text)
        val professionText = view.findViewById<TextView>(R.id.profession_text)
        val profileImage = view.findViewById<ImageView>(R.id.profile_image)
        val emailTextDetail = view.findViewById<TextView>(R.id.email_text_detail)
        val ubicacionTextDetail = view.findViewById<TextView>(R.id.ubicacion_text_detail)
        val starRating = view.findViewById<TextView>(R.id.star_rating)
        val ratingValue = view.findViewById<TextView>(R.id.rating_value)
        val phoneTextDetail = view.findViewById<TextView>(R.id.phone_text_detail)

        // --- Llenamos las vistas con los datos REALES del UsuarioEntity ---
        nameText.text = "${usuario.nombre} ${usuario.apellido}"
        professionText.text = usuario.oficio ?: "Cliente" // Muestra 'Cliente' si no tiene oficio
        emailTextDetail.text = "Email: ${usuario.email}"
        ubicacionTextDetail.text = "Ubicación: ${usuario.distrito ?: "No especificado"}"
        phoneTextDetail.text = "DNI: ${usuario.dni ?: "No especificado"}"

        // --- CORRECCIÓN CLAVE: Usamos el rating y reviews REALES de la base de datos ---
        val ratingReal = usuario.rating ?: 0.0 // Si es nulo, usamos 0.0
        ratingValue.text = String.format("%.1f", ratingReal) // Formateamos a 1 decimal (ej. 4.0)
        starRating.text = generarEstrellas(ratingReal)

        // --- Cargar la foto desde Base64 (tu código ya es correcto) ---
        if (!usuario.fotoPerfilBase64.isNullOrBlank()) {
            try {
                val imageBytes = Base64.decode(usuario.fotoPerfilBase64, Base64.DEFAULT)
                val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                Glide.with(this@PerfilFragment)
                    .load(decodedImage)
                    .placeholder(R.drawable.ic_user)
                    .error(R.drawable.ic_user)
                    .circleCrop()
                    .into(profileImage)
            } catch (e: Exception) {
                Log.e("PerfilFragment", "Error al decodificar imagen Base64", e)
                profileImage.setImageResource(R.drawable.ic_user)
            }
        } else {
            profileImage.setImageResource(R.drawable.ic_user)
        }
    }

    private fun setupListeners(view: View) {
        val logoutButton = view.findViewById<MaterialButton>(R.id.logout_button)
        val optionInfo = view.findViewById<LinearLayout>(R.id.option_info)
        val infoContainer = view.findViewById<LinearLayout>(R.id.info_container)
        val chevronInfo = view.findViewById<ImageView>(R.id.chevron_info)
        // ... (las otras referencias de los acordeones)

        optionInfo.setOnClickListener { toggleAccordion(infoContainer, chevronInfo, view) }
        // ... (los otros listeners)

        logoutButton.setOnClickListener {
            auth.signOut()
            irAPantallaDeLogin()
        }
    }

    private fun irAPantallaDeLogin() {
        if (activity == null || !isAdded) return // Evitar crashes
        val intent = Intent(requireContext(), InicioSesionActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    // --- El resto de tu código (toggleAccordion, generarEstrellas) está bien ---
    private fun toggleAccordion(container: View, chevron: ImageView, rootView: View) {
        val isVisible = container.visibility == View.VISIBLE
        val rotationAngle = if (isVisible) 0f else 180f
        val color = if (isVisible) Color.parseColor("#666666") else Color.parseColor("#1976D2")
        chevron.animate().rotation(rotationAngle).setDuration(300).start()
        chevron.setColorFilter(color)
        if (isVisible) {
            val initialHeight = container.height
            val animator = ValueAnimator.ofInt(initialHeight, 0)
            animator.duration = 300
            animator.addUpdateListener {
                container.layoutParams.height = it.animatedValue as Int
                container.requestLayout()
            }
            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    container.visibility = View.GONE
                }
            })
            animator.start()
        } else {
            container.measure(
                View.MeasureSpec.makeMeasureSpec(rootView.width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.UNSPECIFIED
            )
            val targetHeight = container.measuredHeight
            container.layoutParams.height = 0
            container.visibility = View.VISIBLE
            val animator = ValueAnimator.ofInt(0, targetHeight)
            animator.duration = 300
            animator.addUpdateListener {
                container.layoutParams.height = it.animatedValue as Int
                container.requestLayout()
            }
            animator.start()
        }
    }

    private fun generarEstrellas(rating: Double): String {
        val filled = "★".repeat(rating.toInt())
        val half = if (rating - rating.toInt() >= 0.5) "★" else ""
        val totalStars = filled.length + half.length
        val empty = "☆".repeat(5 - totalStars)
        return filled + half + empty
    }
}
