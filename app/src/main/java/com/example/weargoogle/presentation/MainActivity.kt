/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.example.weargoogle.presentation

import android.os.Bundle
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            val viewModel = AgriViewModel()
            val humidity by viewModel.getHumidity().collectAsState()
            val temperature by viewModel.getTemperature().collectAsState()
            val pressure by viewModel.getPressure().collectAsState()
            val luminosity by viewModel.getLuminosity().collectAsState()
            val airQuality by viewModel.getAirQuality().collectAsState()
            val gardenState by viewModel.getGardenState().collectAsState()

            // Test data
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
}
