package com.dobler.galerygodactivity.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.dobler.galerygodactivity.R
import com.dobler.galerygodactivity.model.City

class FindItemAdapter(context: Context, cities: ArrayList<City>) :
    ArrayAdapter<City>(context, 0, cities) {


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