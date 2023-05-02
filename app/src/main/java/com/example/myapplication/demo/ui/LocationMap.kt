package com.example.myapplication.demo.ui

import com.google.android.gms.maps.model.LatLng
import java.util.*

class LocationMap {
    private val locations: MutableMap<String, MutableList<LatLng>> = HashMap()

    fun addLocation(locationName: String, latitude: Double, longitude: Double) {
        val locationKey = getLocationKey(latitude, longitude)
        val location = LatLng(latitude, longitude)
        if (!locations.containsKey(locationKey)) {
            locations[locationKey] = ArrayList()
        }
        locations[locationKey]?.add(location)
    }

    fun getLocations(): Map<String, List<LatLng>> {
        return locations
    }

    private fun getLocationKey(latitude: Double, longitude: Double): String {
        // Use a simple string concatenation of the latitude and longitude as the key
        return "$latitude,$longitude"
    }
}
