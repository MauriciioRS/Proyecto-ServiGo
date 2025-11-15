package com.example.proyectofinal11

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class PantallaInicialActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Aca se muestra la pantalla Incial Miyuki
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, InicioSesionActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000) // y aca la cantidad de segundos que queremos
    }
}

