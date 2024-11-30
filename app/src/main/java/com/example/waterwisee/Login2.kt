package com.example.waterwisee

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Login2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize views
        val saveButton = findViewById<Button>(R.id.buttonSave)
        val loginButton = findViewById<Button>(R.id.buttonLogin)
        val usernameEditText = findViewById<EditText>(R.id.editTextUsername)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)

        val sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)

        // Save credentials using SharedPreferences
        saveButton.setOnClickListener {
            val editor = sharedPref.edit()
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            editor.apply {
                putString("user_name", username)
                putString("password", password)
                apply()
            }
            Toast.makeText(this, "Credentials Saved!", Toast.LENGTH_SHORT).show()
        }

        // Handle login button click and navigate to MainActivity
        loginButton.setOnClickListener {
            val inputUsername = usernameEditText.text.toString()
            val inputPassword = passwordEditText.text.toString()

            val savedUsername = sharedPref.getString("user_name", null)
            val savedPassword = sharedPref.getString("password", null)

            if (inputUsername == savedUsername && inputPassword == savedPassword) {
                // Credentials are correct, navigate to MainActivity and clear back stack
                val intent = Intent(this, Dashboard_Activity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                // Show error if credentials don't match
                Toast.makeText(this, "Invalid Username or Password!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
