package com.example.waterwisee

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class WelcomePage : AppCompatActivity() {
//when initializing the function the on create method will be used
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_welcome_page) // the page ref ID

        // Adjusting the layout for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize the welcome button
        val button_welcome = findViewById<Button>(R.id.button_welcome)

        // Navigate to Login2 activity when the welcome button is clicked
        button_welcome.setOnClickListener {
            val intent = Intent(this, Login2::class.java)
            startActivity(intent)
        }
    }
}

