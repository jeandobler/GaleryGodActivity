package com.dobler.galerygodactivity

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.telecom.Call
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnKeyListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.dobler.galerygodactivity.WeatherManager.FindResult
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Response
import javax.security.auth.callback.Callback
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    private var mSharedPref: SharedPreferences? = null
    private var mAdapter: FindItemAdapter? = null
    private val cities: ArrayList<WeatherManager.City> = ArrayList()

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


    private fun searchByName() {

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

    var temperatureUnit: String = "metric"

    inner class FindItemAdapter(context: Context, cities: ArrayList<WeatherManager.City>) :
        ArrayAdapter<WeatherManager.City>(context, 0, cities) {


        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            val city = getItem(position)
            if (convertView == null) {
                convertView = LayoutInflater.from(context)
                    .inflate(R.layout.city_list_item, parent, false)

                val cityName: TextView = convertView.findViewById(R.id.cityNameTxt)
                cityName.setText(city?.title)
                val description: TextView = convertView.findViewById(R.id.descriptionTxt)
                description.setText(city?.description)
                val metric: TextView = convertView.findViewById(R.id.metricTxt)
                val units = temperatureUnit
                metric.text = "ºC"
                val temp: TextView = convertView.findViewById(R.id.tempTxt)
                temp.setText(city?.temperature)
                val wind: TextView = convertView.findViewById(R.id.windTxt)
                wind.setText(city?.wind.toString() + " m/s")
                val clouds: TextView = convertView.findViewById(R.id.cloudsTxt)
                clouds.setText(city?.getClouds())
                val pressure: TextView = convertView.findViewById(R.id.pressureTxt)
                pressure.setText(city?.pressure)
                val resId = context.resources.getIdentifier(
                    "w_" + city?.weather?.get(0)?.icon,
                    "drawable",
                    context.packageName
                )
                val icon: ImageView = convertView.findViewById(R.id.weatherIcon)
                icon.setImageResource(resId)
                return convertView

            }
            return parent
        }
    }

    companion object {
        private const val PREFERENCE_NAME = "com.rperazzo.weatherapp.shared"
        private const val TEMPERATURE_UNIT_KEY = "TEMPERATURE_UNIT_KEY"
    }
}
