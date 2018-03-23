package fi.solita.hnybom.trebusses;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

import java.util.HashSet;
import java.util.Set;

public class BusRouterRequestStreamHandler extends SpeechletRequestStreamHandler {

    private static final Set<String> supportedApplicationIds;

    static {
        /*
         * This Id can be found on https://developer.amazon.com/edw/home.html#/ "Edit" the relevant
         * Alexa Skill and put the relevant Application Ids in this Set.
         */
        supportedApplicationIds = new HashSet<String>();
        supportedApplicationIds.add("amzn1.ask.skill.e88f1ed9-b477-4c6e-ab4b-6f1ca77a85ae");
    }

    public BusRouterRequestStreamHandler() {
        super(new BusRouterSpeechlet(), supportedApplicationIds);
    }
}