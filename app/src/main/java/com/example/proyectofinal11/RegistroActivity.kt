package com.example.proyectofinal11

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
import com.example.proyectofinal11.utils.ValidationUtils
import com.example.proyectofinal11.utils.SharedPrefsHelper
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
            if (validateForm()) {
                // Guardar datos temporalmente
                val data = mapOf(
                    SharedPrefsHelper.KEY_REGISTRO_NOMBRES to nombresEditText.text.toString(),
                    SharedPrefsHelper.KEY_REGISTRO_APELLIDOS to apellidosEditText.text.toString(),
                    SharedPrefsHelper.KEY_REGISTRO_DNI to dniEditText.text.toString(),
                    SharedPrefsHelper.KEY_REGISTRO_FECHA to fechaNacimientoEditText.text.toString(),
                    SharedPrefsHelper.KEY_REGISTRO_DIRECCION to direccionEditText.text.toString(),
                    SharedPrefsHelper.KEY_REGISTRO_DISTRITO to distritoEditText.text.toString()
                )
                SharedPrefsHelper.saveRegistroData(this, data)
                
                val intent = Intent(this, Registro2Activity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun validateForm(): Boolean {
        val nombres = nombresEditText.text?.toString() ?: ""
        val apellidos = apellidosEditText.text?.toString() ?: ""
        val dni = dniEditText.text?.toString() ?: ""
        val fechaNacimiento = fechaNacimientoEditText.text?.toString() ?: ""
        val direccion = direccionEditText.text?.toString() ?: ""
        val distrito = distritoEditText.text?.toString() ?: ""
        
        var isValid = true
        var errorMessage = ""

        // Validar nombre
        if (!ValidationUtils.isValidName(nombres)) {
            nombresEditText.error = "Ingresa un nombre válido (mínimo 2 caracteres)"
            isValid = false
            if (errorMessage.isEmpty()) errorMessage = "Nombre inválido"
        } else {
            nombresEditText.error = null
        }

        // Validar apellido
        if (!ValidationUtils.isValidName(apellidos)) {
            apellidosEditText.error = "Ingresa un apellido válido (mínimo 2 caracteres)"
            isValid = false
            if (errorMessage.isEmpty()) errorMessage = "Apellido inválido"
        } else {
            apellidosEditText.error = null
        }

        // Validar DNI
        if (!ValidationUtils.isValidDNI(dni)) {
            dniEditText.error = "Ingresa un DNI válido (8 dígitos)"
            isValid = false
            if (errorMessage.isEmpty()) errorMessage = "DNI inválido"
        } else {
            dniEditText.error = null
        }

        // Validar fecha de nacimiento
        if (!ValidationUtils.isValidDate(fechaNacimiento)) {
            fechaNacimientoEditText.error = "Selecciona una fecha de nacimiento válida"
            isValid = false
            if (errorMessage.isEmpty()) errorMessage = "Fecha de nacimiento inválida"
        } else {
            fechaNacimientoEditText.error = null
        }

        // Validar dirección
        if (!ValidationUtils.isValidAddress(direccion)) {
            direccionEditText.error = "Ingresa una dirección válida (mínimo 5 caracteres)"
            isValid = false
            if (errorMessage.isEmpty()) errorMessage = "Dirección inválida"
        } else {
            direccionEditText.error = null
        }

        // Validar distrito
        if (!ValidationUtils.isValidDistrict(distrito)) {
            distritoEditText.error = "Ingresa un distrito válido (mínimo 2 caracteres)"
            isValid = false
            if (errorMessage.isEmpty()) errorMessage = "Distrito inválido"
        } else {
            distritoEditText.error = null
        }

        if (!isValid) {
            Toast.makeText(this, "Por favor completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
        }

        return isValid
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
                fechaNacimientoEditText.error = null // Limpiar error al seleccionar fecha
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




