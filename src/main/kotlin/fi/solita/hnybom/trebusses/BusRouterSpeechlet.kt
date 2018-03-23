package fi.solita.hnybom.trebusses

import com.amazon.speech.json.SpeechletRequestEnvelope
import com.amazon.speech.speechlet.*
import fi.solita.hnybom.trebusses.util.ResponseUtil
import fi.solita.hnybom.trebusses.util.ResponseUtil.getAskResponse
import com.amazon.speech.speechlet.interfaces.system.SystemState
import com.amazon.speech.speechlet.interfaces.system.SystemInterface
import com.google.gson.Gson
import fi.solita.hnybom.trebusses.busses.BusLine
import fi.solita.hnybom.trebusses.busses.BusStop
import fi.solita.hnybom.trebusses.busses.BusStopSchedule
import fi.solita.hnybom.trebusses.device.address.Address
import fi.solita.hnybom.trebusses.util.GeoEncoding
import org.apache.log4j.LogManager


class BusRouterSpeechlet : SpeechletV2 {

    private val log = LogManager.getLogger(BusRouterSpeechlet::class.java)

    override fun onSessionStarted(p0: SpeechletRequestEnvelope<SessionStartedRequest>?) {
        System.out.println("onSessionStarted")

    }

    override fun onSessionEnded(p0: SpeechletRequestEnvelope<SessionEndedRequest>?) {
        System.out.println("onSessionEnded")
    }

    override fun onIntent(requestEnvelope: SpeechletRequestEnvelope<IntentRequest>?): SpeechletResponse {
        System.out.println("Start")

        if(requestEnvelope == null) return getHelpResponse()

        if(requestEnvelope.request == null) return getHelpResponse()

        val request = requestEnvelope.request
        log.info("onIntent requestId=" + request.getRequestId() + ", sessionId=" + requestEnvelope.getSession().getSessionId())

        System.out.println(Gson().toJson(request.intent))

        val intent = request.intent
        val intentName = if (intent != null) intent.name else null

        return if ("tre_busses" == intentName) {
            getBusResponse(requestEnvelope)
        } else if ("AMAZON.HelpIntent" == intentName) {
            getHelpResponse()
        } else {
            getAskResponse("Bus schedules", "This is unsupported.  Please try something else.")
        }
    }

    override fun onLaunch(p0: SpeechletRequestEnvelope<LaunchRequest>?): SpeechletResponse {
        return getHelpResponse()
    }

    private fun getHelpResponse(): SpeechletResponse {
        return ResponseUtil.getAskResponse("Bus schedules", "You can ask bus time tables")
    }

    private fun getBusResponse(requestEnvelope: SpeechletRequestEnvelope<IntentRequest>?): SpeechletResponse {
        if (requestEnvelope == null) return getHelpResponse()

        val (apiAccessToken, deviceId, apiEndpoint) = getDeviceInfo(requestEnvelope)

        //val findAlexaDeviceAddress = Address.AlexaAddressFinder.findAlexaDeviceAddress(apiAccessToken, deviceId, apiEndpoint)
        val findAlexaDeviceAddress = Address(city = "Tampere", addressLine1 = "Hallilantie 65 A 14")
        if (findAlexaDeviceAddress == null
                || findAlexaDeviceAddress.addressLine1.isBlank()
                || findAlexaDeviceAddress.city.isBlank()) {
            return getAskResponse("Bus schedules", "Couldn't locate you sorry!")
        }

        val latlong = GeoEncoding.findLocation(findAlexaDeviceAddress.addressLine1 + ", " + findAlexaDeviceAddress.city)
        val busStops = BusStop.TreBusStopFinder.findBusStops(latlong)

        if (busStops == null || busStops.isEmpty()) {
            return ResponseUtil.getAskResponse("Bus schedules", "No bus stops found near you!")
        }

        val bussNumber = requestEnvelope.request.intent.slots["buss_number"]

        if (bussNumber == null || bussNumber.value == null) {
            return ResponseUtil.getAskResponse("Bus schedules", "No bus number provided")
        }

        val busLineNumber = bussNumber.value

        val busLines = BusLine.TreBusLineFinder.findBusLines(busLineNumber)

        if (busLines == null || busLines.isEmpty()) {
            return ResponseUtil.getAskResponse("Bus schedules", "No bus stops found for " +
                    busLineNumber + " buss line")
        }

        val filteredStops = busLines.map {
            val lineStops = it.line_stops.filter { sit -> busStops.filter { it.code == sit.code }.isNotEmpty() }
            Pair(it.line_end, lineStops)
        }

        if (filteredStops == null || filteredStops.isEmpty()) {
            return ResponseUtil.getAskResponse("Bus schedules", "No bus stops found near you for " +
                    busLineNumber + " buss line")
        }

        val busLinesScheduleContainers = filteredStops.map {
            val map = it.second.map {
                BusStopSchedule.TreBusStopScheduleFinder.findBusLines(it.code)?.toList() ?: emptyList()
            }.flatten()
            Pair(it.first, map)
        }

        if(busLinesScheduleContainers == null || busLinesScheduleContainers.isEmpty()) {
            return ResponseUtil.getAskResponse("Bus schedules", "No schedule for the selected bus stop found for: " +
                    busLineNumber + " buss line")
        }

        val busLinesSchedules = busLinesScheduleContainers.map {
            it.second.map { it.departures.toList() }.flatten()
        }.flatten()

        if(busLinesSchedules.isEmpty()) {
            return ResponseUtil.getAskResponse("Bus schedules", "No busses leaving at the moment sorry")
        }

        val sb = StringBuilder()

        busLinesScheduleContainers.forEach( {

            sb.append("Next number $busLineNumber busses to the direction of ${it.first} are leaving from ")

            it.second.forEachIndexed { index, busStopSchedule ->
                sb.append("${busStopSchedule.name_fi} at times: ")

                var i = 0

                while(i < busStopSchedule.departures.size && i < 3) {
                    sb.append("${busStopSchedule.departures[i].time} ")
                    i++
                }

                if(index < it.second.size - 1) sb.append(" and from ")
            }
        })


        return ResponseUtil.getAskResponse("Bus schedules", sb.toString())
    }

    private fun getDeviceInfo(requestEnvelope: SpeechletRequestEnvelope<IntentRequest>): Triple<String, String, String> {
        val systemState = getSystemState(requestEnvelope.context)
        val apiAccessToken = systemState.apiAccessToken
        val deviceId = systemState.device.deviceId
        val apiEndpoint = systemState.apiEndpoint
        return Triple(apiAccessToken, deviceId, apiEndpoint)
    }

    private fun getSystemState(context: Context): SystemState {
        return context.getState(SystemInterface::class.java, SystemState::class.java)
    }


}
