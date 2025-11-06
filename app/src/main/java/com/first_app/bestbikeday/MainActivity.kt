package com.first_app.bestbikeday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.first_app.bestbikeday.ui.theme.BestBikeDayTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.first_app.bestbikeday.weather.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.Image
import coil.compose.rememberImagePainter
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BestBikeDayTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WeatherScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherScreen(modifier: Modifier = Modifier, weatherViewModel: WeatherViewModel = viewModel()) {
    var location by remember { mutableStateOf("") }
    val weatherForecast by weatherViewModel.weatherForecast.collectAsState()

    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Best Bike Day",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Enter Location") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { weatherViewModel.fetchWeatherForecast(location) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text("Get Weather", style = MaterialTheme.typography.titleMedium)
        }

        weatherForecast?.let { forecast ->
            LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(forecast.forecast.forecastday.take(3)) { forecastday ->
                    WeatherForecastItem(forecastday = forecastday, weatherViewModel = weatherViewModel)
                }
            }
        } ?: run {
            Text(
                text = "Enter a location to get the 3-day weather forecast.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Composable
fun WeatherForecastItem(forecastday: com.first_app.bestbikeday.weather.Forecastday, weatherViewModel: WeatherViewModel) {
    val score = weatherViewModel.calculateBikeRideScore(forecastday)
    val backgroundColor = lerp(Color.Red, Color.Green, score / 100f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = "Date: ${formatDate(forecastday.date)}", style = MaterialTheme.typography.titleMedium)
                Text(text = "Max Temp: ${forecastday.day.maxtemp_c}°C", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Min Temp: ${forecastday.day.mintemp_c}°C", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Condition: ${forecastday.day.condition.text}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Sunrise: ${forecastday.astro.sunrise}", style = MaterialTheme.typography.bodySmall)
                Text(text = "Sunset: ${forecastday.astro.sunset}", style = MaterialTheme.typography.bodySmall)
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Bike Score: $score%", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                val painter: Painter = rememberImagePainter(data = "https:${forecastday.day.condition.icon}")
                Image(
                    painter = painter,
                    contentDescription = forecastday.day.condition.text,
                    modifier = Modifier.size(50.dp)
                )
            }
        }
    }
}

@Composable
fun formatDate(dateString: String): String {
    val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val formatter = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
    return formatter.format(parser.parse(dateString) ?: Date())
}

@Preview(showBackground = true)
@Composable
fun WeatherScreenPreview() {
    BestBikeDayTheme {
        WeatherScreen()
    }
}