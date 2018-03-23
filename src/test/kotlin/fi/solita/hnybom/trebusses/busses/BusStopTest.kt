package fi.solita.hnybom.trebusses.busses

import com.google.maps.model.LatLng
import io.kotlintest.matchers.shouldEqual
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.specs.BehaviorSpec

class BusStopTest : BehaviorSpec() {

    init {
        given("These coordinates") {
            val latlng = LatLng(61.4773759, 23.8268957)
            `when`("find buss stops") {
                val busStops = BusStop.TreBusStopFinder.findBusStops(latlng)
                then("shloud return these buss stops") {
                    busStops shouldNotBe null
                    busStops!!.size shouldEqual 20
                }
            }
        }
    }

}