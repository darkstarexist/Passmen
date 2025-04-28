package com.example.sample

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class Passwords : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var passwordAdapter: PasswordAdapter
    private val passwordList = mutableListOf<PasswordItem>()

    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cycleview) // Make sure your layout file is correct

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the adapter upfront
        passwordAdapter = PasswordAdapter(passwordList)
        recyclerView.adapter = passwordAdapter

        databaseReference = FirebaseDatabase.getInstance().getReference("passwords")

        // Fetch data from Firebase
        fetchDataFromFirebase()
    }

    private fun fetchDataFromFirebase() {
        // Get the current user's email from Firebase Authentication
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email

        if (currentUserEmail != null) {
            // Filter data based on the current user's email
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    passwordList.clear()

                    for (dataSnapshot in snapshot.children) {
                        val passwordItem = dataSnapshot.getValue(PasswordItem::class.java)
                        passwordItem?.key = dataSnapshot.key // Save key

                        // Check if the passwordItem's email matches the current user's email
                        if (passwordItem?.email == currentUserEmail) {
                            passwordItem.let {
                                passwordList.add(it)
                            }
                        }
                    }

                    // Notify the adapter after filtering the data
                    passwordAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@Passwords, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            // Handle case where the user is not logged in
            Toast.makeText(this@Passwords, "Please log in to access your passwords", Toast.LENGTH_SHORT).show()
        }
    }
}


