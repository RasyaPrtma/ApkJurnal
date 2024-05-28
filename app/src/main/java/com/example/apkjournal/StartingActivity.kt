package com.example.apkjournal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class StartingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_starting)

        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener{
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }

    }
}