package com.example.proyectofinal11

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.proyectofinal11.data.UsuarioData
import com.example.proyectofinal11.models.Usuario
import com.google.android.material.button.MaterialButton

class PerfilFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ==== OBTENER USUARIO ====
        val usuario: Usuario = UsuarioData.obtenerUsuario()

        // ===== REFERENCIAS DE LA CABECERA =====
        val nameText = view.findViewById<TextView>(R.id.name_text)
        val professionText = view.findViewById<TextView>(R.id.profession_text)
        val starRating = view.findViewById<TextView>(R.id.star_rating)
        val ratingValue = view.findViewById<TextView>(R.id.rating_value)
        val profileImage = view.findViewById<ImageView>(R.id.profile_image)
        val logoutButton = view.findViewById<MaterialButton>(R.id.logout_button)

        // ===== REFERENCIAS DEL ACORDEÓN (CON NUEVOS IDs) =====
        val emailTextDetail = view.findViewById<TextView>(R.id.email_text_detail)
        val phoneTextDetail = view.findViewById<TextView>(R.id.phone_text_detail)
        val ubicacionTextDetail = view.findViewById<TextView>(R.id.ubicacion_text_detail)
        val experienciaTextDetail = view.findViewById<TextView>(R.id.experiencia_text_detail)
        val reviewsTextDetail = view.findViewById<TextView>(R.id.reviews_text_detail)


        // ==== MOSTRAR DATOS EN LA CABECERA ====
        nameText.text = usuario.nombre
        professionText.text = usuario.oficio
        ratingValue.text = usuario.rating.toString()
        starRating.text = generarEstrellas(usuario.rating)
        profileImage.setImageResource(R.drawable.ic_user) // Imagen por defecto

        // ==== MOSTRAR DATOS EN EL ACORDEÓN ====
        emailTextDetail.text = "Email: ${usuario.email}"
        phoneTextDetail.text = "Teléfono: ${usuario.telefono}"
        ubicacionTextDetail.text = "Ubicación: ${usuario.ubicacion}"
        experienciaTextDetail.text = "Experiencia: ${usuario.experiencia}"
        reviewsTextDetail.text = "Reseñas totales: ${usuario.reviews}"


        // ========== LÓGICA DEL ACORDEÓN ==========

        // --- Referencias de vistas del acordeón ---
        val optionInfo = view.findViewById<LinearLayout>(R.id.option_info)
        val infoContainer = view.findViewById<LinearLayout>(R.id.info_container)
        val chevronInfo = view.findViewById<ImageView>(R.id.chevron_info)

        val optionPayments = view.findViewById<LinearLayout>(R.id.option_payments)
        val paymentsContainer = view.findViewById<LinearLayout>(R.id.payments_container)
        val chevronPayments = view.findViewById<ImageView>(R.id.chevron_payments)

        val optionNotifications = view.findViewById<LinearLayout>(R.id.option_notifications)
        val notificationsContainer = view.findViewById<LinearLayout>(R.id.notifications_container)
        val chevronNotifications = view.findViewById<ImageView>(R.id.chevron_notifications)

        // ===== LISTENERS =====
        optionInfo.setOnClickListener { toggleAccordion(infoContainer, chevronInfo, view) }
        optionPayments.setOnClickListener { toggleAccordion(paymentsContainer, chevronPayments, view) }
        optionNotifications.setOnClickListener { toggleAccordion(notificationsContainer, chevronNotifications, view) }

        // ========== LOGOUT ==========
        logoutButton.setOnClickListener {
            val intent = Intent(requireContext(), InicioSesionActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }

    // --- Función de animación genérica y reutilizable ---
    private fun toggleAccordion(container: View, chevron: ImageView, rootView: View) {
        val isVisible = container.visibility == View.VISIBLE

        // Girar la flecha y cambiar color
        val rotationAngle = if (isVisible) 0f else 180f
        val color = if (isVisible) Color.parseColor("#666666") else Color.parseColor("#1976D2")
        chevron.animate().rotation(rotationAngle).setDuration(300).start()
        chevron.setColorFilter(color)

        // Animar la altura
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
            container.measure(View.MeasureSpec.makeMeasureSpec(rootView.width, View.MeasureSpec.EXACTLY), View.MeasureSpec.UNSPECIFIED)
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
        val empty = "☆".repeat(5 - rating.toInt())
        return filled + empty
    }
}
    