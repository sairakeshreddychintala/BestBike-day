package com.first_app.bestbikeday.weather

data class WeatherResponse(
    val forecast: Forecast
)

data class Forecast(
    val forecastday: List<Forecastday>
)

data class Forecastday(
    val date: String,
    val day: Day,
    val astro: Astro
)

data class Day(
    val maxtemp_c: Double,
    val mintemp_c: Double,
    val condition: Condition
)

data class Condition(
    val text: String,
    val icon: String
)

data class Astro(
    val sunrise: String,
    val sunset: String
)
