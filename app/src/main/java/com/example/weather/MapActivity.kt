package com.example.weather

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ola.mapsdk.camera.MapControlSettings
import com.ola.mapsdk.interfaces.OlaMapCallback
import com.ola.mapsdk.listeners.OlaMapsListenerManager
import com.ola.mapsdk.model.OlaLatLng
import com.ola.mapsdk.view.OlaMap
import com.ola.mapsdk.view.OlaMapView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapActivity : AppCompatActivity(), OlaMapsListenerManager.OnOlaMapClickedListener {

    private lateinit var olaMapView: OlaMapView
    private lateinit var olaMap: OlaMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_map_view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        olaMapView = findViewById(R.id.olaMapView)

        val mapControlSettings = MapControlSettings.Builder()
            .setRotateGesturesEnabled(true)
            .setScrollGesturesEnabled(true)
            .setZoomGesturesEnabled(true)
            .setCompassEnabled(true)
            .setTiltGesturesEnabled(true)
            .setDoubleTapGesturesEnabled(true)
            .build()

        CoroutineScope(Dispatchers.Main).launch {
            olaMapView.getMap(apiKey = "3tphmPLdXry8WEmiqenfphbq15MpF3UQe2yExXVU",
                olaMapCallback = object : OlaMapCallback {
                    override fun onMapReady(map: OlaMap) {
                        // Map is ready to use
                        olaMap = map
                        olaMap.showCurrentLocation()

                        val currentLocation = olaMap.getCurrentLocation()

                        if (currentLocation != null && !currentLocation.latitude.isNaN() && !currentLocation.longitude.isNaN()) {
                            olaMap.zoomToLocation(currentLocation, 10.0)
                        } else {
                            Log.e("MapActivity", "Invalid current location: $currentLocation")
                            // You can provide a default fallback location if needed
                        }

                        olaMap.setOnMapClickedListener(this@MapActivity) // Set the map click listener
                    }

                    override fun onMapError(error: String) {
                        // Handle map error
                        Log.e("OlaMapError", error)
                    }
                },mapControlSettings
            )
        }
    }

    override fun onOlaMapClicked(olaLatLng: OlaLatLng) {

        CoroutineScope(Dispatchers.IO).launch {
            val latitude = olaLatLng.latitude
            val longitude = olaLatLng.longitude
            Log.d("MapClick", "Clicked coordinates: ($latitude, $longitude)")

            // Fetch and display weather details using these coordinates
            WeatherData().fetchWeatherData(latitude, longitude) { weather ->
                if (weather != null) {
                    val intent = Intent(this@MapActivity, MainActivity::class.java)

                    // Pass the weather data to the intent
                    intent.putExtra("city", weather.name)
                    intent.putExtra("temp", weather.main.temp)
                    intent.putExtra("max_temp", weather.main.temp_max)
                    intent.putExtra("min_temp", weather.main.temp_min)
                    intent.putExtra("humidity", weather.main.humidity)
                    intent.putExtra("pressure", weather.main.pressure)
                    intent.putExtra("icon", weather.weather[0].icon)

                    // Start MainActivity
                    startActivity(intent)
                } else {
                    Log.e("WeatherInfo", "Failed to fetch weather data")
                }
            }
        }
    }
}