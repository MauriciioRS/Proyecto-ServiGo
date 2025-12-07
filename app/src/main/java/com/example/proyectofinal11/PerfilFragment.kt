// CÓDIGO CORREGIDO Y COMPLETADO PARA PerfilFragment.kt
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
        setupListeners(view)
    }

    private fun cargarDatosUsuario(view: View) {
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            irAPantallaDeLogin()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val usuarioEntity = db.usuarioDao().obtenerUsuarioPorFirebaseUid(firebaseUser.uid)

            withContext(Dispatchers.Main) {
                if (usuarioEntity != null) {
                    mostrarDatosEnPantalla(view, usuarioEntity)
                } else {
                    // Si no se encuentra en Room, podría ser útil intentar sincronizar o mostrar error
                    // Por ahora, manejamos el caso vacío
                }
            }
        }
    }

    private fun mostrarDatosEnPantalla(view: View, usuario: UsuarioEntity) {
        val nameText = view.findViewById<TextView>(R.id.name_text)
        val professionText = view.findViewById<TextView>(R.id.profession_text)
        val profileImage = view.findViewById<ImageView>(R.id.profile_image)
        val emailTextDetail = view.findViewById<TextView>(R.id.email_text_detail)
        val ubicacionTextDetail = view.findViewById<TextView>(R.id.ubicacion_text_detail)
        val phoneTextDetail = view.findViewById<TextView>(R.id.phone_text_detail)
        val starRating = view.findViewById<TextView>(R.id.star_rating)
        val ratingValue = view.findViewById<TextView>(R.id.rating_value)

        nameText.text = "${usuario.nombre} ${usuario.apellido}"
        professionText.text = usuario.oficio ?: "Cliente"
        emailTextDetail.text = usuario.email
        ubicacionTextDetail.text = usuario.distrito ?: "No especificado"
        phoneTextDetail.text = usuario.dni ?: "No especificado"

        val ratingReal = usuario.rating ?: 0.0
        ratingValue.text = String.format("%.1f", ratingReal)
        starRating.text = generarEstrellas(ratingReal)

        if (!usuario.fotoPerfilBase64.isNullOrBlank()) {
            try {
                val imageBytes = Base64.decode(usuario.fotoPerfilBase64, Base64.DEFAULT)
                val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                Glide.with(this@PerfilFragment)
                    .load(decodedImage)
                    .circleCrop()
                    .into(profileImage)
            } catch (e: Exception) {
                profileImage.setImageResource(R.drawable.ic_user)
            }
        } else {
            profileImage.setImageResource(R.drawable.ic_user)
        }
    }

    private fun setupListeners(view: View) {
        // Listener para el botón de cierre de sesión
        val logoutButton = view.findViewById<MaterialButton>(R.id.logout_button)
        logoutButton.setOnClickListener {
            auth.signOut()
            irAPantallaDeLogin()
        }

        // Configuración de acordeones (Info, Pagos, Notificaciones)
        val optionInfo = view.findViewById<LinearLayout>(R.id.option_info)
        val infoContainer = view.findViewById<LinearLayout>(R.id.info_container)
        val chevronInfo = view.findViewById<ImageView>(R.id.chevron_info)

        val optionPayments = view.findViewById<LinearLayout>(R.id.option_payments)
        val paymentsContainer = view.findViewById<LinearLayout>(R.id.payments_container)
        val chevronPayments = view.findViewById<ImageView>(R.id.chevron_payments)

        val optionNotifications = view.findViewById<LinearLayout>(R.id.option_notifications)
        val notificationsContainer = view.findViewById<LinearLayout>(R.id.notifications_container)
        val chevronNotifications = view.findViewById<ImageView>(R.id.chevron_notifications)

        // Funcionalidad para expandir/colapsar
        optionInfo.setOnClickListener { toggleAccordion(infoContainer, chevronInfo, view) }
        optionPayments.setOnClickListener { toggleAccordion(paymentsContainer, chevronPayments, view) }
        optionNotifications.setOnClickListener { toggleAccordion(notificationsContainer, chevronNotifications, view) }
        
        // Botón Editar Perfil (Opcional: agregar navegación si existe EditarPerfilActivity o Fragment)
        val btnEditProfile = view.findViewById<MaterialButton>(R.id.btn_edit_profile)
        btnEditProfile.setOnClickListener {
            Toast.makeText(requireContext(), "Funcionalidad de editar perfil en desarrollo", Toast.LENGTH_SHORT).show()
        }
        
        // Botón Gestionar Publicaciones
        val btnManagePublications = view.findViewById<MaterialButton>(R.id.btn_manage_publications)
        btnManagePublications.setOnClickListener {
             Toast.makeText(requireContext(), "Funcionalidad de gestionar publicaciones en desarrollo", Toast.LENGTH_SHORT).show()
        }
        
         // Botón Ayuda
        val btnHelp = view.findViewById<MaterialButton>(R.id.btn_help)
        btnHelp.setOnClickListener {
             Toast.makeText(requireContext(), "Contactar a soporte@servigo.com", Toast.LENGTH_LONG).show()
        }
        
        // Botón Back (Flecha atrás en la toolbar)
        val backButton = view.findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            // Navegar hacia atrás en la pila de fragments o cerrar la actividad si es el único
            if (parentFragmentManager.backStackEntryCount > 0) {
                parentFragmentManager.popBackStack()
            } else {
               // Si no hay nada a donde volver, tal vez no hacer nada o ir a Home
            }
        }
    }

    private fun toggleAccordion(container: View, chevron: ImageView, rootView: View) {
        val isVisible = container.visibility == View.VISIBLE
        
        // Animar rotación de la flecha
        val rotationAngle = if (isVisible) 0f else 180f
        chevron.animate().rotation(rotationAngle).setDuration(300).start()
        
        // Cambiar color de la flecha (opcional, estilo visual)
        val color = if (isVisible) Color.parseColor("#666666") else Color.parseColor("#1976D2")
        chevron.setColorFilter(color)

        if (isVisible) {
            // Colapsar
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
            // Expandir
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

    private fun irAPantallaDeLogin() {
        if (activity == null || !isAdded) return
        val intent = Intent(requireContext(), InicioSesionActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}
