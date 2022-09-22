package com.dobler.galerygodactivity.data

import com.dobler.galerygodactivity.model.FindResult
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.Serializable
import java.text.DecimalFormat
import java.util.*

object WeatherManager {
    private const val API_URL = "http://api.openweathermap.org/data/2.5/"
    const val API_KEY = "a14349535dcd0932c4e087e86631e180"

    val service: WeatherService
        get() {

            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

            val retrofit = Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(WeatherService::class.java)
        }

    interface WeatherService {
        @GET("find")
        fun find(
            @Query("q") cityName: String?,
            @Query("units") units: String?,
            @Query("appid") apiKey: String?
        ): Call<FindResult?>
    }

}