package com.example.weargoogle.presentation
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class AgriViewModel : ViewModel() {
    private val Humidity = MutableStateFlow(0)
    private val Temperature = MutableStateFlow(0)
    private val Pressure = MutableStateFlow(0)
    private val Luminosity = MutableStateFlow(0)
    private val airQuality = MutableStateFlow(0)
    private val gardenState = MutableStateFlow(GardenState.NONE)

    private val minHumidity = MutableStateFlow(0)
    private val maxHumidity = MutableStateFlow(100)
    private val minTemperature = MutableStateFlow(0)
    private val maxTemperature = MutableStateFlow(50)
    private val minPressure = MutableStateFlow(0)
    private val maxPressure = MutableStateFlow(100)
    private val minLuminosity = MutableStateFlow(0)
    private val maxLuminosity = MutableStateFlow(1000)
    private val minAirQuality = MutableStateFlow(0)
    private val maxAirQuality = MutableStateFlow(500)

    init {
        evaluateGardenState()
    }

    private fun evaluateGardenState() {
        val state = when {
            isAnyValueInDangerZone() -> GardenState.BAD
            isAnyValueInWarningZone() -> GardenState.WARNING
            areAllValuesGood() -> GardenState.GOOD
            else -> GardenState.NONE
        }
        gardenState.value = state
    }

    private fun isAnyValueInDangerZone(): Boolean {
        return Temperature.value > maxTemperature.value * 0.9 ||
                Humidity.value < minHumidity.value * 1.1 ||
                Pressure.value < minPressure.value * 1.1 ||
                Luminosity.value > maxLuminosity.value * 0.9 ||
                airQuality.value > maxAirQuality.value * 0.8
    }

    private fun isAnyValueInWarningZone(): Boolean {
        return Temperature.value > maxTemperature.value * 0.8 ||
                Humidity.value < minHumidity.value * 1.2 ||
                Pressure.value < minPressure.value * 1.2 ||
                Luminosity.value > maxLuminosity.value * 0.8 ||
                airQuality.value > maxAirQuality.value * 0.6
    }

    private fun areAllValuesGood(): Boolean {
        return Temperature.value in (minTemperature.value..maxTemperature.value) &&
                Humidity.value in (minHumidity.value..maxHumidity.value) &&
                Pressure.value in (minPressure.value..maxPressure.value) &&
                Luminosity.value in (minLuminosity.value..maxLuminosity.value) &&
                airQuality.value in (minAirQuality.value..maxAirQuality.value)
    }

    fun updateSensorValue(sensorType: String, value: Int) {
        when (sensorType.lowercase()) {
            "temperature" -> Temperature.value = value
            "humidity" -> Humidity.value = value
            "pressure" -> Pressure.value = value
            "luminosity" -> Luminosity.value = value
            "airquality" -> airQuality.value = value
        }
        evaluateGardenState()
    }

    fun getHumidity() = Humidity
    fun getTemperature() = Temperature
    fun getPressure() = Pressure
    fun getLuminosity() = Luminosity
    fun getAirQuality() = airQuality
    fun getGardenState() = gardenState
}
