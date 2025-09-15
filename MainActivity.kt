package com.example.Weather

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException

class MainActivity : AppCompatActivity() {

    private lateinit var editTextCity: EditText
    private lateinit var buttonGetWeather: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var textViewWeatherInfo: TextView
    private lateinit var textViewError: TextView
    private lateinit var cardWeather: CardView
    private lateinit var imageViewWeatherIcon: ImageView

    private val requestQueue by lazy {
        Volley.newRequestQueue(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        editTextCity = findViewById(R.id.editTextCity)
        buttonGetWeather = findViewById(R.id.buttonGetWeather)
        progressBar = findViewById(R.id.progressBar)
        textViewWeatherInfo = findViewById(R.id.textViewWeatherInfo)
        textViewError = findViewById(R.id.textViewError)
        cardWeather = findViewById(R.id.cardWeather)
        imageViewWeatherIcon = findViewById(R.id.imageViewWeatherIcon)

        // Button click listener
        buttonGetWeather.setOnClickListener {
            val city = editTextCity.text.toString().trim()
            if (city.isNotEmpty()) {
                fetchWeather(city)
            } else {
                Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchWeather(city: String) {
        // Show loading
        progressBar.visibility = View.VISIBLE
        cardWeather.visibility = View.GONE
        textViewError.visibility = View.GONE

        // Simple approach: hardcode API key
        val apiKey = "5cd0edc9a497bc46a2e016c084686176"  // Replace with your OpenWeatherMap API key
        val url = "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey&units=metric"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                progressBar.visibility = View.GONE
                try {
                    val main = response.getJSONObject("main")
                    val temperature = main.getDouble("temp")
                    val weatherArray = response.getJSONArray("weather")
                    val description = weatherArray.getJSONObject(0).getString("description")
                    val icon = weatherArray.getJSONObject(0).getString("icon")

                    // Update UI
                    val weatherInfo = "City: $city\nTemperature: $temperature°C\nCondition: $description"
                    textViewWeatherInfo.text = weatherInfo
                    cardWeather.visibility = View.VISIBLE

                    // Optionally load weather icon from OpenWeatherMap
                    // For demo, we can use placeholder from drawable
                    imageViewWeatherIcon.setImageResource(android.R.drawable.ic_menu_compass)

                } catch (e: JSONException) {
                    showError("Error parsing weather data.")
                }
            },
            { error ->
                progressBar.visibility = View.GONE
                val networkResponse = error.networkResponse
                if (networkResponse != null && networkResponse.statusCode == 404) {
                    showError("City not found. Please check the name.")
                } else {
                    showError("Failed to fetch weather. Check your connection.")
                }
            }
        )

        requestQueue.add(jsonObjectRequest)
    }

    private fun showError(message: String) {
        textViewError.text = message
        textViewError.visibility = View.VISIBLE
        cardWeather.visibility = View.GONE
    }
}
