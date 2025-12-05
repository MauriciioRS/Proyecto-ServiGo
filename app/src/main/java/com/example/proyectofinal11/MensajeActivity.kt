package com.example.proyectofinal11

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MensajeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activitymensaje)

        val loginButton = findViewById<MaterialButton>(R.id.login_button)

        loginButton.setOnClickListener {
            startActivity(Intent(this, InicioSesionActivity::class.java))
            finish()
        }
    }
}
