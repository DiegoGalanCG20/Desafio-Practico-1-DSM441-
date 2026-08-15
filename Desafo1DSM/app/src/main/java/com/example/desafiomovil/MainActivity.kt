package com.example.desafiomovil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnEjercicio1 = findViewById<LinearLayout>(R.id.btnEjercicio1)
        val btnEjercicio2 = findViewById<LinearLayout>(R.id.btnEjercicio2)
        val btnEjercicio3 = findViewById<LinearLayout>(R.id.btnEjercicio3)

        btnEjercicio1.setOnClickListener {
            startActivity(Intent(this, PromedioActivity::class.java))
        }

        btnEjercicio2.setOnClickListener {
            startActivity(Intent(this, SalarioActivity::class.java))
        }

        btnEjercicio3.setOnClickListener {
            startActivity(Intent(this, CalculadoraActivity::class.java))
        }
    }
}