package com.example.proyectofinal11

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.proyectofinal11.data.UsuarioData
import com.example.proyectofinal11.models.Usuario
import com.google.android.material.button.MaterialButton
import android.widget.TextView
import android.widget.ImageView

class PerfiLAdapter : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_perfil, container, false)

        // Obtener usuario
        val usuario: Usuario = UsuarioData.obtenerUsuario()

        // Referencias
        val nameText = view.findViewById<TextView>(R.id.name_text)
        val professionText = view.findViewById<TextView>(R.id.profession_text)
        val emailText = view.findViewById<TextView>(R.id.email_text)
        val phoneText = view.findViewById<TextView>(R.id.phone_text)
        val ubicacionText = view.findViewById<TextView>(R.id.ubicacion_text)
        val experienciaText = view.findViewById<TextView>(R.id.experiencia_text)
        val starRating = view.findViewById<TextView>(R.id.star_rating)
        val ratingValue = view.findViewById<TextView>(R.id.rating_value)
        val reviewsText = view.findViewById<TextView>(R.id.reviews_text)
        val profileImage = view.findViewById<ImageView>(R.id.profile_image)
        val logoutButton = view.findViewById<MaterialButton>(R.id.logout_button)

        // Mostrar datos
        nameText.text = usuario.nombre
        professionText.text = usuario.oficio
        emailText.text = usuario.email
        phoneText.text = usuario.telefono
        ubicacionText.text = usuario.ubicacion
        experienciaText.text = usuario.experiencia

        ratingValue.text = usuario.rating.toString()
        starRating.text = generarEstrellas(usuario.rating)
        reviewsText.text = "${usuario.reviews} reseñas"

        // Imagen sin Glide → imagen por defecto
        profileImage.setImageResource(R.drawable.ic_user) // Cambia este recurso

        logoutButton.setOnClickListener {
            val intent = Intent(requireContext(), InicioSesionActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        return view
    }

    private fun generarEstrellas(rating: Double): String {
        val filled = "★".repeat(rating.toInt())
        val empty = "☆".repeat(5 - rating.toInt())
        return filled + empty
    }
}
