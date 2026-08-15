package com.example.desafiomovil

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import java.text.DecimalFormat

class PromedioActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etNota1: EditText
    private lateinit var etNota2: EditText
    private lateinit var etNota3: EditText
    private lateinit var etNota4: EditText
    private lateinit var etNota5: EditText
    private lateinit var tvResultado: TextView
    private lateinit var btnCalcular: Button
    private lateinit var btnVolver: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promedio)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        etNombre = findViewById(R.id.etNombreEstudiante)
        etNota1 = findViewById(R.id.etNota1)
        etNota2 = findViewById(R.id.etNota2)
        etNota3 = findViewById(R.id.etNota3)
        etNota4 = findViewById(R.id.etNota4)
        etNota5 = findViewById(R.id.etNota5)
        tvResultado = findViewById(R.id.tvResultado)
        btnCalcular = findViewById(R.id.btnCalcularPromedio)
        btnVolver = findViewById(R.id.btnVolverPromedio)
    }

    private fun setupListeners() {
        btnCalcular.setOnClickListener {
            calcularPromedio()
        }

        btnVolver.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun calcularPromedio() {
        val nombre = etNombre.text.toString().trim()
        val nota1Str = etNota1.text.toString()
        val nota2Str = etNota2.text.toString()
        val nota3Str = etNota3.text.toString()
        val nota4Str = etNota4.text.toString()
        val nota5Str = etNota5.text.toString()

        // Validar campos vacíos
        if (nombre.isEmpty() || nota1Str.isEmpty() || nota2Str.isEmpty() ||
            nota3Str.isEmpty() || nota4Str.isEmpty() || nota5Str.isEmpty()) {
            showError(getString(R.string.error_campos))
            return
        }

        try {
            val nota1 = nota1Str.toDouble()
            val nota2 = nota2Str.toDouble()
            val nota3 = nota3Str.toDouble()
            val nota4 = nota4Str.toDouble()
            val nota5 = nota5Str.toDouble()

            // Validar notas entre 0 y 10
            if (!validarNota(nota1, etNota1) || !validarNota(nota2, etNota2) ||
                !validarNota(nota3, etNota3) || !validarNota(nota4, etNota4) ||
                !validarNota(nota5, etNota5)) {
                return
            }

            // Ponderaciones: 10%, 15%, 20%, 25%, 30%
            val ponderaciones = doubleArrayOf(0.10, 0.15, 0.20, 0.25, 0.30)
            val notas = doubleArrayOf(nota1, nota2, nota3, nota4, nota5)

            val promedio = calcularPromedioPonderado(notas, ponderaciones)
            val estado = if (promedio >= 6.0) getString(R.string.aprobado) else getString(R.string.reprobado)

            val df = DecimalFormat("#.##")
            val resultado = String.format(getString(R.string.resultado_promedio), promedio, estado)
            tvResultado.text = resultado

            // Enviar notificación
            enviarNotificacion(promedio, estado)

        } catch (e: NumberFormatException) {
            showError(getString(R.string.error_campos))
        }
    }

    private fun validarNota(nota: Double, editText: EditText): Boolean {
        if (nota < 0 || nota > 10) {
            editText.error = getString(R.string.error_nota)
            return false
        }
        return true
    }

    private fun calcularPromedioPonderado(notas: DoubleArray, ponderaciones: DoubleArray): Double {
        var suma = 0.0
        for (i in notas.indices) {
            suma += notas[i] * ponderaciones[i]
        }
        return suma
    }

    private fun showError(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    private fun enviarNotificacion(promedio: Double, estado: String) {
        val channelId = "promedio_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notificaciones de Promedio",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val df = DecimalFormat("#.##")
        val mensaje = String.format(getString(R.string.resultado_promedio), promedio, estado)

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(getString(R.string.promedio_final))
            .setContentText(mensaje)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(1, notification)
    }
}