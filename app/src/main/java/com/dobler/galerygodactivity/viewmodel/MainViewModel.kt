package com.dobler.galerygodactivity.viewmodel

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dobler.galerygodactivity.data.WeatherManager
import com.dobler.galerygodactivity.model.FindResult
import com.dobler.galerygodactivity.usecase.FindWeatherByCityUseCase
import kotlinx.android.synthetic.main.activity_main.*

class MainViewModel : ViewModel() {

    private val _weather: MutableLiveData<MainViewModelResults> by lazy {
        MutableLiveData<MainViewModelResults>().also {
            searchByName("", "")
        }
    }
    val weather: LiveData<MainViewModelResults> = _weather
//

    private val useCase = FindWeatherByCityUseCase()

    fun searchByName(search: String, temperatureUnit: String) {

        if (!TextUtils.isEmpty(search)) {
            useCase.findWeatherByCity(search, temperatureUnit)
                .enqueue(object : retrofit2.Callback<FindResult?> {
                    override fun onResponse(
                        call: retrofit2.Call<FindResult?>,
                        response: retrofit2.Response<FindResult?>
                    ) {
                        response.body()?.let {
                            _weather.postValue(MainViewModelResults.success(it))
                        }

//                onFinishLoading(response.body())
                    }

                    override fun onFailure(call: retrofit2.Call<FindResult?>, t: Throwable) {
//                onFinishLoadingWithError()
                    }
                })
        }
//        onStartLoading()


    }


}