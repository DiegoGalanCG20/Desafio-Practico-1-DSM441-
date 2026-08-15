package com.example.desafiomovil

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class SalarioActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etSalarioBase: EditText
    private lateinit var tvSalarioBruto: TextView
    private lateinit var tvRenta: TextView
    private lateinit var tvAFP: TextView
    private lateinit var tvISSS: TextView  // ← CORREGIDO: antes era tvIsss
    private lateinit var tvTotalDescuentos: TextView
    private lateinit var tvSalarioNeto: TextView
    private lateinit var btnCalcular: Button
    private lateinit var btnVolver: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_salario)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        etNombre = findViewById(R.id.etNombreEmpleado)
        etSalarioBase = findViewById(R.id.etSalarioBase)
        tvSalarioBruto = findViewById(R.id.tvSalarioBruto)
        tvRenta = findViewById(R.id.tvRenta)
        tvAFP = findViewById(R.id.tvAFP)
        tvISSS = findViewById(R.id.tvISSS)  // ← CORREGIDO
        tvTotalDescuentos = findViewById(R.id.tvTotalDescuentos)
        tvSalarioNeto = findViewById(R.id.tvSalarioNeto)
        btnCalcular = findViewById(R.id.btnCalcularSalario)
        btnVolver = findViewById(R.id.btnVolverSalario)
    }

    private fun setupListeners() {
        btnCalcular.setOnClickListener {
            calcularSalario()
        }

        btnVolver.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun calcularSalario() {
        val nombre = etNombre.text.toString().trim()
        val salarioStr = etSalarioBase.text.toString()

        if (nombre.isEmpty() || salarioStr.isEmpty()) {
            vibrarDispositivo()
            etSalarioBase.error = getString(R.string.error_salario)
            return
        }

        try {
            val salarioBase = salarioStr.toDouble()

            if (salarioBase <= 0) {
                vibrarDispositivo()
                etSalarioBase.error = getString(R.string.error_salario)
                return
            }

            val renta = calcularRenta(salarioBase)
            val afp = salarioBase * 0.0725
            val isss = salarioBase * 0.03  // ← CORREGIDO: antes era issss
            val totalDescuentos = renta + afp + isss
            val salarioNeto = salarioBase - totalDescuentos

            // Mostrar resultados con colores
            tvSalarioBruto.text = String.format("%s: $%.2f", getString(R.string.salario_bruto), salarioBase)
            tvSalarioBruto.setTextColor(ContextCompat.getColor(this, R.color.salario_bruto_color))

            tvRenta.text = String.format("%s: $%.2f", getString(R.string.descuento_renta), renta)
            tvAFP.text = String.format("%s: $%.2f", getString(R.string.descuento_afp), afp)
            tvISSS.text = String.format("%s: $%.2f", getString(R.string.descuento_isss), isss)  // ← CORREGIDO
            tvTotalDescuentos.text = String.format("%s: $%.2f", getString(R.string.total_descuentos), totalDescuentos)

            tvSalarioNeto.text = String.format("%s: $%.2f", getString(R.string.salario_neto), salarioNeto)
            tvSalarioNeto.setTextColor(ContextCompat.getColor(this, R.color.salario_neto_color))

        } catch (e: NumberFormatException) {
            vibrarDispositivo()
            etSalarioBase.error = getString(R.string.error_salario)
        }
    }

    private fun calcularRenta(salario: Double): Double {
        return when {
            salario <= 472.00 -> 0.0
            salario <= 895.24 -> (salario - 472.00) * 0.10 + 17.67
            salario <= 2038.10 -> (salario - 895.24) * 0.20 + 60.00
            else -> (salario - 2038.10) * 0.30 + 288.57
        }
    }

    private fun vibrarDispositivo() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(200)
        }
    }
}