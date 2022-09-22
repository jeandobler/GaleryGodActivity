package com.dobler.galerygodactivity.usecase

import com.dobler.galerygodactivity.data.WeatherManager
import com.dobler.galerygodactivity.model.FindResult
import retrofit2.Call

class FindWeatherByCityUseCase {


    fun findWeatherByCity(search: String, temperatureUnit: String): Call<FindResult?> {
        val wService: WeatherManager.WeatherService = WeatherManager.service
        val units = temperatureUnit
        return wService.find(search, units, WeatherManager.API_KEY)
    }
}