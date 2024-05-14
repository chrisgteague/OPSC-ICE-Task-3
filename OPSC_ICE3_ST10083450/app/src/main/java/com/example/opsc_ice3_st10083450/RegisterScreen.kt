package com.example.opsc_ice3_st10083450

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class RegisterScreen : AppCompatActivity() {

    private lateinit var backToLoginBtn : Button
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_screen)

        backToLoginBtn = findViewById(R.id.btnBackToLogin)

        backToLoginBtn.setOnClickListener{
            var toLoginIntent = Intent(this, LoginScreen::class.java)
            startActivity(toLoginIntent)
        }

        emailEditText = findViewById(R.id.regEmail)
        passwordEditText = findViewById(R.id.regPassword)
        registerButton = findViewById(R.id.btncreateAccount)

        auth = FirebaseAuth.getInstance()
        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Registration success
                            Toast.makeText(
                                this,
                                "Registration successful",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(Intent(this, LoginScreen::class.java))
                            finish()
                        } else {
                            // Registration failed
                            if (task.exception is FirebaseAuthUserCollisionException) {
                                Toast.makeText(
                                    this,
                                    "Email is already registered",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this,
                                    "Registration failed. Please try again later.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
            }
        }




    }
}