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

import java.util.Set;

import org.openhab.binding.weatherflowsmartweather.model.PrecipitationStartedData;
import org.openhab.core.events.AbstractEventFactory;
import org.openhab.core.events.Event;
import org.openhab.core.events.EventFactory;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.*;

@Component(service = { EventFactory.class,
        PrecipitationStartedEventFactory.class }, configurationPid = "binding.weatherflowsmartweather")
public class PrecipitationStartedEventFactoryImpl extends AbstractEventFactory
        implements PrecipitationStartedEventFactory {

    static final String PRECIPITATION_STARTED_EVENT_TOPIC = "openhab/things/{thingUID}/precipitationstarted";

    private static final Logger log = LoggerFactory.getLogger(PrecipitationStartedEventFactoryImpl.class);

    public PrecipitationStartedEventFactoryImpl() {
        super(Set.of(PrecipitationStartedEvent.TYPE));
    }

    @Override
    protected Event createEventByType(String eventType, String topic, String payload, String source) throws Exception {
        if (PrecipitationStartedEvent.TYPE.equals(eventType)) {
            log.debug("creating event {} topic={}, payload={}, source={}", eventType, topic, payload, source);
            return createPrecipitionStartedEvent(topic, payload);
        }
        throw new IllegalArgumentException("Unsupported event type " + eventType);
    }

    public static PrecipitationStartedEvent createPrecipitionStartedEvent(
            PrecipitationStartedData precipitation_started) {
        String topic = PrecipitationStartedEventFactoryImpl.buildTopic(
                PrecipitationStartedEventFactoryImpl.PRECIPITATION_STARTED_EVENT_TOPIC,
                precipitation_started.getThingUID());
        String payload = null;

        try {
            payload = PrecipitationStartedEventFactoryImpl.mySerializePayload(precipitation_started);
        } catch (Throwable e) {
            PrecipitationStartedEventFactoryImpl.log.error("Error serializing payload.", e);
        }
        return new PrecipitationStartedEvent(topic, payload, precipitation_started);
    }

    protected static String buildTopic(String topic, String thingUID) {
        return topic.replace("{thingUID}", thingUID);
    }

    private Event createPrecipitionStartedEvent(String topic, String payload) {
        PrecipitationStartedData precipitationStartedData = myDeserializePayload(payload,
                PrecipitationStartedData.class);
        return new PrecipitationStartedEvent(topic, payload, precipitationStartedData);
    }

    protected static <T> T myDeserializePayload(String payload, Class<T> classOfPayload) {
        return gsonDateTime().fromJson(payload, classOfPayload);
    }

    protected static String mySerializePayload(PrecipitationStartedData payloadObject) {
        return gsonDateTime().toJson(payloadObject);
    }
}
