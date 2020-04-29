package com.homurax.sunnyweather.logic.model

import com.google.gson.annotations.SerializedName

class Location(val lng: String, val lat: String)

class Place(val name: String, val location: Location,
            @SerializedName("formatted_address") val address: String)

class PlaceResponse(val status: String, val places: List<Place>)