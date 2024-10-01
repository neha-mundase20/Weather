package com.example.weather
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

    object RetrofitClient {
        private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

        val instance: ApiInterface by lazy {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            retrofit.create(ApiInterface::class.java)
        }
    }