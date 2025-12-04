package com.example.proyectofinal11

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.proyectofinal11.database.Repository
import com.example.proyectofinal11.utils.SharedPrefsHelper
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PerfilFragment : Fragment() {

    private lateinit var logoutButton: MaterialButton
    private lateinit var nameText: TextView
    private lateinit var professionText: TextView
    private lateinit var ratingLayout: LinearLayout
    private lateinit var ratingValue: TextView
    private lateinit var starRating: TextView
    private lateinit var reviewsText: TextView
    private lateinit var emailText: TextView
    private lateinit var phoneText: TextView
    private lateinit var locationText: TextView
    private lateinit var experienceText: TextView
    private lateinit var profileImage: ImageView
    
    private lateinit var repository: Repository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_perfil, container, false)
        
        // Inicializar repositorio
        repository = Repository(requireContext())
        
        // Inicializar vistas
        logoutButton = view.findViewById(R.id.logout_button)
        nameText = view.findViewById(R.id.name_text)
        professionText = view.findViewById(R.id.profession_text)
        ratingLayout = view.findViewById(R.id.rating_layout)
        ratingValue = view.findViewById(R.id.rating_value)
        starRating = view.findViewById(R.id.star_rating)
        reviewsText = view.findViewById(R.id.reviews_text)
        emailText = view.findViewById(R.id.email_text)
        phoneText = view.findViewById(R.id.phone_text)
        locationText = view.findViewById(R.id.location_text)
        experienceText = view.findViewById(R.id.experience_text)
        profileImage = view.findViewById(R.id.profile_image)
        
        logoutButton.setOnClickListener {
            cerrarSesion()
        }
        
        return view
    }
    
    override fun onResume() {
        super.onResume()
        // Usar lifecycleScope para evitar leaks si el fragmento se destruye
        cargarDatosUsuario()
    }
    
    private fun cargarDatosUsuario() {
        // Obtener contexto de forma segura
        val context = context ?: return
        val userId = SharedPrefsHelper.getCurrentUserId(context)
        
        if (userId != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val usuario = repository.getUsuarioById(userId)
                    
                    withContext(Dispatchers.Main) {
                        // Verificar que el fragmento siga añadido
                        if (!isAdded) return@withContext
                        
                        if (usuario != null) {
                            // Actualizar UI con datos del usuario
                            nameText.text = "${usuario.nombres} ${usuario.apellidos}"
                            emailText.text = usuario.email
                            
                            // Usar distrito como ubicación
                            locationText.text = usuario.distrito
                            
                            // Teléfono no existe en entidad, usar DNI o placeholder
                            phoneText.text = "DNI: ${usuario.dni}" 
                            
                            // Lógica específica según tipo de cuenta
                            if (usuario.tipoCuenta == "Cliente") {
                                professionText.text = "Cliente"
                                ratingLayout.visibility = View.GONE
                                reviewsText.visibility = View.GONE
                                experienceText.text = "N/A"
                            } else {
                                // Contratista o Ambos
                                professionText.text = usuario.oficio ?: "Profesional"
                                ratingLayout.visibility = View.VISIBLE
                                reviewsText.visibility = View.VISIBLE
                                
                                // Simulamos rating y experiencia ya que no están en UsuarioEntity aún
                                starRating.text = "★★★★★"
                                ratingValue.text = "5.0"
                                reviewsText.text = "0 Reseñas"
                                experienceText.text = "Nueva cuenta"
                            }
                        } else {
                            // Usuario no encontrado: no forzar cierre sesión inmediatamente para no interrumpir UX
                            // Podríamos mostrar un estado de error o reintentar
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            // No hay usuario logueado, pero estamos en PerfilFragment.
            // Esto puede pasar si el proceso se mató y se restauró el fragmento pero se perdió la sesión en memoria?
            // SharedPrefs debería persistir. Si retorna null es que no hay sesión válida.
            cerrarSesion()
        }
    }
    
    private fun cerrarSesion() {
        val context = context ?: return
        SharedPrefsHelper.clearCurrentUser(context)
        val intent = Intent(context, InicioSesionActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish()
    }
}
