package com.example.sample

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.random.Random
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast

class GeneratePassword : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()


        enableEdgeToEdge()
        setContentView(R.layout.activity_generate_password)

        // Edge-to-edge support
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        //set size
        val size = 12
        val sizeEdit = findViewById<EditText>(R.id.size_Edit)
        sizeEdit.setText(size.toString())

        // Logout button functionality
        val logoutBtn = findViewById<Button>(R.id.logout_Btn)
        logoutBtn.setOnClickListener {
            auth.signOut()
            Log.d("Dashboard", "User logged out")
            startActivity(Intent(this, Login::class.java))
            finish()
        }

        // Generate password button functionality

        val genPassBtn = findViewById<ImageView>(R.id.genPassBtn)
        genPassBtn.setOnClickListener {
            val passwordEdit = findViewById<EditText>(R.id.password_Edit)
            val sizeEdit = findViewById<EditText>(R.id.size_Edit).text.toString().trim()
            val size = sizeEdit.toIntOrNull()?:0

            if(size == 0){
                Toast.makeText(this, "Please Enter a Valid Size!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if(size > 18){
                Toast.makeText(this, "Password size must be less than 18", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else{
                val newPassword = generatePassword(size)
                passwordEdit.setText(newPassword)
            }
        }

        //show password btn
        findViewById<Button>(R.id.showPassBtn).setOnClickListener {
            startActivity(Intent(this, Passwords::class.java))
        }


        // Save password entry functionality
        val savePassBtn = findViewById<Button>(R.id.savePassBtn)
        savePassBtn.setOnClickListener {
            val sitename = findViewById<EditText>(R.id.site_Edit).text.toString().trim()
            val username = findViewById<EditText>(R.id.username_Edit).text.toString().trim()
            val password = findViewById<EditText>(R.id.password_Edit).text.toString().trim()
            val sizeEdit = findViewById<EditText>(R.id.size_Edit).text.toString().trim()
            val size = sizeEdit.toIntOrNull()?:0

            // Check if fields are not empty before saving
            if (sitename.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty() && password.length > 8) {
                savePasswordEntry(sitename, username, password)
            }
            else if(size == 0){
                Toast.makeText(this, "Please Enter a Valid Size!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
             else if(password.length < 8){
                Toast.makeText(this,"Password Size must be greater than 8!",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
             else {
                Toast.makeText(this, "All field must be filled!", Toast.LENGTH_SHORT).show()
                Log.e("Password Manager", "All fields must be filled.")
            }
        }
    }



    // Function to generate a random password
    private fun generatePassword(size: Int): String {
        val chars =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+<>?"
        return (1..size)
            .map { chars[Random.nextInt(chars.length)] }
            .joinToString("")
    }

    // Function to save password entry to Firebase Database
    private fun savePasswordEntry(sitename: String, username: String, password: String) {
        val database = FirebaseDatabase.getInstance()
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val reference = database.getReference("passwords")

        data class PasswordEntry(
            val sitename: String = "",
            val username: String = "",
            val password: String = "",
            val email:String = ""

        )

        val entryId = reference.push().key // Generate a unique ID
        val email = user!!.email

        val entry = PasswordEntry(sitename, username, password, email.toString())

        if (entryId != null) {
            reference.child(entryId).setValue(entry)
                .addOnSuccessListener {
                    Log.d("Firebase", "Password saved successfully")
                    copyToClipboard(this,"Password",password)
                    Toast.makeText(this, "Password Copied to Clipboard!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Log.e("Firebase", "Failed to save: ${it.message}")
                    Toast.makeText(this,"Saving Failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    fun copyToClipboard(context: Context, label: String, text: String) {
        val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
    }
}
