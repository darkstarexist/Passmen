package com.example.sample

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.widget.Toast


class PasswordAdapter(private val passwordList: MutableList<PasswordItem>) :
    RecyclerView.Adapter<PasswordAdapter.PasswordViewHolder>() {

    inner class PasswordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val siteEditText: EditText = itemView.findViewById(R.id.siteEdit)
        val emailEditText: EditText = itemView.findViewById(R.id.usernameEdit)
        val passwordEditText: EditText = itemView.findViewById(R.id.passwordEdit)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteimg)
        val copyBtn : ImageView = itemView.findViewById(R.id.copyimg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PasswordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_password, parent, false)
        return PasswordViewHolder(view)
    }

    override fun onBindViewHolder(holder: PasswordViewHolder, position: Int) {
        val passwordItem = passwordList[position]

        holder.siteEditText.setText(passwordItem.sitename)
        holder.emailEditText.setText(passwordItem.username)
        holder.passwordEditText.setText(passwordItem.password)


        // Setup autofill for site name
        val siteSuggestions = listOf("Google", "Facebook", "Instagram", "Twitter", "LinkedIn")
        val adapter = ArrayAdapter(holder.itemView.context, android.R.layout.simple_dropdown_item_1line, siteSuggestions)
        (holder.siteEditText as? AutoCompleteTextView)?.setAdapter(adapter)

        //copy to cliboard
        holder.copyBtn.setOnClickListener {
            val password = passwordItem.password
            Log.d("PasswordAdapter", "Password to copy: $password")
            copyToClipboard(holder.itemView.context, password)
        }

        // Efficient deletion with error handling
        holder.deleteButton.setOnClickListener {
            val pos = holder.adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                val item = passwordList.getOrNull(pos) // Safe get
                val itemKey = item?.key

                if (itemKey != null) {
                    val databaseReference = FirebaseDatabase.getInstance().getReference("passwords")
                    databaseReference.child(itemKey).removeValue().addOnSuccessListener {
                        // Ensure the item is still in the list before removing
                        if (pos != RecyclerView.NO_POSITION && pos < passwordList.size) {
                            passwordList.removeAt(pos)
                            notifyItemRemoved(pos)
                        }
                    }.addOnFailureListener {
                        // Handle any errors during deletion
                    }
                }
            }
        }

        // Efficient update when user finishes editing site name
        holder.siteEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { // Only when focus is lost
                val pos = holder.adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    val updatedItem = passwordList[pos]
                    updatedItem.sitename = holder.siteEditText.text.toString()

                    // Update the site name in Firebase
                    updatedItem.key?.let {
                        FirebaseDatabase.getInstance().getReference("passwords")
                            .child(it) // Use the item's key for updating
                            .setValue(updatedItem).addOnSuccessListener {
                                notifyItemChanged(pos)
                            }.addOnFailureListener {
                                // Handle failure (e.g., network errors)
                            }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return passwordList.size
    }

    // Function to copy text to clipboard
    fun copyToClipboard(context: Context, text: String) {
        val clipboard: ClipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("password", text)
        clipboard.setPrimaryClip(clip)
        // Show a Toast message to notify the user
        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
    }

}
