package com.dobler.galerygodactivity.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dobler.galerygodactivity.R
import com.dobler.galerygodactivity.adapter.FindItemAdapter
import com.dobler.galerygodactivity.model.FindResult
import com.dobler.galerygodactivity.viewmodel.MainViewModel
import com.dobler.galerygodactivity.viewmodel.MainViewModelResults
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private var mSharedPref: SharedPreferences? = null
    private var mAdapter: FindItemAdapter? = null

    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mSharedPref = getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

        initObservers()
    }

    private fun initObservers() {
        button.setOnClickListener {
            mainViewModel.searchByName(editText.text.toString(), temperatureUnit)
        }

        mainViewModel.weather.observe(this) {
            when(it){
                is MainViewModelResults.success -> {
                    mAdapter = FindItemAdapter(this, it.body.list)
                    newList.adapter = mAdapter
                }
            }
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
        }
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
//        cities.clear()
        result?.list?.let {
            if (it.size > 0) {
//                cities.addAll(it)
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


    var temperatureUnit: String = "metric"


    companion object {
        private const val PREFERENCE_NAME = "com.rperazzo.weatherapp.shared"
        private const val TEMPERATURE_UNIT_KEY = "TEMPERATURE_UNIT_KEY"
    }
}
