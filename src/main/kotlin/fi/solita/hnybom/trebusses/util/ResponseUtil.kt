package fi.solita.hnybom.trebusses.util

import com.amazon.speech.speechlet.SpeechletResponse
import com.amazon.speech.ui.OutputSpeech
import com.amazon.speech.ui.PlainTextOutputSpeech
import com.amazon.speech.ui.Reprompt
import com.amazon.speech.ui.SimpleCard
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import fi.solita.hnybom.trebusses.busses.BusStop
import org.apache.log4j.LogManager

object ResponseUtil {

    private val log = LogManager.getLogger(ResponseUtil::class.java)

    class GeneralDesirializer<T : Any> constructor(val clazz: Class<T> ) : ResponseDeserializable<T> {
        override fun deserialize(content: String): T? = Gson().fromJson(content, clazz)
    }

    fun getSimpleCard(title: String, content: String): SimpleCard {
        val card = SimpleCard()
        card.title = title
        card.content = content

        return card
    }

    fun getAskResponse(cardTitle: String, speechText: String): SpeechletResponse {
        val card = getSimpleCard(cardTitle, speechText)
        val speech = getPlainTextOutputSpeech(speechText)
        val reprompt = getReprompt(speech)

        return SpeechletResponse.newAskResponse(speech, reprompt, card)
    }

    fun getReprompt(outputSpeech: OutputSpeech): Reprompt {
        val reprompt = Reprompt()
        reprompt.outputSpeech = outputSpeech

        return reprompt
    }

    fun getPlainTextOutputSpeech(speechText: String): PlainTextOutputSpeech {
        val speech = PlainTextOutputSpeech()
        speech.text = speechText

        return speech
    }

    fun <T : Any> getResultValue(result: Result<T, FuelError>): T? {
        if (result.component2() != null) {
            log.error(result.component2().toString())
            return null
        }

        return result.component1()
    }

}