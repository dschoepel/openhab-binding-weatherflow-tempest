package org.openhab.binding.weatherflowsmartweather.model;

import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class SmartWeatherDeserializer implements JsonDeserializer<SmartWeatherMessage> {

    private static Logger log = LoggerFactory.getLogger(SmartWeatherDeserializer.class);

    @Override
    public SmartWeatherMessage deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
            throws JsonParseException {
        // Get the "type" element from the parsed JSON
        JsonElement content = je.getAsJsonObject().get("type");
        String messageType = content.getAsString();
        Gson gson = new Gson();
        SmartWeatherMessage message = null;
        switch (messageType) {
            case "obs_st":
                return gson.fromJson(je, ObservationTempestMessage.class);
            case "evt_precip":
                return gson.fromJson(je, EventPrecipitationMessage.class);
            case "evt_strike":
                return gson.fromJson(je, EventStrikeMessage.class);
            case "rapid_wind":
                return gson.fromJson(je, EventRapidWindMessage.class);
            case "station_status":
                return gson.fromJson(je, StationStatusMessage.class);
            case "device_status":
                return gson.fromJson(je, DeviceStatusMessage.class);
            case "hub-status":
                return gson.fromJson(je, HubStatusMessage.class);
            case "hub_status":
                return gson.fromJson(je, HubStatusV30Message.class);
            case "obs_air":
            case "obs_sky":
            case "obs_pm":
                // Air, Sky and Air Quality sensors are not supported; only the Tempest is
            case "calibration":
            case "light_debug":
            case "wind_debug":
                log.trace("Ignoring {} message: {}", messageType, je);
                break;
            default:
                log.debug("Ignoring unknown message type {}: {}", messageType, je);
                break;
        }
        return message;
    }
}
