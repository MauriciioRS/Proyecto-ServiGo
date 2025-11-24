package com.example.proyectofinal11

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // Carga el primer fragmento al iniciar
        if (savedInstanceState == null) {
            loadFragment(ExplorarFragment())
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_explorar -> ExplorarFragment()
                R.id.nav_favoritos -> FavoritosFragment()
                R.id.nav_trabajo -> TrabajosFragment()
                R.id.nav_mensaje -> MensajesFragment()
                R.id.nav_perfil -> PerfilFragment() // <-- ¡CAMBIO REALIZADO AQUÍ!
                else -> null
            }
            fragment?.let {
                loadFragment(it)
                true
            } ?: false
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main, fragment)
            .commit()
    }

    // Estos métodos no son necesarios para la funcionalidad básica de navegación
    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        return false
    }

    override fun onPrepareOptionsMenu(menu: android.view.Menu?): Boolean {
        return false
    }
}
