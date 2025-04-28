package com.example.googlehack24

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
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
import com.example.googlehack24.service.WatchDataListenerService
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
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
    
    // Wearable clients
    private lateinit var dataClient: DataClient
    private lateinit var capabilityClient: CapabilityClient
    
    // Sensor values
    private var temperatureValue = 24.5f
    private var humidityValue = 65
    private var airQualityValue = 35
    private var luminosityValue = 8500
    private var pressureValue = 1013
    
    // BroadcastReceiver for garden data updates from watch
    private val gardenDataReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == "com.example.googlehack24.GARDEN_DATA_UPDATED") {
                // Update UI with values from watch if needed
                val gardenState = intent.getStringExtra("garden_state")
                val temperature = intent.getFloatExtra("temperature", -1f)
                val humidity = intent.getIntExtra("humidity", -1)
                val airQuality = intent.getIntExtra("air_quality", -1)
                val luminosity = intent.getIntExtra("luminosity", -1)
                val pressure = intent.getIntExtra("pressure", -1)
                
                Log.d("MainActivity", "Received garden data from watch: garden_state=$gardenState, " +
                       "temperature=$temperature, humidity=$humidity")
                
                Toast.makeText(context, "Received data from watch", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Initialize Wearable clients
        dataClient = Wearable.getDataClient(this)
        capabilityClient = Wearable.getCapabilityClient(this)
        
        // Register the capability name
        capabilityClient.addLocalCapability(WatchDataListenerService.CAPABILITY_GARDEN_SENSORS)
            .addOnSuccessListener { 
                Log.d("MainActivity", "Successfully registered capability") 
            }
            .addOnFailureListener { e ->
                Log.e("MainActivity", "Failed to register capability", e)
            }
        
        // Initialize UI components
        setupToolbar()
        setupNavigationDrawer()
        setupRefreshButton()
        setupGardenStateView()
        setupSensorViews()
        setupCardClickListeners()
        setupSendToWatchButton()
        
        // Initial data refresh
        refreshData()
        
        // Register BroadcastReceiver for watch data updates
        registerReceiver(gardenDataReceiver, IntentFilter("com.example.googlehack24.GARDEN_DATA_UPDATED"))
    }
    
    override fun onDestroy() {
        // Unregister the receiver
        try {
            unregisterReceiver(gardenDataReceiver)
        } catch (e: Exception) {
            Log.e("MainActivity", "Error unregistering receiver", e)
        }
        
        // Remove the capability when app is closed
        capabilityClient.removeLocalCapability(WatchDataListenerService.CAPABILITY_GARDEN_SENSORS)
            .addOnFailureListener { e ->
                Log.e("MainActivity", "Failed to remove capability", e)
            }
        
        super.onDestroy()
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
        airQualityValueTextView = findViewById(R.id.tv_air_quality_value)
        luminosityValueTextView = findViewById(R.id.tv_luminosity_value)
        pressureValueTextView = findViewById(R.id.tv_pressure_value)
    }
    
    private fun setupCardClickListeners() {
        // Set click listeners for the sensor cards to show detailed info
        findViewById<CardView>(R.id.card_temperature).setOnClickListener {
            Toast.makeText(this, "Temperature: $temperatureValue°C", Toast.LENGTH_SHORT).show()
        }
        
        findViewById<CardView>(R.id.card_humidity).setOnClickListener {
            Toast.makeText(this, "Humidity: $humidityValue%", Toast.LENGTH_SHORT).show()
        }
        
        findViewById<CardView>(R.id.card_air_quality).setOnClickListener {
            Toast.makeText(this, "Air Quality: $airQualityValue AQI", Toast.LENGTH_SHORT).show()
        }
        
        findViewById<CardView>(R.id.card_luminosity).setOnClickListener {
            Toast.makeText(this, "Luminosity: $luminosityValue lux", Toast.LENGTH_SHORT).show()
        }
        
        findViewById<CardView>(R.id.card_pressure).setOnClickListener {
            Toast.makeText(this, "Pressure: $pressureValue hPa", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupSendToWatchButton() {
        findViewById<CardView>(R.id.card_send_to_watch).setOnClickListener {
            sendDataToWatch()
            Toast.makeText(this, "Sending data to watch...", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun sendDataToWatch() {
        // Create a DataMap to store the sensor values
        val gardenState = calculateGardenState()
        val putDataMapReq = PutDataMapRequest.create(WatchDataListenerService.GARDEN_DATA_PATH).apply {
            dataMap.apply {
                putString("garden_state", gardenState)
                putFloat("temperature", temperatureValue)
                putInt("humidity", humidityValue)
                putInt("air_quality", airQualityValue)
                putInt("luminosity", luminosityValue)
                putInt("pressure", pressureValue)
                putLong("timestamp", System.currentTimeMillis())
            }
        }
        
        // Log the data before sending
        Log.d("SendData", "Sending data: garden_state=$gardenState, temperature=$temperatureValue, humidity=$humidityValue, air_quality=$airQualityValue, luminosity=$luminosityValue, pressure=$pressureValue, timestamp=${System.currentTimeMillis()}")
        
        val putDataReq = putDataMapReq.asPutDataRequest().setUrgent()
        
        // Send the data
        dataClient.putDataItem(putDataReq)
            .addOnSuccessListener {
                Toast.makeText(this, "Data sent successfully to watch", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to send data: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("SendData", "Failed to send data", e)
            }
    }
    
    private fun refreshData() {
        // Generate new random sensor data
        generateRandomSensorData()
        
        // Update the UI with new values
        temperatureValueTextView.text = String.format(Locale.getDefault(), "%.1f°C", temperatureValue)
        humidityValueTextView.text = "$humidityValue%"
        airQualityValueTextView.text = "$airQualityValue AQI"
        updateAirQualityText()
        luminosityValueTextView.text = "$luminosityValue lux"
        pressureValueTextView.text = "$pressureValue hPa"
        
        // Calculate and update garden state
        val gardenState = calculateGardenState()
        updateGardenState(gardenState)
        
        // Update the last updated timestamp
        val dateFormat = SimpleDateFormat("MMM dd, HH:mm:ss", Locale.getDefault())
        val currentTimeString = dateFormat.format(Date())
        lastUpdatedTextView.text = "Last updated: $currentTimeString"
    }

    private fun updateAirQualityText() {
        // Set text color based on air quality value
        val color = when {
            airQualityValue < 50 -> ContextCompat.getColor(this, R.color.good_state)
            airQualityValue < 100 -> ContextCompat.getColor(this, R.color.moderate_state)
            else -> ContextCompat.getColor(this, R.color.bad_state)
        }
        airQualityValueTextView.setTextColor(color)
    }

    private fun calculateGardenState(): String {
        val appLogic = AppLogic()
        
        // Calculate garden state based on sensor values
        val tempOptimal = appLogic.isTemperatureOptimal(temperatureValue)
        val humidityOptimal = appLogic.isHumidityOptimal(humidityValue)
        val airQualityOptimal = airQualityValue < 50
        val luminosityOptimal = luminosityValue in 2000..20000
        val pressureOptimal = pressureValue in 980..1050
        
        val optimalCount = listOf(tempOptimal, humidityOptimal, airQualityOptimal, luminosityOptimal, pressureOptimal)
            .count { it }
        
        return when {
            optimalCount == 5 -> "VERY GOOD"
            optimalCount == 4 -> "GOOD"
            optimalCount == 3 -> "OK"
            optimalCount == 2 -> "BAD"
            else -> "VERY BAD"
        }
    }

    private fun isGardenHealthy(state: String): Boolean {
        return AppLogic().evaluateGardenHealth(state)
    }

    private fun generateRandomSensorData() {
        // Generate random values for sensors within realistic ranges
        temperatureValue = Random.nextDouble(10.0, 35.0).toFloat()
        humidityValue = Random.nextInt(30, 80)
        airQualityValue = Random.nextInt(10, 150)
        luminosityValue = Random.nextInt(500, 30000)
        pressureValue = Random.nextInt(970, 1060)
    }

    private fun updateGardenState(state: String) {
        gardenStateTextView.text = state
        
        // Set text color based on garden state
        val color = when (state) {
            "VERY GOOD" -> ContextCompat.getColor(this, R.color.very_good_state)
            "GOOD" -> ContextCompat.getColor(this, R.color.good_state)
            "OK" -> ContextCompat.getColor(this, R.color.moderate_state)
            "BAD" -> ContextCompat.getColor(this, R.color.bad_state)
            else -> ContextCompat.getColor(this, R.color.very_bad_state)
        }
        gardenStateTextView.setTextColor(color)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.action_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                // Already on home, just close drawer
            }
            R.id.nav_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
            R.id.nav_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
            }
            R.id.nav_about -> {
                Toast.makeText(this, "About Garden Monitor App v1.0", Toast.LENGTH_SHORT).show()
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
