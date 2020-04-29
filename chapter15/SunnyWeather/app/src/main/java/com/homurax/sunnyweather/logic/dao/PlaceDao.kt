package com.homurax.sunnyweather.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.homurax.sunnyweather.SunnyWeatherApplication
import com.homurax.sunnyweather.logic.model.Place


object PlaceDao {

    private fun sharedPreferences() = SunnyWeatherApplication.context.getSharedPreferences("sunny_weather", Context.MODE_PRIVATE)

    fun savePlace(place: Place) {
        sharedPreferences().edit {
            putString("place", Gson().toJson(place))
        }
    }

    fun getSavedPlace(): Place {
        val placeJson = sharedPreferences().getString("place", "")
        return Gson().fromJson(placeJson, Place::class.java)
    }

    fun isPlaceSaved() = sharedPreferences().contains("place")

}