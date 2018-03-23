package fi.solita.hnybom.trebusses.device.address

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpGet
import com.google.gson.Gson
import fi.solita.hnybom.trebusses.util.ResponseUtil
import org.apache.log4j.LogManager
import org.slf4j.LoggerFactory

data class Address(val stateOrRegion : String = "",
                   val city: String,
                   val countryCode: String = "",
                   val postalCode: String = "",
                   val addressLine1: String,
                   val addressLine2: String = "",
                   val addressLine3: String = "",
                   val districtOrCounty: String = ""
){
    object AlexaAddressFinder {

        private val log = LogManager.getLogger(AlexaAddressFinder::class.java)

        fun findAlexaDeviceAddress(apiAccessToken: String, deviceId: String, apiEndpoint: String): Address? {

            val uri = apiEndpoint + "/v1/devices/" + deviceId + "/settings/address"

            val (_, _, result) = uri.httpGet().header(Pair("Authorization: Bearer ", apiAccessToken))
                    .responseObject(ResponseUtil.GeneralDesirializer(Address::class.java))

            if(result.component2() != null) {
                log.error(result.component2().toString())
            }

            return result.component1()

        }

    }
}