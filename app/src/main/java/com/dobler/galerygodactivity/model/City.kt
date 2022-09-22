package com.dobler.galerygodactivity.model

import java.io.Serializable
import java.text.DecimalFormat
import java.util.*
import java.util.List

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

class FindResult(val list: List<City>)