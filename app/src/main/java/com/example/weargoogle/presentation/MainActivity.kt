/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.example.weargoogle.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText

import com.example.weargoogle.R
import com.example.weargoogle.presentation.theme.WeargoogleTheme
import com.example.weargoogle.service.WatchDataListenerService

class MainActivity : ComponentActivity() {
    
    private lateinit var viewModel: AgriViewModel
    
    private val sensorDataReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == WatchDataListenerService.ACTION_SENSOR_DATA_RECEIVED) {
                // Get sensor values from intent
                val temperature = intent.getIntExtra(WatchDataListenerService.KEY_TEMPERATURE, 0)
                val humidity = intent.getIntExtra(WatchDataListenerService.KEY_HUMIDITY, 0)
                val airQuality = intent.getIntExtra(WatchDataListenerService.KEY_AIR_QUALITY, 0)
                val luminosity = intent.getIntExtra(WatchDataListenerService.KEY_LUMINOSITY, 0)
                val pressure = intent.getIntExtra(WatchDataListenerService.KEY_PRESSURE, 0)
                
                // Update view model with values from watch
                viewModel.updateSensorValue("temperature", temperature)
                viewModel.updateSensorValue("humidity", humidity)
                viewModel.updateSensorValue("airquality", airQuality)
                viewModel.updateSensorValue("luminosity", luminosity)
                viewModel.updateSensorValue("pressure", pressure)
                
                // Notify user
                Toast.makeText(context, "Data received from watch", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)
        
        // Register receiver for watch data with RECEIVER_NOT_EXPORTED flag
        registerReceiver(
            sensorDataReceiver,
            IntentFilter(WatchDataListenerService.ACTION_SENSOR_DATA_RECEIVED),
            Context.RECEIVER_NOT_EXPORTED
        )

        setContent {
            viewModel = AgriViewModel()
            val humidity by viewModel.getHumidity().collectAsState()
            val temperature by viewModel.getTemperature().collectAsState()
            val pressure by viewModel.getPressure().collectAsState()
            val luminosity by viewModel.getLuminosity().collectAsState()
            val airQuality by viewModel.getAirQuality().collectAsState()
            val gardenState by viewModel.getGardenState().collectAsState()

            // Initial test data (will be replaced with actual watch data)
            viewModel.updateSensorValue("temperature", 25)
            viewModel.updateSensorValue("humidity", 60)
            viewModel.updateSensorValue("pressure", 1013)
            viewModel.updateSensorValue("luminosity", 500)
            viewModel.updateSensorValue("airquality", 50)

            StatsScreen(
                humidity = humidity,
                temperature = temperature,
                pressure = pressure,
                luminosity = luminosity,
                airQuality = airQuality,
                gardenState = gardenState
            )
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Unregister the receiver when the activity is destroyed
        unregisterReceiver(sensorDataReceiver)
    }
}
