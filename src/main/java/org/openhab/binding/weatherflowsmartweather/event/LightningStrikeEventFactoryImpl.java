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

import org.openhab.binding.weatherflowsmartweather.model.LightningStrikeData;
import org.openhab.core.events.AbstractEventFactory;
import org.openhab.core.events.Event;
import org.openhab.core.events.EventFactory;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.*;

@Component(service = { EventFactory.class,
        LightningStrikeEventFactory.class }, configurationPid = "binding.weatherflowsmartweather")
public class LightningStrikeEventFactoryImpl extends AbstractEventFactory implements LightningStrikeEventFactory {

    static final String LIGHTNING_STRIKE_EVENT_TOPIC = "openhab/things/{thingUID}/lightning_strike";

    private static final Logger log = LoggerFactory.getLogger(LightningStrikeEventFactoryImpl.class);

    public LightningStrikeEventFactoryImpl() {
        super(Set.of(LightningStrikeEvent.TYPE));
    }

    public static LightningStrikeEvent createLightningStrikeEvent(LightningStrikeData lightning_strike) {
        String topic = LightningStrikeEventFactoryImpl.buildTopic(
                LightningStrikeEventFactoryImpl.LIGHTNING_STRIKE_EVENT_TOPIC, lightning_strike.getThingUID());
        log.debug("Topic: {}", topic);
        String payload = null;

        try {
            payload = mySerializePayload(lightning_strike);
        } catch (Throwable e) {
            log.error("Error serializing payload.", e);
        }
        log.debug("Payload: {}", payload);
        return new LightningStrikeEvent(topic, payload, lightning_strike);
    }

    @Override
    protected Event createEventByType(String eventType, String topic, String payload, String source) throws Exception {
        if (LightningStrikeEvent.TYPE.equals(eventType)) {
            log.debug("creating event {} topic={}, payload={}, source={}", eventType, topic, payload, source);
            return createLightningStrikeEvent(topic, payload);
        }
        throw new IllegalArgumentException("Unsupported event type " + eventType);
    }

    protected static String buildTopic(String topic, String thingUID) {
        return topic.replace("{thingUID}", thingUID);
    }

    private Event createLightningStrikeEvent(String topic, String payload) {
        LightningStrikeData lightningStrikeData = myDeserializePayload(payload, LightningStrikeData.class);
        return new LightningStrikeEvent(topic, payload, lightningStrikeData);
    }

    protected static <T> T myDeserializePayload(String payload, Class<T> classOfPayload) {
        return gsonDateTime().fromJson(payload, classOfPayload);
    }

    protected static String mySerializePayload(LightningStrikeData payloadObject) {
        return gsonDateTime().toJson(payloadObject);
    }
}
