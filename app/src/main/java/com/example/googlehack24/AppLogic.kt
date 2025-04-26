package com.example.googlehack24

class AppLogic {
    fun checkForRange(min: Int, max: Int, value: Int): Boolean {
        return value in min..max
    }
    
    /**
     * Checks if temperature is in optimal range for most plants
     * @param temperature The current temperature in Celsius
     * @return true if temperature is optimal, false otherwise
     */
    fun isTemperatureOptimal(temperature: Float): Boolean {
        return temperature in 15.0f..30.0f
    }
    
    /**
     * Checks if humidity is in optimal range for most plants
     * @param humidity The current humidity percentage
     * @return true if humidity is optimal, false otherwise
     */
    fun isHumidityOptimal(humidity: Int): Boolean {
        return humidity in 40..70
    }
    
    /**
     * Evaluates if the garden state indicates a healthy garden
     * @param gardenState The state of the garden as a string
     * @return true if the garden is in a healthy state, false otherwise
     */
    fun evaluateGardenHealth(gardenState: String): Boolean {
        return when (gardenState) {
            "VERY GOOD", "GOOD" -> true
            "OK", "BAD", "VERY BAD" -> false
            else -> false
        }
    }
}
