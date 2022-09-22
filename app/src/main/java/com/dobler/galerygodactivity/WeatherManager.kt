package com.dobler.galerygodactivity

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

     class FindResult(val list: List<City>)
     class City : Serializable {
        var id: Int? = null
        var name: String? = null
        var sys: Sys? = null
        var main: Main? = null
        var wind: Wind? = null
        var clouds: Clouds? = null
        var weather: List<Weather>? = null
        val title: String
            get() = name + ", " + sys!!.country!!.uppercase(Locale.getDefault())
        val pressure: String
            get() = DecimalFormat("#").format(main!!.pressure) + " hpa"

        fun getWind(): String {
            return "wind " + DecimalFormat("#.#").format(wind!!.speed)
        }

        fun getClouds(): String {
            return "clouds " + clouds!!.all + "%"
        }

        val temperature: String
            get() = DecimalFormat("#").format(main!!.temp)
        val description: String?
            get() = weather!![0].description

        inner class Sys : Serializable {
            var country: String? = null
        }

        inner class Main : Serializable {
            var temp = 0.0
            var pressure = 0.0
        }

        inner class Wind : Serializable {
            var speed = 0.0
        }

        inner class Clouds : Serializable {
            var all = 0
        }

        inner class Weather : Serializable {
            var description: String? = null
            var icon: String? = null
        }
    }
}