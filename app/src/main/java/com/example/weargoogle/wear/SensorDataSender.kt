package com.example.weargoogle.wear

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.example.weargoogle.service.WatchDataListenerService
import java.util.concurrent.TimeUnit

/**
 * Helper class for the watch-side app to collect sensor data and send to the phone.
 */
class SensorDataSender(private val context: Context) : SensorEventListener {

    private val TAG = "SensorDataSender"
    private val PATH_SENSOR_DATA = "/sensor-data"
    // Advertise the capability as "appkey"
    private val CAPABILITY_GARDEN_SENSORS = "appkey"

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val dataClient = Wearable.getDataClient(context)
    private val messageClient = Wearable.getMessageClient(context)
    private val capabilityClient = Wearable.getCapabilityClient(context)

    private var temperature = 0f
    private var humidity = 0
    private var pressure = 0
    private var airQuality = 0
    private var luminosity = 0

    fun startSensors() {
        sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        } ?: Log.w(TAG, "No temperature sensor available")
        sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        } ?: Log.w(TAG, "No humidity sensor available")
        sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        } ?: Log.w(TAG, "No pressure sensor available")
        sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        } ?: Log.w(TAG, "No light sensor available")

        registerCapability()
    }

    fun stopSensors() {
        sensorManager.unregisterListener(this)
        capabilityClient.removeLocalCapability(CAPABILITY_GARDEN_SENSORS)
            .addOnSuccessListener { Log.d(TAG, "Removed capability: $CAPABILITY_GARDEN_SENSORS") }
            .addOnFailureListener { e -> Log.e(TAG, "Failed to remove capability: ${e.message}") }
    }

    private fun registerCapability() {
        capabilityClient.addLocalCapability(CAPABILITY_GARDEN_SENSORS)
            .addOnSuccessListener { Log.d(TAG, "Advertised capability: $CAPABILITY_GARDEN_SENSORS") }
            .addOnFailureListener { e -> Log.e(TAG, "Failed to advertise capability: ${e.message}") }
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_AMBIENT_TEMPERATURE -> temperature = event.values[0]
            Sensor.TYPE_RELATIVE_HUMIDITY -> humidity = event.values[0].toInt()
            Sensor.TYPE_PRESSURE -> pressure = event.values[0].toInt()
            Sensor.TYPE_LIGHT -> luminosity = event.values[0].toInt()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No action on accuracy changes
    }

    fun sendSensorDataToPhone() {
        sendDataItem()
        sendMessageToPhone()
    }

    private fun sendDataItem() {
        val request = PutDataMapRequest.create(PATH_SENSOR_DATA).apply {
            dataMap.putFloat(WatchDataListenerService.KEY_TEMPERATURE, temperature)
            dataMap.putInt(WatchDataListenerService.KEY_HUMIDITY, humidity)
            dataMap.putInt(WatchDataListenerService.KEY_PRESSURE, pressure)
            dataMap.putInt(WatchDataListenerService.KEY_LUMINOSITY, luminosity)
            dataMap.putInt(WatchDataListenerService.KEY_AIR_QUALITY, airQuality)
        }.asPutDataRequest().setUrgent()

        dataClient.putDataItem(request)
            .addOnSuccessListener { Log.d(TAG, "Sensor data sent via DataClient") }
            .addOnFailureListener { e -> Log.e(TAG, "Failed to send sensor data: ${e.message}") }
    }

    private fun sendMessageToPhone() {
        try {
            val nodes = Tasks.await(Wearable.getNodeClient(context).connectedNodes, 2, TimeUnit.SECONDS)
            if (nodes.isEmpty()) {
                Log.e(TAG, "No connected nodes found.")
                return
            }
            val dataString = """{"temperature":$temperature,"humidity":$humidity,"pressure":$pressure,"luminosity":$luminosity,"airQuality":$airQuality}"""
            nodes.forEach { node ->
                messageClient.sendMessage(node.id, PATH_SENSOR_DATA, dataString.toByteArray())
                    .addOnSuccessListener { Log.d(TAG, "Message sent to ${node.displayName}") }
                    .addOnFailureListener { e -> Log.e(TAG, "Failed to send message: ${e.message}") }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error sending message: ${e.message}")
        }
    }
}
