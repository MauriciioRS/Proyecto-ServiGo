package com.example.proyectofinal11

import java.util.Locale
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import java.util.Calendar

class RegistroActivity : AppCompatActivity() {

    private lateinit var nombresEditText: EditText
    private lateinit var apellidosEditText: EditText
    private lateinit var dniEditText: EditText
    private lateinit var fechaNacimientoEditText: EditText
    private lateinit var direccionEditText: EditText
    private lateinit var distritoEditText: EditText
    private lateinit var continuarButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        nombresEditText = findViewById(R.id.nombres_edit_text)
        apellidosEditText = findViewById(R.id.apellidos_edit_text)
        dniEditText = findViewById(R.id.dni_edit_text)
        fechaNacimientoEditText = findViewById(R.id.fecha_nacimiento_edit_text)
        direccionEditText = findViewById(R.id.direccion_edit_text)
        distritoEditText = findViewById(R.id.distrito_edit_text)
        continuarButton = findViewById(R.id.continuar_button)

        fechaNacimientoEditText.setOnClickListener {
            showDatePicker()
        }

        // ===== CÓDIGO UNIDO Y MEJORADO =====
        continuarButton.setOnClickListener {
            // Recolectamos los datos del primer paso
            val nombre = nombresEditText.text.toString().trim()
            val apellido = apellidosEditText.text.toString().trim()
            val dni = dniEditText.text.toString().trim()
            val fechaNac = fechaNacimientoEditText.text.toString().trim()
            val direccion = direccionEditText.text.toString().trim()
            val distrito = distritoEditText.text.toString().trim()

            // Validación simple para asegurar que ningún campo esté vacío
            if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() || fechaNac.isEmpty() || direccion.isEmpty() || distrito.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Detiene la ejecución si algo falta
            }

            // Creamos el Intent y pasamos los datos como "extras"
            val intent = Intent(this, Registro2Activity::class.java).apply {
                putExtra("NOMBRE", nombre)
                putExtra("APELLIDO", apellido)
                putExtra("DNI", dni)
                putExtra("FECHA_NAC", fechaNac)
                putExtra("DIRECCION", direccion)
                putExtra("DISTRITO", distrito)
            }
            startActivity(intent)
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val fechaFormateada = String.format(Locale.ROOT,"%02d / %02d / %04d", selectedDay, selectedMonth + 1, selectedYear)
                fechaNacimientoEditText.setText(fechaFormateada)
            },
            year,
            month,
            day
        )

        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        calendar.add(Calendar.YEAR, -100)
        datePickerDialog.datePicker.minDate = calendar.timeInMillis

        datePickerDialog.show()
    }
}