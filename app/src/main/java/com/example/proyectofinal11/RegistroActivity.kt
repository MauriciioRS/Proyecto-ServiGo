package com.example.proyectofinal11

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.Calendar

/**
 * Pantalla de Registro de ServiGo
 * Permite a los usuarios crear una cuenta nueva
 */
class RegistroActivity : AppCompatActivity() {

    private lateinit var nombresEditText: TextInputEditText
    private lateinit var apellidosEditText: TextInputEditText
    private lateinit var dniEditText: TextInputEditText
    private lateinit var edadEditText: TextInputEditText
    private lateinit var fechaNacimientoEditText: TextInputEditText
    private lateinit var provinciaLayout: TextInputLayout
    private lateinit var provinciaAutoComplete: AutoCompleteTextView
    private lateinit var ciudadLayout: TextInputLayout
    private lateinit var ciudadAutoComplete: AutoCompleteTextView
    private lateinit var distritoLayout: TextInputLayout
    private lateinit var distritoAutoComplete: AutoCompleteTextView
    private lateinit var direccionEditText: TextInputEditText
    private lateinit var oficioLayout: TextInputLayout
    private lateinit var oficioAutoComplete: AutoCompleteTextView
    private lateinit var registrarseButton: MaterialButton

    private val provincias = listOf(
        "Lima", "Arequipa", "Cusco", "Trujillo", "Chiclayo", 
        "Piura", "Iquitos", "Huancayo", "Cajamarca", "Tacna"
    )

    private val oficios = listOf(
        "Jardinería", "Peluquería", "Mecánica", "Carpintería", 
        "Gasfitería", "Electricidad", "Pintura", "Albañilería",
        "Limpieza", "Cocina", "Plomería", "Otro"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar vistas
        nombresEditText = findViewById(R.id.nombres_edit_text)
        apellidosEditText = findViewById(R.id.apellidos_edit_text)
        dniEditText = findViewById(R.id.dni_edit_text)
        edadEditText = findViewById(R.id.edad_edit_text)
        fechaNacimientoEditText = findViewById(R.id.fecha_nacimiento_edit_text)
        provinciaLayout = findViewById(R.id.provincia_input_layout)
        provinciaAutoComplete = findViewById(R.id.provincia_auto_complete)
        ciudadLayout = findViewById(R.id.ciudad_input_layout)
        ciudadAutoComplete = findViewById(R.id.ciudad_auto_complete)
        distritoLayout = findViewById(R.id.distrito_input_layout)
        distritoAutoComplete = findViewById(R.id.distrito_auto_complete)
        direccionEditText = findViewById(R.id.direccion_edit_text)
        oficioLayout = findViewById(R.id.oficio_input_layout)
        oficioAutoComplete = findViewById(R.id.oficio_auto_complete)
        registrarseButton = findViewById(R.id.registrarse_button)


        setupDropdowns()


        fechaNacimientoEditText.setOnClickListener {
            showDatePicker()
        }

        // Botón de registrars
        registrarseButton.setOnClickListener {

            finish()
        }
    }

    private fun setupDropdowns() {
        // Provincia
        val provinciaAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, provincias)
        provinciaAutoComplete.setAdapter(provinciaAdapter)

        // Ciudad
        val ciudadAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, provincias)
        ciudadAutoComplete.setAdapter(ciudadAdapter)

        // Distrito
        val distritoAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, provincias)
        distritoAutoComplete.setAdapter(distritoAdapter)

        // Oficio
        val oficioAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, oficios)
        oficioAutoComplete.setAdapter(oficioAdapter)
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val fechaFormateada = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                fechaNacimientoEditText.setText(fechaFormateada)
                
                // Calcular edad automáticamente
                val edad = Calendar.getInstance().get(Calendar.YEAR) - selectedYear
                edadEditText.setText(edad.toString())
            },
            year,
            month,
            day
        )
        
        // Establecer fecha
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        calendar.add(Calendar.YEAR, -100)
        datePickerDialog.datePicker.minDate = calendar.timeInMillis
        
        datePickerDialog.show()
    }
}


