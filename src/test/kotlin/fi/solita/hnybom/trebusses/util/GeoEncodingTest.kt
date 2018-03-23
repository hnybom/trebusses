package fi.solita.hnybom.trebusses.util

import io.kotlintest.matchers.shouldEqual
import io.kotlintest.specs.BehaviorSpec


class GeoEncodingTest : BehaviorSpec() {

    init {
        given("Geo encoding") {
            `when`("find coordinates for address") {
                val findLocation = GeoEncoding.findLocation("Hallilantie 65, Tampere")
                then("should return these coordinates", {

                    findLocation.lat shouldEqual 61.4773759
                    findLocation.lng shouldEqual 23.8268957
                })
            }
        }
    }
}