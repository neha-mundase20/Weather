package com.example.weather

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.HttpUrl
import okio.IOException

class AccessTokenInterceptor : Interceptor {
    private val API_KEY_PARAM = "api_key"
    private val API_KEY_VALUE = "3tphmPLdXry8WEmiqenfphbq15MpF3UQe2yExXVU"

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()

        // Modify the URL to include the API key as a query parameter
        val urlWithApiKey: HttpUrl = originalRequest.url.newBuilder()
            .addQueryParameter(API_KEY_PARAM, API_KEY_VALUE)
            .build()

        // Create a new request with the modified URL
        val newRequest: Request = originalRequest.newBuilder()
            .url(urlWithApiKey)
            .build()

        // Log the request URL for debugging
        Log.d("AccessTokenInterceptor", "Request URL: ${newRequest.url}")

        // Proceed with the modified request
        val response = try {
            chain.proceed(newRequest)
        } catch (e: IOException) {
            Log.e("AccessTokenInterceptor", "Request failed: ${e.message}")
            throw e
        }

        // Log the response code for debugging
        Log.d("AccessTokenInterceptor", "Response code: ${response.code}")

        return response
    }
}