package fi.solita.hnybom.trebusses.busses

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import fi.solita.hnybom.trebusses.util.ResponseUtil
import org.slf4j.LoggerFactory

// http://api.publictransport.tampere.fi/prod/?request=stop&code=3589&format=json&user=hnybom&pass=tiuxiBNAG3ek58i5UQ

data class BusStopSchedule(val code: String,
                           val code_short: String,
                           val name_fi: String,
                           val name_sv: String,
                           val city_fi: String,
                           val city_sv: String,
                           val coords: String,
                           val wgs_coords: String,
                           val accessibility: String,
                           val departures:Array<Schedule>) {

    object TreBusStopScheduleFinder {

        fun findBusLines(busStopCode: String): Array<BusStopSchedule>? {

            val uri = "http://api.publictransport.tampere.fi/prod/?request=stop&code=" +
                    busStopCode +
                    "&format=json&user=hnybom&pass=tiuxiBNAG3ek58i5UQ"

            val (_, _, result) = uri.httpGet().responseObject(ResponseUtil.GeneralDesirializer(Array<BusStopSchedule>::class.java))

            return ResponseUtil.getResultValue(result)

        }

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BusStopSchedule

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }


}

data class Schedule(val code: String,
                    val direction: String,
                    val name1: String,
                    val time: String,
                    val date: Int) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BusStopSchedule

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}