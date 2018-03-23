package fi.solita.hnybom.trebusses.busses

import com.github.kittinunf.fuel.httpGet
import com.google.maps.model.LatLng
import fi.solita.hnybom.trebusses.util.ResponseUtil

// http://api.publictransport.tampere.fi/prod/?request=stops_area&center_coordinate=3331810,6823852&diameter=500&format=json&user=hnybom&pass=tiuxiBNAG3ek58i5UQ


// http://api.publictransport.tampere.fi/prod/?request=stops_area&center_coordinate=23.8268957,61.4773759&diameter=1000&format=json&user=hnybom&pass=tiuxiBNAG3ek58i5UQ&epsg_in=4326&epsg_out=4326

data class BusStop(val code: String,
                   val name: String,
                   val city: String,
                   val coords: String,
                   val dist: Int,
                   val codeShort: String,
                   val address: String
){

    object TreBusStopFinder {

        fun findBusStops(latLng: LatLng): Array<BusStop>? {

            val uri = "http://api.publictransport.tampere.fi/prod/?request=stops_area&center_coordinate=" +
                    latLng.lng + "," + latLng.lat +
                    "&diameter=1000&format=json&user=hnybom&pass=tiuxiBNAG3ek58i5UQ&epsg_in=4326&epsg_out=4326"

            val (_, _, result) = uri.httpGet().responseObject(ResponseUtil.GeneralDesirializer(Array<BusStop>::class.java))

            return ResponseUtil.getResultValue(result)

        }

    }
}