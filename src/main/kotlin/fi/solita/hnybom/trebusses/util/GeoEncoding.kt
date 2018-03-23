package fi.solita.hnybom.trebusses.util

import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import com.google.maps.model.LatLng


// https://maps.google.com/maps/api/geocode/json?address=Hallilantie%2065%20Tampere&key=AIzaSyCBivQrig0NVIRpURYCUhNO0s9hnskm3bs


object GeoEncoding {

    fun findLocation (address: String) : LatLng{
        val context = GeoApiContext.Builder()
                .apiKey("AIzaSyCBivQrig0NVIRpURYCUhNO0s9hnskm3bs")
                .build()
        val results = GeocodingApi.geocode(context, address).await()
        return results[0].geometry.location
    }

}