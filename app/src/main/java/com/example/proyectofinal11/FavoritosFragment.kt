// CÓDIGO FINAL Y COMPLETO PARA FavoritosFragment.kt
package com.example.proyectofinal11

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal11.adapters.ProfesionalAdapter
import com.example.proyectofinal11.adapters.ProfesionalClickListeners
import com.example.proyectofinal11.data.local.database.ServiGoDatabase
import com.example.proyectofinal11.data.local.entity.FavoritoEntity
import com.example.proyectofinal11.data.local.entity.UsuarioEntity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoritosFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var profesionalAdapter: ProfesionalAdapter
    private lateinit var db: ServiGoDatabase
    private lateinit var emptyFavoritosText: TextView
    private lateinit var auth: FirebaseAuth // <-- ¡AÑADIDO! Necesitamos saber quién es el usuario.

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favoritos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializamos los componentes
        db = ServiGoDatabase.getDatabase(requireContext())
        auth = FirebaseAuth.getInstance() // <-- ¡AÑADIDO!
        emptyFavoritosText = view.findViewById(R.id.empty_favoritos_text)

        setupRecyclerView(view)
        observeFavoritos() // Ahora esta función hará lo correcto
    }

    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.recycler_view_profesionales) // Asumo que el ID es el mismo
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Los listeners son los mismos, lo cual es perfecto.
        val listeners = ProfesionalClickListeners(
            onFavoritoClick = { usuario -> toggleFavorito(usuario) },
            onVerPerfilClick = { usuario -> verPerfil(usuario) }
        )

        profesionalAdapter = ProfesionalAdapter(listeners)
        recyclerView.adapter = profesionalAdapter
    }

    // ⭐⭐⭐ FUNCIÓN observeFavoritos() COMPLETAMENTE CORREGIDA ⭐⭐⭐
    private fun observeFavoritos() {
        // Obtenemos el UID del cliente actual. Si no hay nadie logueado, no podemos mostrar favoritos.
        val clienteUid = auth.currentUser?.uid
        if (clienteUid == null) {
            Log.w("FavoritosFragment", "No se puede observar favoritos, no hay usuario logueado.")
            emptyFavoritosText.isVisible = true // Mostramos el texto de vacío
            recyclerView.isVisible = false
            return
        }

        lifecycleScope.launch {
            // ¡CORRECCIÓN CLAVE! Ahora usamos el DAO y la consulta correctos.
            db.favoritoDao().obtenerFavoritos(clienteUid).collectLatest { listaDeProfesionalesFavoritos ->
                Log.d("FavoritosFragment", "Se recibieron ${listaDeProfesionalesFavoritos.size} favoritos.")

                // Actualizamos el adaptador con la lista de usuarios favoritos.
                profesionalAdapter.submitList(listaDeProfesionalesFavoritos)

                // Actualizamos la visibilidad de las vistas.
                emptyFavoritosText.isVisible = listaDeProfesionalesFavoritos.isEmpty()
                recyclerView.isVisible = listaDeProfesionalesFavoritos.isNotEmpty()
            }
        }
    }

    // ⭐⭐⭐ FUNCIÓN toggleFavorito() CORREGIDA PARA USAR LA LÓGICA REAL ⭐⭐⭐
    // Esta función ahora es IDÉNTICA a la de ExplorarFragment, como debe ser.
    private fun toggleFavorito(usuario: UsuarioEntity) {
        val contratistaUid = usuario.firebaseUid
        val clienteActualUid = auth.currentUser?.uid

        if (clienteActualUid == null) {
            Log.w("FavoritosFragment", "Intento de marcar favorito sin un usuario logueado.")
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val dao = db.favoritoDao()
            val esFavorito = dao.esFavorito(clienteActualUid, contratistaUid) > 0

            if (esFavorito) {
                // Si el usuario presiona el corazón en un favorito, se elimina de la lista.
                dao.eliminarFavorito(
                    clienteUid = clienteActualUid,
                    contratistaUid = contratistaUid
                )
            } else {
                // Este caso es raro aquí, pero por consistencia lo dejamos:
                // Si por alguna razón se pudiera añadir un no-favorito desde esta pantalla, se añadiría.
                dao.agregarFavorito(FavoritoEntity(clienteUid = clienteActualUid, contratistaUid = contratistaUid))
            }
        }
    }

    // La función verPerfil ya estaba bien. No necesita cambios.
    private fun verPerfil(usuario: UsuarioEntity) {
        parentFragmentManager.commit {
            replace(R.id.main, ProfileFragment.newInstance(usuario.firebaseUid))
            addToBackStack(null)
        }
    }
}
