/*
 * Copyright (c) 2010-2026 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.weatherflowsmartweather.event;

import static org.openhab.binding.weatherflowsmartweather.util.GsonUtils.gsonDateTime;

import java.time.format.DateTimeFormatter;
import java.util.Set;

import org.openhab.binding.weatherflowsmartweather.model.RapidWindData;
import org.openhab.core.events.AbstractEventFactory;
import org.openhab.core.events.Event;
import org.openhab.core.events.EventFactory;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.*;

@Component(service = { EventFactory.class,
        RapidWindEventFactory.class }, configurationPid = "binding.weatherflowsmartweather")
public class RapidWindEventFactoryImpl extends AbstractEventFactory implements RapidWindEventFactory {

    static final String RAPID_WIND_EVENT_TOPIC = "openhab/things/{thingUID}/rapid_wind";

    private static final Logger log = LoggerFactory.getLogger(RapidWindEventFactoryImpl.class);

    public RapidWindEventFactoryImpl() {
        super(Set.of(RapidWindEvent.TYPE));
    }

    public static RapidWindEvent createRapidWindEvent(RapidWindData rapid_wind) {
        String topic = RapidWindEventFactoryImpl.buildTopic(RapidWindEventFactoryImpl.RAPID_WIND_EVENT_TOPIC,
                rapid_wind.getThingUID());
        log.debug("Topic: {}", topic);
        String payload = null;

        try {
            payload = mySerializePayload(rapid_wind);
        } catch (Throwable e) {
            log.error("Error serializing payload.", e);
        }
        log.debug("Payload: {}", payload);
        return new RapidWindEvent(topic, payload, rapid_wind);
    }

    @Override
    protected Event createEventByType(String eventType, String topic, String payload, String source) throws Exception {
        // log.debug("Creating event {} topic={}, payload={}, source={}", eventType, topic, payload, source);
        if (RapidWindEvent.TYPE.equals(eventType)) {
            if (log.isDebugEnabled())
                log.debug("Creating event {} topic={}, payload={}, source={}", eventType, topic, payload, source);
            return createRapidWindEvent(topic, payload);
        }
        throw new IllegalArgumentException("Unsupported event type " + eventType);
    }

    protected static String buildTopic(String topic, String thingUID) {
        return topic.replace("{thingUID}", thingUID);
    }

    private Event createRapidWindEvent(String topic, String payload) {
        RapidWindData rapidWindData = myDeserializePayload(payload, RapidWindData.class);
        return new RapidWindEvent(topic, payload, rapidWindData);
    }

    protected static <T> T myDeserializePayload(String payload, Class<T> classOfPayload) {
        if (log.isTraceEnabled())
            log.trace("deserializing {} into {}", payload, classOfPayload);
        return gsonDateTime().fromJson(payload, classOfPayload);
    }

    protected static String mySerializePayload(RapidWindData payloadObject) {

        StringBuilder b = new StringBuilder();
        b.append("{");

        b.append("\"");
        b.append("bridgeUID");
        b.append("\": ");

        b.append("\"");
        b.append(payloadObject.getBridgeUID());
        b.append("\", ");

        b.append("\"");
        b.append("thingUID");
        b.append("\": ");

        b.append("\"");
        b.append(payloadObject.getThingUID());
        b.append("\", ");

        b.append("\"");
        b.append("hubSerialNumber");
        b.append("\": ");

        b.append("\"");
        b.append(payloadObject.getHubSerialNumber());
        b.append("\", ");

        b.append("\"");
        b.append("serialNumber");
        b.append("\": ");

        b.append("\"");
        b.append(payloadObject.getSerialNumber());
        b.append("\", ");

        b.append("\"");
        b.append("epoch");
        b.append("\": ");

        b.append("\"");
        b.append(payloadObject.getEpoch().format(DateTimeFormatter.ISO_DATE_TIME));
        b.append("\", ");

        b.append("\"");
        b.append("windDirection");
        b.append("\": ");

        b.append("\"");
        b.append(payloadObject.getWindDirection().toFullString());
        b.append("\", ");

        b.append("\"");
        b.append("windSpeed");
        b.append("\": ");

        b.append("\"");
        b.append(payloadObject.getWindSpeed().toFullString());
        b.append("\"");

        b.append("}");

        /*
         * private String bridgeUID;
         * private String thingUID;
         * 
         * private String hubSerialNumber;
         * private String serialNumber;
         * 
         * private ZonedDateTime epoch;
         * private final QuantityType<Angle> windDirection;
         * private final QuantityType<Speed> windSpeed;
         */

        if (log.isTraceEnabled())
            log.trace("Serialized RapidWindData to {}", b.toString());
        return b.toString();
        // return gsonDateTime().toJson(payloadObject);
    }
}
