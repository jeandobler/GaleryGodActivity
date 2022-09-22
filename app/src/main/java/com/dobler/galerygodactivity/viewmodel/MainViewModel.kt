package com.dobler.galerygodactivity.viewmodel

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dobler.galerygodactivity.model.FindResult
import com.dobler.galerygodactivity.usecase.FindWeatherUseCase
import retrofit2.Call
import retrofit2.Response

class MainViewModel : ViewModel() {

    private val _results: MutableLiveData<FindResult> by lazy {
        MutableLiveData<FindResult>().also {
            searchByName("")
        }
    }
    val results: LiveData<FindResult> = _results

    private val useCase = FindWeatherUseCase()

    fun searchByName(search: String) {

        if (TextUtils.isEmpty(search)) {
            return
        }
//        onStartLoading()

        useCase.getWeather(temperatureUnit, search)
            .enqueue(object : retrofit2.Callback<FindResult?> {
                override fun onResponse(
                    call: Call<FindResult?>,
                    response: Response<FindResult?>
                ) {
                    _results.postValue(response.body())

//                onFinishLoading(response.body())
                }

                override fun onFailure(call: Call<FindResult?>, t: Throwable) {
//                onFinishLoadingWithError()
                }
            })
    }

    var temperatureUnit: String = "metric"


    fun updateUnitIfNecessary(newUnits: String) {
        val currentUnits = temperatureUnit
        if (currentUnits != newUnits) {
            temperatureUnit = newUnits
        }
    }


}