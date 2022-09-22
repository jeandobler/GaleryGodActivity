package com.dobler.galerygodactivity.usecase

import com.dobler.galerygodactivity.data.WeatherManager
import com.dobler.galerygodactivity.model.FindResult
import retrofit2.Call

class FindWeatherUseCase {


    fun getWeather(temperatureUnit: String, search: String): Call<FindResult?> {
        return WeatherManager.service.find(search, temperatureUnit, WeatherManager.API_KEY)
    }
}