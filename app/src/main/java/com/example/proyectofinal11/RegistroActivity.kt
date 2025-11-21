package com.example.proyectofinal11

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
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

        continuarButton.setOnClickListener {
            val intent = Intent(this, Registro2Activity::class.java)
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
                val fechaFormateada = String.format("%02d / %02d / %02d", selectedDay, selectedMonth + 1, selectedYear % 100)
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




