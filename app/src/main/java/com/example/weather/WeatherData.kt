package com.example.weather

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherData {

    private val API_KEY = "40b65d66e010c7cb9423be5422a1dfbd"

    fun fetchWeatherData(lat: Double, lon: Double, callback: (WeatherResponse?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                RetrofitClient.instance.getWeather(lat, lon, API_KEY,"metric").enqueue(object :
                    Callback<WeatherResponse> {
                    override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                        if (response.isSuccessful) {
                            response.body()?.let { weatherResponse ->
                                Log.d("WeatherInfo", "City: ${weatherResponse.name}, Max Temp: ${weatherResponse.main.temp_max}, Min Temp: ${weatherResponse.main.temp_min}, Humidity: ${weatherResponse.main.humidity}, Pressure: ${weatherResponse.main.pressure}, Icon: ${weatherResponse.weather[0].icon}")
                                callback(weatherResponse)  // Pass the weather response to the callback
                            } ?: callback(null)  // Handle the case where response body is null
                        } else {
                            Log.e("WeatherInfo", "Error: ${response.code()}")
                            callback(null)  // Handle unsuccessful response
                        }
                    }

                    override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                        Log.e("WeatherInfo", "Failure: ${t.message}")
                        callback(null)  // Handle failure
                    }
                })
            } catch (e: Exception) {
                Log.e("WeatherData", "Error: ${e.message}")
                callback(null)  // Handle exception
            }
        }
    }
}
