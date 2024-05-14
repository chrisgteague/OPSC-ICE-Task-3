package com.example.opsc_ice3_st10083450

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class LoginScreen : AppCompatActivity() {

    private lateinit var regPageBtn : Button
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)

        regPageBtn = findViewById(R.id.btnRegPage)

        regPageBtn.setOnClickListener{
            var regIntent = Intent(this, RegisterScreen::class.java)
            startActivity(regIntent)
        }

        emailEditText = findViewById(R.id.logEmail)
        passwordEditText = findViewById(R.id.logPassword)
        loginButton = findViewById(R.id.btnLogin)

        auth = FirebaseAuth.getInstance()

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT)
                                .show()
                            startActivity(Intent(this, MainScreen::class.java))
                            finish()
                        } else {
                            // Login failed
                            Toast.makeText(
                                this,
                                "Login failed. Please try again.",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
            }
        }



    }
}