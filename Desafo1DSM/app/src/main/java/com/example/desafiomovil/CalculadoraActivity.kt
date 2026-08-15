package com.example.desafiomovil

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class CalculadoraActivity : AppCompatActivity() {

    private lateinit var tvResultado: TextView

    private var numeroActual = ""
    private var numeroAnterior = ""
    private var operacion = ""
    private var esNuevoNumero = true

    private val historial = mutableListOf<String>()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculadora)

        tvResultado = findViewById(R.id.tvResultado)

        // Números
        findViewById<Button>(R.id.btn0).setOnClickListener { agregarNumero("0") }
        findViewById<Button>(R.id.btn1).setOnClickListener { agregarNumero("1") }
        findViewById<Button>(R.id.btn2).setOnClickListener { agregarNumero("2") }
        findViewById<Button>(R.id.btn3).setOnClickListener { agregarNumero("3") }
        findViewById<Button>(R.id.btn4).setOnClickListener { agregarNumero("4") }
        findViewById<Button>(R.id.btn5).setOnClickListener { agregarNumero("5") }
        findViewById<Button>(R.id.btn6).setOnClickListener { agregarNumero("6") }
        findViewById<Button>(R.id.btn7).setOnClickListener { agregarNumero("7") }
        findViewById<Button>(R.id.btn8).setOnClickListener { agregarNumero("8") }
        findViewById<Button>(R.id.btn9).setOnClickListener { agregarNumero("9") }

        // Operaciones
        findViewById<Button>(R.id.btnSuma).setOnClickListener { seleccionarOperacion("+") }
        findViewById<Button>(R.id.btnResta).setOnClickListener { seleccionarOperacion("-") }
        findViewById<Button>(R.id.btnMultiplicacion).setOnClickListener { seleccionarOperacion("×") }
        findViewById<Button>(R.id.btnDivision).setOnClickListener { seleccionarOperacion("÷") }

        // Igual
        findViewById<Button>(R.id.btnIgual).setOnClickListener { calcularResultado() }

        // Limpiar
        findViewById<Button>(R.id.btnLimpiar).setOnClickListener { limpiar() }

        // Cambiar signo
        findViewById<Button>(R.id.btnCambiarSigno).setOnClickListener { cambiarSigno() }

        // Porcentaje
        findViewById<Button>(R.id.btnPorcentaje).setOnClickListener { porcentaje() }

        // Punto
        findViewById<Button>(R.id.btnPunto).setOnClickListener { agregarPunto() }

        // Volver
        findViewById<Button>(R.id.btnVolverCalculadora).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Guardar historial
        findViewById<Button>(R.id.btnGuardarHistorial).setOnClickListener {
            guardarHistorial()
        }
    }

    private fun agregarNumero(numero: String) {
        if (esNuevoNumero) {
            numeroActual = ""
            esNuevoNumero = false
        }
        if (numeroActual.length < 12) {
            numeroActual += numero
            // Mostrar el número actual con la operación si existe
            actualizarPantalla()
        }
    }

    private fun agregarPunto() {
        if (esNuevoNumero) {
            numeroActual = "0."
            esNuevoNumero = false
        } else if (!numeroActual.contains(".")) {
            numeroActual += "."
        }
        actualizarPantalla()
    }

    private fun seleccionarOperacion(op: String) {
        if (numeroActual.isNotEmpty()) {
            if (numeroAnterior.isNotEmpty() && !esNuevoNumero) {
                calcularResultado()
            }
            operacion = op
            numeroAnterior = numeroActual
            numeroActual = ""
            esNuevoNumero = true
            // Mostrar la operación seleccionada
            tvResultado.text = "$numeroAnterior $operacion"
        } else if (numeroAnterior.isNotEmpty()) {
            // Si ya hay un número anterior, solo cambia la operación
            operacion = op
            tvResultado.text = "$numeroAnterior $operacion"
        }
    }

    private fun calcularResultado() {
        if (numeroActual.isEmpty() || numeroAnterior.isEmpty()) {
            Toast.makeText(this, "Ingresa número y operación", Toast.LENGTH_SHORT).show()
            return
        }

        val num1 = numeroAnterior.toDoubleOrNull() ?: 0.0
        val num2 = numeroActual.toDoubleOrNull() ?: 0.0

        val resultado = when (operacion) {
            "+" -> num1 + num2
            "-" -> num1 - num2
            "×" -> num1 * num2
            "÷" -> {
                if (num2 == 0.0) {
                    Toast.makeText(this, "❌ No se puede dividir entre cero", Toast.LENGTH_LONG).show()
                    limpiar()
                    return
                }
                num1 / num2
            }
            else -> 0.0
        }

        val resultadoStr = if (resultado % 1 == 0.0) {
            resultado.toInt().toString()
        } else {
            String.format("%.4f", resultado).trimEnd('0').trimEnd('.')
        }

        // Guardar en historial con la operación completa
        val operacionStr = "$num1 $operacion $num2 = $resultadoStr"
        historial.add("${dateFormat.format(Date())}: $operacionStr")

        // Mostrar resultado completo
        tvResultado.text = "$num1 $operacion $num2 = $resultadoStr"
        numeroActual = resultadoStr
        numeroAnterior = ""
        operacion = ""
        esNuevoNumero = true
    }

    private fun limpiar() {
        numeroActual = ""
        numeroAnterior = ""
        operacion = ""
        esNuevoNumero = true
        tvResultado.text = "0"
    }

    private fun cambiarSigno() {
        if (numeroActual.isNotEmpty() && numeroActual != "0") {
            if (numeroActual.startsWith("-")) {
                numeroActual = numeroActual.substring(1)
            } else {
                numeroActual = "-$numeroActual"
            }
            actualizarPantalla()
        }
    }

    private fun porcentaje() {
        if (numeroActual.isNotEmpty()) {
            val num = numeroActual.toDoubleOrNull() ?: 0.0
            val resultadoPorcentaje = num / 100
            numeroActual = if (resultadoPorcentaje % 1 == 0.0) {
                resultadoPorcentaje.toInt().toString()
            } else {
                resultadoPorcentaje.toString()
            }
            actualizarPantalla()
        }
    }

    private fun actualizarPantalla() {
        if (operacion.isNotEmpty() && numeroAnterior.isNotEmpty()) {
            // Si hay una operación, mostrar número + operación
            if (numeroActual.isNotEmpty()) {
                tvResultado.text = "$numeroAnterior $operacion $numeroActual"
            } else {
                tvResultado.text = "$numeroAnterior $operacion"
            }
        } else {
            tvResultado.text = if (numeroActual.isEmpty()) "0" else numeroActual
        }
    }

    private fun guardarHistorial() {
        if (historial.isEmpty()) {
            Toast.makeText(this, "No hay operaciones para guardar", Toast.LENGTH_SHORT).show()
            return
        }

        val contenido = historial.joinToString("\n")
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val nombreArchivo = "historial_$timestamp.txt"

        try {
            val archivoInterno = File(filesDir, nombreArchivo)
            FileOutputStream(archivoInterno).use { outputStream ->
                outputStream.write(contenido.toByteArray())
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                guardarConMediaStore(contenido, nombreArchivo)
            } else {
                guardarEnDescargas(contenido, nombreArchivo)
            }

            Toast.makeText(this, "✅ Historial guardado", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun guardarEnDescargas(contenido: String, nombreArchivo: String) {
        val descargasDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val archivo = File(descargasDir, nombreArchivo)
        FileOutputStream(archivo).use { outputStream ->
            outputStream.write(contenido.toByteArray())
        }
    }

    private fun guardarConMediaStore(contenido: String, nombreArchivo: String) {
        val resolver = contentResolver
        val contentValues = android.content.ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, nombreArchivo)
            put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                outputStream.write(contenido.toByteArray())
            }
        }
    }
}