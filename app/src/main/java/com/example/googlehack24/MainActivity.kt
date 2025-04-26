package com.example.googlehack24

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.googlehack24.ProfileActivity  // Added import for ProfileActivity

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var lastUpdatedTextView: TextView
    private lateinit var gardenStateTextView: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Initialize UI components
        setupToolbar()
        setupNavigationDrawer()
        setupRefreshButton()
        setupGardenStateView()
        
        // Initial data refresh
        refreshData()
    }
    
    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }
    
    private fun setupNavigationDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        
        // Set up the drawer toggle
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, findViewById(R.id.toolbar),
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        
        // Set navigation item click listener
        navigationView.setNavigationItemSelectedListener(this)
    }
    
    private fun setupRefreshButton() {
        val fabRefresh = findViewById<FloatingActionButton>(R.id.fab_refresh)
        lastUpdatedTextView = findViewById(R.id.tv_last_updated)
        
        fabRefresh.setOnClickListener {
            refreshData()
            Toast.makeText(this, "Refreshing data...", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupGardenStateView() {
        gardenStateTextView = findViewById(R.id.tv_garden_state_value)
    }
    
    private fun refreshData() {
        // This would connect to your data source in a real app
        // For now, we'll just update the timestamp and set a random garden state
        
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val currentTime = dateFormat.format(Date())
        lastUpdatedTextView.text = "Last updated: $currentTime"
        
        // Update garden state (in a real app, this would be calculated based on sensor data)
        updateGardenState(getRandomGardenState())
        
        // Here you would update all the sensor data
        // updateTemperature()
        // updateHumidity()
        // etc.
    }
    
    private fun getRandomGardenState(): String {
        val states = listOf("VERY GOOD", "GOOD", "OK", "BAD", "VERY BAD")
        return states.random()
    }
    
    private fun updateGardenState(state: String) {
        gardenStateTextView.text = state
        
        // Set the color based on the state
        val color = when (state) {
            "VERY GOOD" -> R.color.primary_green
            "GOOD" -> R.color.accent_temperature
            "OK" -> R.color.accent_luminosity
            "BAD" -> R.color.accent_humidity
            "VERY BAD" -> android.R.color.holo_red_dark
            else -> R.color.text_primary
        }
        
        gardenStateTextView.setTextColor(ContextCompat.getColor(this, color))
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                // Navigate to settings activity
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                // Already on home
            }
            R.id.nav_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_history -> {
                // Navigate to history page
                Toast.makeText(this, "History", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_profile -> {
                // Fix: Proper intent creation for ProfileActivity
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_logout -> {
                // Handle logout
                Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show()
            }
        }
        
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
