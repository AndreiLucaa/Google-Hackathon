package com.example.googlehack24

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        
        // Setup toolbar
        val toolbar = findViewById<Toolbar>(R.id.profile_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Profile"
        
        // Bind user display and logout button
        val userPhotoImageView = findViewById<ImageView>(R.id.iv_user_photo)
        val userTextView = findViewById<TextView>(R.id.tv_user)
        val logoutButton = findViewById<Button>(R.id.btn_logout)
        
        // Show current user (in a real app, fetch from user session)
        userTextView.text = "John Doe"
        
        // Handle logout button click
        logoutButton.setOnClickListener {
            // In a real app, implement logout functionality here
            Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> { 
                finish() 
                true 
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
