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

import org.openhab.binding.weatherflowsmartweather.model.LightningStrikeData;
import org.openhab.core.events.AbstractEvent;

public class LightningStrikeEvent extends AbstractEvent {
    public static final String TYPE = LightningStrikeEvent.class.getSimpleName();

    private final LightningStrikeData lightningStrikeData;

    LightningStrikeEvent(String topic, String payload, LightningStrikeData lightningStrikeData) {
        super(topic, payload, lightningStrikeData.getThingUID());
        this.lightningStrikeData = lightningStrikeData;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    public LightningStrikeData getLightningStrikeData() {
        return lightningStrikeData;
    }

    @Override
    public String toString() {
        return "Lightning Strike at '" + lightningStrikeData.toString() + "'.";
    }
}
