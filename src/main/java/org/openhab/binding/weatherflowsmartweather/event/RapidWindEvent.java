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

import org.openhab.binding.weatherflowsmartweather.model.RapidWindData;
import org.openhab.core.events.AbstractEvent;

public class RapidWindEvent extends AbstractEvent {
    public static final String TYPE = RapidWindEvent.class.getSimpleName();

    private final RapidWindData rapidWindData;

    RapidWindEvent(String topic, String payload, RapidWindData rapidWindData) {
        super(topic, payload, rapidWindData.getThingUID());
        this.rapidWindData = rapidWindData;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    public RapidWindData getRapidWindData() {
        return rapidWindData;
    }

    @Override
    public String toString() {
        return "Rapid Wind at '" + rapidWindData.toString() + "'.";
    }
}
