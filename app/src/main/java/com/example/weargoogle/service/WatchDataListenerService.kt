package com.example.weargoogle.service

import android.content.Intent
import android.util.Log
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import org.json.JSONException
import org.json.JSONObject

class WatchDataListenerService : WearableListenerService() {
    
    companion object {
        private const val TAG = "WatchDataListener"
        const val PATH_SENSOR_DATA = "/sensor-data"  // Make sure path has leading slash
        const val KEY_TEMPERATURE = "temperature"
        const val KEY_HUMIDITY = "humidity"
        const val KEY_AIR_QUALITY = "airQuality"
        const val KEY_LUMINOSITY = "luminosity"
        const val KEY_PRESSURE = "pressure"
        
        const val ACTION_SENSOR_DATA_RECEIVED = "com.example.weargoogle.SENSOR_DATA_RECEIVED"
    }
    
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "WatchDataListenerService created")
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "WatchDataListenerService started with command")
        return super.onStartCommand(intent, flags, startId)
    }
    
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.d(TAG, "onDataChanged triggered with ${dataEvents.count} events")
        
        dataEvents.forEach { event ->
            if (event.type == DataEvent.TYPE_CHANGED) {
                val path = event.dataItem.uri.path
                Log.d(TAG, "Data changed for path: $path")
                
                if (path == PATH_SENSOR_DATA) {
                    try {
                        val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                        
                        // Extract sensor values from watch
                        val temperature = dataMap.getFloat(KEY_TEMPERATURE, 0f).toInt()
                        val humidity = dataMap.getInt(KEY_HUMIDITY, 0)
                        val airQuality = dataMap.getInt(KEY_AIR_QUALITY, 0)
                        val luminosity = dataMap.getInt(KEY_LUMINOSITY, 0)
                        val pressure = dataMap.getInt(KEY_PRESSURE, 0)
                        
                        Log.d(TAG, "Received sensor data via DataClient: temp=$temperature, humidity=$humidity")
                        
                        // Broadcast the received data to MainActivity
                        sendSensorDataBroadcast(temperature, humidity, airQuality, luminosity, pressure)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error processing data item: ${e.message}")
                    }
                }
            }
        }
    }
    
    override fun onMessageReceived(messageEvent: MessageEvent) {
        Log.d(TAG, "Message received on path: ${messageEvent.path}")
        
        if (messageEvent.path == PATH_SENSOR_DATA) {
            try {
                // Parse the JSON string from the message
                val dataString = String(messageEvent.data)
                Log.d(TAG, "Received message data: $dataString")
                
                val jsonData = JSONObject(dataString)
                val temperature = jsonData.optInt(KEY_TEMPERATURE, 0)
                val humidity = jsonData.optInt(KEY_HUMIDITY, 0)
                val airQuality = jsonData.optInt(KEY_AIR_QUALITY, 0)
                val luminosity = jsonData.optInt(KEY_LUMINOSITY, 0)
                val pressure = jsonData.optInt(KEY_PRESSURE, 0)
                
                Log.d(TAG, "Received sensor data via MessageClient: temp=$temperature, humidity=$humidity")
                
                // Broadcast the received data to MainActivity
                sendSensorDataBroadcast(temperature, humidity, airQuality, luminosity, pressure)
            } catch (e: JSONException) {
                Log.e(TAG, "Error parsing JSON data: ${e.message}")
            }
        }
    }
    
    private fun sendSensorDataBroadcast(temperature: Int, humidity: Int, airQuality: Int, luminosity: Int, pressure: Int) {
        val intent = Intent(ACTION_SENSOR_DATA_RECEIVED).apply {
            putExtra(KEY_TEMPERATURE, temperature)
            putExtra(KEY_HUMIDITY, humidity)
            putExtra(KEY_AIR_QUALITY, airQuality)
            putExtra(KEY_LUMINOSITY, luminosity)
            putExtra(KEY_PRESSURE, pressure)
        }
        sendBroadcast(intent)
        Log.d(TAG, "Broadcast sent with sensor data")
    }
}
