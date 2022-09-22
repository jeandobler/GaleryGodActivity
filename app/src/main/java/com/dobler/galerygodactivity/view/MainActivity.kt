package com.dobler.galerygodactivity.view

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.dobler.galerygodactivity.R
import com.dobler.galerygodactivity.data.WeatherManager
import com.dobler.galerygodactivity.data.WeatherManager.FindResult
import com.dobler.galerygodactivity.model.City
import com.dobler.galerygodactivity.model.FindResult
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    private var mSharedPref: SharedPreferences? = null
    private var mAdapter: FindItemAdapter? = null
    private val cities: ArrayList<City> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mSharedPref = getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        mAdapter = FindItemAdapter(this, cities)
        newList.adapter = mAdapter


        editText.setOnKeyListener { v, keyCode, event ->

            if (event.getAction() === KeyEvent.ACTION_DOWN &&
                keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                searchByName()
            }
            false
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.getItemId()
        if (id == R.id.menu_celcius) {
            updateUnitIfNecessary("metric")
            return true
        } else if (id == R.id.menu_fahrenheit) {
            updateUnitIfNecessary("imperial")
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateUnitIfNecessary(newUnits: String) {
        val currentUnits = temperatureUnit
        if (currentUnits != newUnits) {
            temperatureUnit = newUnits
            searchByName()
        }
    }

    fun onSearchClick(view: View?) {
        searchByName()
    }

    private fun onStartLoading() {
        newList.setVisibility(View.GONE)
        progressBar!!.visibility = View.VISIBLE
        textView!!.visibility = View.GONE
        val view: View? = this.currentFocus
        if (view != null) {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
        }
    }

    private fun onFinishLoading(result: FindResult?) {
        progressBar!!.visibility = View.GONE
        cities.clear()
        result?.list?.let {
            if (it.size > 0) {
                cities.addAll(it)
                newList.setVisibility(View.VISIBLE)
                mAdapter!!.notifyDataSetChanged()
            } else {
                textView!!.text = "No results."
            }
        }
    }

    private fun onFinishLoadingWithError() {
        progressBar!!.visibility = View.GONE
        newList.setVisibility(View.GONE)
        textView!!.text = "Error"
    }

    val isDeviceConnected: Boolean
        get() {
            val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            return netInfo != null && netInfo.isConnected
        }

    private fun searchByName() {
        if (!isDeviceConnected) {
            Toast.makeText(this, "No connection!", Toast.LENGTH_LONG).show()
            return
        }
        val search = editText!!.text.toString()
        if (TextUtils.isEmpty(search)) {
            return
        }
        onStartLoading()
        val wService: WeatherManager.WeatherService = WeatherManager.service
        val units = temperatureUnit
        val findCall = wService.find(search, units, WeatherManager.API_KEY)
        findCall.enqueue(object : retrofit2.Callback<FindResult?> {
            override fun onResponse(
                call: retrofit2.Call<FindResult?>,
                response: retrofit2.Response<FindResult?>
            ) {
                onFinishLoading(response.body())
            }

            override fun onFailure(call: retrofit2.Call<FindResult?>, t: Throwable) {
                onFinishLoadingWithError()
            }
        })
    }

    var temperatureUnit: String?
        get() = mSharedPref!!.getString(TEMPERATURE_UNIT_KEY, "metric")
        set(value) {
            val editor = mSharedPref!!.edit()
            editor.putString(TEMPERATURE_UNIT_KEY, value)
            editor.apply()
        }


    companion object {
        private const val PREFERENCE_NAME = "com.rperazzo.weatherapp.shared"
        private const val TEMPERATURE_UNIT_KEY = "TEMPERATURE_UNIT_KEY"
    }
}
