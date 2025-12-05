package com.first_app.bestbikeday.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

class WeatherViewModel : ViewModel() {

    private val _weatherForecast = MutableStateFlow<WeatherResponse?>(null)
    val weatherForecast: StateFlow<WeatherResponse?> = _weatherForecast

    private val API_KEY = "008801018d9c4c27a6c163710251911" // Replace with your actual API key

    fun fetchWeatherForecast(location: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.weatherService.getForecast(API_KEY, location)
                if (response.isSuccessful && response.body() != null) {
                    _weatherForecast.value = response.body()
                } else {
                    // Handle error
                    println("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                // Handle exception
                e.printStackTrace()
            }
        }
    }

    fun calculateBikeRideScore(forecastday: Forecastday): Int {
        val avgTemp = (forecastday.day.maxtemp_c + forecastday.day.mintemp_c) / 2
        val conditionText = forecastday.day.condition.text

        var tempScore = 0.0
        when {
            avgTemp >= 18 && avgTemp <= 25 -> tempScore = 100.0
            avgTemp >= 15 && avgTemp < 18 -> tempScore = 70.0 + (30.0 * (avgTemp - 15) / 3)
            avgTemp > 25 && avgTemp <= 28 -> tempScore = 70.0 + (30.0 * (28 - avgTemp) / 3)
            avgTemp >= 10 && avgTemp < 15 -> tempScore = 30.0 + (40.0 * (avgTemp - 10) / 5)
            avgTemp > 28 && avgTemp <= 32 -> tempScore = 30.0 + (40.0 * (32 - avgTemp) / 4)
            else -> tempScore = 0.0
        }

        var conditionScore = 0.0
        when (conditionText) {
            "Clear", "Sunny" -> conditionScore = 100.0
            "Partly cloudy" -> conditionScore = 90.0
            "Cloudy", "Overcast" -> conditionScore = 70.0
            "Mist", "Fog" -> conditionScore = 50.0
            "Light rain", "Patchy light rain", "Light drizzle" -> conditionScore = 40.0
            else -> conditionScore = 0.0 // Heavy rain, snow, thunderstorm, etc.
        }

        val finalScore = (tempScore * 0.4 + conditionScore * 0.6).toInt()
        return min(100, max(0, finalScore))
    }
}
