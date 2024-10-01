package com.example.weather

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.mapbox.mapboxsdk.maps.MapView
import kotlinx.coroutines.*
import java.time.LocalDate
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val day=findViewById<TextView>(R.id.day)
        day.text=LocalDate.now().dayOfWeek.toString()

        val date=findViewById<TextView>(R.id.date)
        date.text=LocalDate.now().toString()

        // Check if there is intent data by checking extras
        val intent = intent

        intent?.extras?.let {
            val city = it.getString("city")
            val temp = it.getDouble("temp", 0.0)
            val maxTemp = it.getDouble("max_temp", 0.0)
            val minTemp = it.getDouble("min_temp", 0.0)
            val humidity = it.getInt("humidity", 0)
            val pressure = it.getInt("pressure", 0)
            val icon = it.getString("icon")

            // Call updateUI method to display the data
            if (city != null && icon != null) {
                updateUI(city, temp, maxTemp, minTemp, humidity, pressure, icon)
            }
        } ?: run {
            // Intent data is null, proceed to fetch current location
            Log.d("MainActivity", "No intent data, fetching current location")
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            getCurrentLocation()
        }

    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        val currentLocationRequest = CurrentLocationRequest.Builder()
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setMaxUpdateAgeMillis(2 * 60 * 1000)   // 2 minutes
            .build()

        fusedLocationClient.getCurrentLocation(currentLocationRequest, null).addOnSuccessListener { location: Location? ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                Log.d("getCurrentLocation", "Latitude: $latitude & Longitude: $longitude")

                CoroutineScope(Dispatchers.Main).launch {
                    WeatherData().fetchWeatherData(latitude, longitude) { weather ->
                        if(weather != null) {
                            Log.d("Weather Info", weather.toString())
                            updateUI(weather.name,weather.main.temp,weather.main.temp_max,weather.main.temp_min,weather.main.humidity,weather.main.pressure,weather.weather[0].icon)
                        }
                        else {
                            Log.e("WeatherInfo", "Failed to fetch weather data")
                        }
                    }
                }
            }
            else {
                Log.d("getCurrentLocation", "Location is null")
            }
        }
    }

    // Method to update weather data
    fun updateUI(
        name:String,
        temp: Double,
        max_temp:Double,
        min_temp:Double,
        humidity: Int,
        pressure: Int,
        icon: String
    ) {
        findViewById<TextView>(R.id.city).text = name
        findViewById<TextView>(R.id.temperature).text = "$temp °C"
        findViewById<TextView>(R.id.maxTemp).text = "$max_temp °C"
        findViewById<TextView>(R.id.minTemp).text = "$min_temp °C"
        findViewById<TextView>(R.id.humidity).text = "$humidity%"
        findViewById<TextView>(R.id.pressure).text = "$pressure hPa"

        Glide.with(this).load("https://openweathermap.org/img/wn/$icon@2x.png").into(findViewById(R.id.weatherIcon))

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getCurrentLocation()
            } else {
                Log.d("getCurrentLocation", "Permission denied")
            }
        }
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        // Create an AlertDialog builder
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Exit")
        builder.setMessage("Are you sure you want to exit?")

        // Set positive button (Yes)
        builder.setPositiveButton("Yes") { dialog, _ ->
            dialog.dismiss()
            super.onBackPressed() // Exit the activity
            finishAffinity() // This will close all activities and exit the app
            exitProcess(0)
        }

        // Set negative button (No)
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss() // Dismiss the dialog
        }
        // Show the dialog
        builder.create().show()
    }


    fun mapsButtonClicked(view: View) {
        startActivity(Intent(this, MapActivity::class.java))
    }

}
