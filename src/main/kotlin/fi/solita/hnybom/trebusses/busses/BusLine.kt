package fi.solita.hnybom.trebusses.busses

import com.github.kittinunf.fuel.httpGet
import fi.solita.hnybom.trebusses.util.ResponseUtil


// http://api.publictransport.tampere.fi/prod/?request=lines&query=10&user=hnybom&pass=tiuxiBNAG3ek58i5UQ
data class BusLine(val code: String,
                    val code_short: String,
                    val transport_type_id: Int,
                    val line_start: String,
                    val line_end: String,
                    val name: String,
                    val timetable_url: String,
                    val line_shape: String,
                    val line_stops: Array<LineStop>) {

    object TreBusLineFinder {

        fun findBusLines(busLineNumber: String): Array<BusLine>? {

            val uri = "http://api.publictransport.tampere.fi/prod/?request=lines&query=" +
                    busLineNumber +
                    "&format=json&user=hnybom&pass=tiuxiBNAG3ek58i5UQ"

            val (_, _, result) = uri.httpGet().responseObject(ResponseUtil.GeneralDesirializer(Array<BusLine>::class.java))

            return ResponseUtil.getResultValue(result)

        }

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BusLine

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }

}

data class LineStop(
        val code: String,
        val codeShort: String,
        val time: Int,
        val address: String,
        val name: String,
        val coords: String,
        val city_name: String) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BusLine

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}

