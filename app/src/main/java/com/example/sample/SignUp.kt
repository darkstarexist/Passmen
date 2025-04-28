package com.example.sample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

internal lateinit var auth: FirebaseAuth
internal lateinit var database: FirebaseDatabase

class SignUp : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //change to login page
        val img = findViewById<ImageView>(R.id.imageView)
        img.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }

        //intializing firebase
        auth = FirebaseAuth.getInstance()

        val signupBtn = findViewById<Button>(R.id.btnSignUp)

        signupBtn.setOnClickListener {
            //check all fields
            val username = findViewById<EditText>(R.id.userField).text.toString().trim()
            val email = findViewById<EditText>(R.id.emailField).text.toString().trim()
            val password = findViewById<EditText>(R.id.passwordField).text.toString()
            val repassword = findViewById<EditText>(R.id.repassField).text.toString()



            when{
                username.isEmpty() || email.isEmpty() || password.isEmpty() || repassword.isEmpty() -> {
                    Toast.makeText(this, "Some fields are missing, please fill them!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    Toast.makeText(this, "Invalid email format!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                password != repassword -> {
                    Toast.makeText(this, "Password does not match confirm password!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                else -> {
                    auth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful){
                                val user = FirebaseAuth.getInstance().currentUser
                                val uid = user?.uid

                                user?.sendEmailVerification()?.addOnCompleteListener {task ->
                                    if (task.isSuccessful){
                                        Toast.makeText(this, "Email verification link has been send to $email", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this,Verify::class.java)
                                        intent.putExtra("email",email)
                                        startActivity(intent)
                                        finish()
                                    }
                                }

                            }
                        }
                }
            }
        }



    }
}
