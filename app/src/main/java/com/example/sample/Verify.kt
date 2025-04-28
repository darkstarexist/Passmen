package com.example.sample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Verify : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify)

        //initalize instances
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val email = intent.getStringExtra("email")

        val btnContinue = findViewById<Button>(R.id.btnContinue)

        btnContinue.setOnClickListener {
            val user = auth.currentUser
            val uid = user?.uid

            user?.reload()?.addOnCompleteListener {
                if(user.isEmailVerified){
                    Toast.makeText(this, "Email Verified!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, GeneratePassword::class.java))
                    finish()

                }
                else{
                    Toast.makeText(this, "Please Verify your email First!", Toast.LENGTH_SHORT).show()
                    return@addOnCompleteListener
                }
            }
        }

    }


}
