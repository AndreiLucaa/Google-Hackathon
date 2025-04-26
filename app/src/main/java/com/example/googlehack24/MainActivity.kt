package com.example.googlehack24

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var lastUpdatedTextView: TextView
    private lateinit var gardenStateTextView: TextView
    
    // Sensor value TextViews
    private lateinit var temperatureValueTextView: TextView
    private lateinit var humidityValueTextView: TextView
    private lateinit var airQualityValueTextView: TextView
    private lateinit var luminosityValueTextView: TextView
    private lateinit var pressureValueTextView: TextView
    
    // Sensor values
    private var temperatureValue = 24.5f
    private var humidityValue = 65
    private var airQualityValue = 35
    private var luminosityValue = 8500
    private var pressureValue = 1013
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Initialize UI components
        setupToolbar()
        setupNavigationDrawer()
        setupRefreshButton()
        setupGardenStateView()
        setupSensorViews()
        setupCardClickListeners()
        
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
    
    private fun setupSensorViews() {
        temperatureValueTextView = findViewById(R.id.tv_temperature_value)
        humidityValueTextView = findViewById(R.id.tv_humidity_value)
        airQualityValueTextView = findViewById(R.id.tv_air_value)
        luminosityValueTextView = findViewById(R.id.tv_luminosity_value)
        pressureValueTextView = findViewById(R.id.tv_pressure_value)
    }
    
    private fun setupCardClickListeners() {
        // Set up simple click listeners for cards
        findViewById<CardView>(R.id.card_temperature).setOnClickListener {
            Toast.makeText(this, "Temperature data", Toast.LENGTH_SHORT).show()
        }
        
        findViewById<CardView>(R.id.card_humidity).setOnClickListener {
            Toast.makeText(this, "Humidity data", Toast.LENGTH_SHORT).show()
        }
        
        findViewById<CardView>(R.id.card_air).setOnClickListener {
            Toast.makeText(this, "Air quality data", Toast.LENGTH_SHORT).show()
        }
        
        findViewById<CardView>(R.id.card_luminosity).setOnClickListener {
            Toast.makeText(this, "Luminosity data", Toast.LENGTH_SHORT).show()
        }
        
        findViewById<CardView>(R.id.card_pressure).setOnClickListener {
            Toast.makeText(this, "Pressure data", Toast.LENGTH_SHORT).show()
        }
        
        // Garden state card info message
        findViewById<CardView>(R.id.card_garden_state).setOnClickListener {
            Toast.makeText(this, "Garden state is calculated from sensor values", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun refreshData() {
        // Update last updated timestamp
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val currentTime = dateFormat.format(Date())
        lastUpdatedTextView.text = "Last updated: $currentTime"
        
        // In a real app, this would fetch data from the sensor
        // For now, we'll just use the stored values
        temperatureValueTextView.text = "${temperatureValue}°C"
        humidityValueTextView.text = "$humidityValue%"
        updateAirQualityText()
        luminosityValueTextView.text = "$luminosityValue lux"
        pressureValueTextView.text = "$pressureValue hPa"

        // Update garden state based on current values
        updateGardenState(calculateGardenState())

        // Check if the garden state is good or bad and inform the user
        val isGardenHealthy = isGardenHealthy(calculateGardenState())
        if (!isGardenHealthy) {
            Toast.makeText(this, "Warning: Your garden needs attention!", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateAirQualityText() {
        // Determine air quality status based on AQI value
        val airStatus = when {
            airQualityValue <= 50 -> "Good"
            airQualityValue <= 100 -> "Moderate"
            airQualityValue <= 150 -> "Unhealthy (Sensitive)"
            airQualityValue <= 200 -> "Unhealthy"
            airQualityValue <= 300 -> "Very Unhealthy"
            else -> "Hazardous"
        }

        airQualityValueTextView.text = "$airStatus ($airQualityValue AQI)"
    }

    private fun calculateGardenState(): String {
        // This is a simple calculation for demonstration
        // In a real app, you would use more complex logic based on plant needs

        var scoreCount = 0
        var totalScore = 0

        // Temperature score (15-30°C is optimal)
        if (temperatureValue in 15.0f..30.0f) {
            totalScore += 3
        } else if (temperatureValue in 5.0f..35.0f) {
            totalScore += 2
        } else {
            totalScore += 1
        }
        scoreCount++

        // Humidity score (40-70% is optimal)
        if (humidityValue in 40..70) {
            totalScore += 3
        } else if (humidityValue in 20..90) {
            totalScore += 2
        } else {
            totalScore += 1
        }
        scoreCount++

        // Air quality score
        if (airQualityValue <= 50) {
            totalScore += 3
        } else if (airQualityValue <= 100) {
            totalScore += 2
        } else {
            totalScore += 1
        }
        scoreCount++

        // Luminosity score depends on plant type, using a general range
        if (luminosityValue in 3000..10000) {
            totalScore += 3
        } else if (luminosityValue in 1000..15000) {
            totalScore += 2
        } else {
            totalScore += 1
        }
        scoreCount++

        // Calculate average score
        val averageScore = totalScore.toFloat() / scoreCount

        return when {
            averageScore >= 2.8 -> "VERY GOOD"
            averageScore >= 2.3 -> "GOOD"
            averageScore >= 1.8 -> "OK"
            averageScore >= 1.3 -> "BAD"
            else -> "VERY BAD"
        }
    }

    private fun isGardenHealthy(state: String): Boolean {
        return when (state) {
            "VERY GOOD", "GOOD" -> true
            "OK", "BAD", "VERY BAD" -> false
            else -> false
        }
    }

    private fun generateRandomSensorData() {
        // Generate random values within realistic ranges
        temperatureValue = Random.nextFloat() * 40 - 5  // Range: -5 to 35°C
        humidityValue = Random.nextInt(0, 101)          // Range: 0-100%
        airQualityValue = Random.nextInt(0, 301)        // Range: 0-300 AQI
        luminosityValue = Random.nextInt(0, 20001)      // Range: 0-20000 lux
        pressureValue = Random.nextInt(950, 1051)       // Range: 950-1050 hPa

        // Update UI with random values
        refreshData()

        // Notify user
        Toast.makeText(this, "Random sensor data generated", Toast.LENGTH_SHORT).show()
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
            R.id.nav_random_data -> {
                // Generate random data
                generateRandomSensorData()
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
