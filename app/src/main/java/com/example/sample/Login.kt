package com.example.sample

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Auto-login check
        if(auth.currentUser != null){
            startActivity(Intent(this, GeneratePassword::class.java))
            finish()
        }


        // signup activity
        val SignupBtn = findViewById<ImageView>(R.id.imageView)
        SignupBtn.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
            finish()
        }


        // Login button click
        val loginBtn = findViewById<Button>(R.id.btnSignIn)
        loginBtn.setOnClickListener {
            val email = findViewById<EditText>(R.id.userEdit).text.toString().trim()
            val password = findViewById<EditText>(R.id.passEdit).text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Email validation
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginUser(email, password)
        }
    }



    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, GeneratePassword::class.java))
                    finish()
                }
                else{
                    Toast.makeText(this, "Username or Password is Invalid!", Toast.LENGTH_SHORT).show()
                    return@addOnCompleteListener
                }
            }
    }
}
