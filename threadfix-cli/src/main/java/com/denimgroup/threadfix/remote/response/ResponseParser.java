package com.denimgroup.threadfix.remote.response;

import com.denimgroup.threadfix.logging.SanitizedLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ResponseParser {

    private static final SanitizedLogger LOGGER = new SanitizedLogger(ResponseParser.class);

    private static <T> Type getTypeReference() {
        return new TypeReference<RestResponse<T>>(){}.getType();
    }

    public static RestResponse<String> getStringRestResponse(String responseString, int responseCode) {
        RestResponse<String> response = new RestResponse<String>();

        if (responseString != null && responseString.trim().indexOf('{') == 0) {
            try {
                Gson gson = new Gson();
                response = gson.fromJson(responseString, getTypeReference()); // turn everything into an object
                response.object = gson.toJson(response.object);
            } catch (JsonSyntaxException e) {
                LOGGER.error("Encountered JsonSyntaxException", e);
            }
        }

        response.responseCode = responseCode;

        return response;
    }

    private static Gson getGson() {
        GsonBuilder gsonB = new GsonBuilder();
        gsonB.registerTypeAdapter(Calendar.class, new CalendarSerializer());
        gsonB.registerTypeAdapter(GregorianCalendar.class, new CalendarSerializer());
        return gsonB.create();
    }

    // TODO remove the double JSON read for efficiency
    // I still think this will be IO-bound in most cases
    @SuppressWarnings("unchecked") // the JSON String preservation broke this
    public static <T> RestResponse<T> getRestResponse(String responseString, int responseCode, Class<T> internalClass) {

        LOGGER.info("Parsing from " + responseString);

        RestResponse<T> response = new RestResponse<T>();

        if (responseString != null && responseString.trim().indexOf('{') == 0) {
            try {
                Gson gson = getGson();
                response = gson.fromJson(responseString, getTypeReference()); // turn everything into an object
                String innerJson = gson.toJson(response.object); // turn the inner object back into a string

                if (response.object instanceof String) {
                    response.object = (T) innerJson;
                } else {
                    // turn the inner object into the correctly typed object
                    response.object = gson.fromJson(innerJson, internalClass);
                }
            } catch (JsonSyntaxException e) {
                LOGGER.error("Encountered JsonSyntaxException", e);
            }
        }

        response.responseCode = responseCode;

        return response;
    }

    public static <T> RestResponse<T> getRestResponse(InputStream responseStream, int responseCode, Class<T> target) {
        String inputString = null;

        try {
            inputString = IOUtils.toString(responseStream);
        } catch (IOException e) {
            LOGGER.error("Encountered IOException", e);
        }

        return getRestResponse(inputString, responseCode, target);
    }

    public static <T> RestResponse<T> getErrorResponse(String errorText, int responseCode) {
        RestResponse<T> instance = new RestResponse<T>();

        instance.message = errorText;
        instance.responseCode = responseCode;
        instance.success = false;

        return instance;
    }

}
