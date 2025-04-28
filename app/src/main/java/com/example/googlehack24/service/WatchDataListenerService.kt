package com.example.googlehack24.service

import android.content.Intent
import android.util.Log
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class WatchDataListenerService : WearableListenerService() {

    companion object {
        private const val TAG = "WatchDataListenerService"
        const val GARDEN_DATA_PATH = "/garden_data"
        const val CAPABILITY_GARDEN_SENSORS = "appkey" // Must match the watch's capability string
    }

    override fun onDataChanged(events: DataEventBuffer) {
        Log.d(TAG, "onDataChanged: ${events.count} events received")
        
        for (event in events) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val path = event.dataItem.uri.path
                Log.d(TAG, "Data changed for path: $path")
                
                if (path == GARDEN_DATA_PATH) {
                    val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                    
                    // Extract the data from the dataMap
                    val gardenState = dataMap.getString("garden_state", "")
                    val temperature = dataMap.getFloat("temperature", 0f)
                    val humidity = dataMap.getInt("humidity", 0)
                    val airQuality = dataMap.getInt("air_quality", 0)
                    val luminosity = dataMap.getInt("luminosity", 0)
                    val pressure = dataMap.getInt("pressure", 0)
                    val timestamp = dataMap.getLong("timestamp", 0)
                    
                    Log.d(TAG, "Received garden data: garden_state=$gardenState, temperature=$temperature, " +
                           "humidity=$humidity, air_quality=$airQuality, luminosity=$luminosity, " +
                           "pressure=$pressure, timestamp=$timestamp")
                    
                    // You could broadcast this data to update the UI if MainActivity is open
                    val intent = Intent("com.example.googlehack24.GARDEN_DATA_UPDATED")
                    intent.putExtra("garden_state", gardenState)
                    intent.putExtra("temperature", temperature)
                    intent.putExtra("humidity", humidity)
                    intent.putExtra("air_quality", airQuality)
                    intent.putExtra("luminosity", luminosity)
                    intent.putExtra("pressure", pressure)
                    sendBroadcast(intent)
                }
            }
        }
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        Log.d(TAG, "onMessageReceived: ${messageEvent.path}")
        
        if (messageEvent.path == GARDEN_DATA_PATH) {
            val payload = String(messageEvent.data)
            Log.d(TAG, "Message payload: $payload")
            
            // Parse the payload and handle it as needed
            // This is an alternative to using DataItems
        }
    }
}
