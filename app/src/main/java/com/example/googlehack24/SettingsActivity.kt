package com.example.googlehack24

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SeekBarPreference

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings_container, SettingsFragment())
                .commit()
        }
        
        // Configure toolbar
        setSupportActionBar(findViewById(R.id.settings_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Settings"
    }
    
    // Handle the back button press
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    
    class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {
        
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)
            
            // Set listeners for threshold preferences
            setupThresholdPreference("max_temperature")
            setupThresholdPreference("min_temperature")
            setupThresholdPreference("max_humidity")
            setupThresholdPreference("min_humidity")
            setupThresholdPreference("max_air_quality")
            setupThresholdPreference("min_air_quality")
            setupThresholdPreference("max_pressure")
            setupThresholdPreference("min_pressure")
            setupThresholdPreference("max_luminosity")
            setupThresholdPreference("min_luminosity")
            
            // Setup logout preference
            findPreference<Preference>("logout")?.setOnPreferenceClickListener {
                // In a real app, implement logout functionality here
                true
            }
        }
        
        private fun setupThresholdPreference(key: String) {
            val preference = findPreference<SeekBarPreference>(key)
            preference?.onPreferenceChangeListener = this
            
            // Set summary with current value
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
            val value = sharedPreferences.getInt(key, preference?.value ?: 0)
            updateThresholdSummary(preference, value)
        }
        
        override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
            when (preference.key) {
                "max_temperature", "min_temperature", "max_humidity", 
                "min_humidity", "max_air_quality", "min_air_quality",
                "max_pressure", "min_pressure", "max_luminosity", "min_luminosity" -> {
                    updateThresholdSummary(preference, newValue as Int)
                }
            }
            return true
        }
        
        private fun updateThresholdSummary(preference: Preference?, value: Int) {
            val unit = when (preference?.key) {
                "max_temperature", "min_temperature" -> "°C"
                "max_pressure", "min_pressure" -> "hPa"
                "max_air_quality", "min_air_quality" -> "AQI"
                "max_luminosity", "min_luminosity" -> "lux"
                else -> "%"
            }
            
            preference?.summary = when (preference?.key) {
                "max_temperature" -> "Alert when temperature exceeds $value$unit"
                "min_temperature" -> "Alert when temperature falls below $value$unit"
                "max_humidity" -> "Alert when humidity exceeds $value$unit"
                "min_humidity" -> "Alert when humidity falls below $value$unit"
                "max_air_quality" -> "Alert when air quality exceeds $value$unit"
                "min_air_quality" -> "Alert when air quality falls below $value$unit"
                "max_pressure" -> "Alert when pressure exceeds $value$unit"
                "min_pressure" -> "Alert when pressure falls below $value$unit"
                "max_luminosity" -> "Alert when luminosity exceeds $value$unit"
                "min_luminosity" -> "Alert when luminosity falls below $value$unit"
                else -> preference?.summary.toString()
            }
        }
    }
}
