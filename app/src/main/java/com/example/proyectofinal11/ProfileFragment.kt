package com.example.proyectofinal11

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.proyectofinal11.adapters.ProfilePagerAdapter
import com.example.proyectofinal11.data.local.database.ServiGoDatabase
import com.example.proyectofinal11.data.local.entity.FavoritoEntity
import com.example.proyectofinal11.data.local.entity.UsuarioEntity
import com.example.proyectofinal11.vi.chat.ChatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ⭐ CORRECCIÓN: Implementa la interfaz para poder recibir notificaciones del fragmento hijo.
class ProfileFragment : Fragment(), TabResenasFragment.OnResenaSubidaListener {

    private lateinit var db: ServiGoDatabase
    private lateinit var auth: FirebaseAuth
    private var usuarioUid: String? = null
    private var usuarioDelPerfil: UsuarioEntity? = null
    private var esFavoritoActual: Boolean = false

    companion object {
        private const val ARG_USUARIO_UID = "USUARIO_UID"

        fun newInstance(usuarioUid: String): ProfileFragment {
            val fragment = ProfileFragment()
            val args = Bundle()
            args.putString(ARG_USUARIO_UID, usuarioUid)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = ServiGoDatabase.getDatabase(requireContext())
        auth = FirebaseAuth.getInstance()
        arguments?.let {
            usuarioUid = it.getString(ARG_USUARIO_UID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (usuarioUid != null) {
            cargarDatosDelUsuario(view)
        } else {
            Toast.makeText(requireContext(), "Error: No se pudo encontrar al usuario.", Toast.LENGTH_SHORT).show()
        }
        setupTabs(view)
    }

    private fun cargarDatosDelUsuario(view: View) {
        val clienteActualUid = auth.currentUser?.uid
        if (clienteActualUid == null) {
            Toast.makeText(requireContext(), "Error: Debes iniciar sesión.", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val usuario = db.usuarioDao().obtenerUsuarioPorFirebaseUid(usuarioUid!!)
            // Si el usuario a ver es el mismo que el logueado, no puede ser favorito de sí mismo.
            esFavoritoActual = if (clienteActualUid == usuarioUid) {
                false
            } else {
                db.favoritoDao().esFavorito(clienteActualUid, usuarioUid!!) > 0
            }


            withContext(Dispatchers.Main) {
                if (usuario != null) {
                    usuarioDelPerfil = usuario
                    mostrarDatosEnUI(view, usuario)
                    setupButtons(view)
                } else {
                    Toast.makeText(requireContext(), "Usuario no encontrado.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupButtons(view: View) {
        val contactarButton = view.findViewById<MaterialButton>(R.id.btnContactar)
        val guardarButton = view.findViewById<MaterialButton>(R.id.btnGuardar)

        contactarButton.setOnClickListener {
            usuarioDelPerfil?.let { user ->
                val intent = Intent(requireContext(), ChatActivity::class.java).apply {
                    putExtra("RECIPIENT_UID", user.firebaseUid)
                    putExtra("CHAT_TITLE", "${user.nombre} ${user.apellido}")
                }
                startActivity(intent)
            }
        }

        // Ocultar botón de guardar si el usuario está viendo su propio perfil
        if (auth.currentUser?.uid == usuarioUid) {
            guardarButton.visibility = View.GONE
        } else {
            guardarButton.visibility = View.VISIBLE
            actualizarBotonFavorito(guardarButton)
            guardarButton.setOnClickListener {
                usuarioDelPerfil?.let {
                    toggleFavorito(it, guardarButton)
                }
            }
        }
    }

    private fun toggleFavorito(usuario: UsuarioEntity, boton: MaterialButton) {
        val contratistaUid = usuario.firebaseUid
        val clienteActualUid = auth.currentUser?.uid

        if (clienteActualUid == null) {
            Log.w("ProfileFragment", "Intento de marcar favorito sin un usuario logueado.")
            Toast.makeText(requireContext(), "Necesitas iniciar sesión para guardar favoritos", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val dao = db.favoritoDao()

            if (esFavoritoActual) {
                dao.eliminarFavorito(
                    clienteUid = clienteActualUid,
                    contratistaUid = contratistaUid
                )
            } else {
                dao.agregarFavorito(FavoritoEntity(clienteUid = clienteActualUid, contratistaUid = contratistaUid))
            }
            esFavoritoActual = !esFavoritoActual

            withContext(Dispatchers.Main) {
                actualizarBotonFavorito(boton)
                val mensaje = if (esFavoritoActual) "Guardado en favoritos" else "Eliminado de favoritos"
                Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun actualizarBotonFavorito(boton: MaterialButton) {
        if (esFavoritoActual) {
            boton.text = "Guardado"
            boton.setIconResource(R.drawable.ic_heart_filled)
        } else {
            boton.text = "Guardar"
            boton.setIconResource(R.drawable.ic_heart_outline)
        }
    }

    private fun mostrarDatosEnUI(view: View, usuario: UsuarioEntity) {
        val profileImage = view.findViewById<ImageView>(R.id.profileImage)
        val userName = view.findViewById<TextView>(R.id.userName)
        val userProfession = view.findViewById<TextView>(R.id.userProfession)
        val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)
        val ratingText = view.findViewById<TextView>(R.id.ratingText)

        userName.text = "${usuario.nombre} ${usuario.apellido}"
        userProfession.text = usuario.oficio ?: "Cliente"
        ratingBar.rating = (usuario.rating ?: 0.0).toFloat()
        ratingText.text = String.format("%.1f", usuario.rating ?: 0.0)

        // Lógica de carga de imagen (sin cambios)
        if (usuario.fotoPerfilBase64 != null) {
            try {
                val imageBytes = Base64.decode(usuario.fotoPerfilBase64, Base64.DEFAULT)
                val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                Glide.with(this).load(decodedImage).circleCrop()
                    .placeholder(R.drawable.ic_profile_placeholder).error(R.drawable.ic_user)
                    .into(profileImage)
            } catch (e: Exception) {
                profileImage.setImageResource(R.drawable.ic_user)
            }
        } else {
            profileImage.setImageResource(R.drawable.ic_user)
        }
    }

    private fun setupTabs(view: View) {
        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = view.findViewById<ViewPager2>(R.id.tabContent)

        val pagerAdapter = ProfilePagerAdapter(this)
        pagerAdapter.profesionalUid = this.usuarioUid
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Información"
                1 -> "Portafolio"
                2 -> "Reseña"
                else -> ""
            }
        }.attach()
    }

    override fun onResenaSubida() {
        Log.d("ProfileFragment", "Notificación recibida: nueva reseña subida. Recargando perfil...")
        view?.let {
            cargarDatosDelUsuario(it)
        }
    }
}