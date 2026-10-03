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

import org.openhab.binding.weatherflowsmartweather.model.PrecipitationStartedData;
import org.openhab.core.events.AbstractEvent;

public class PrecipitationStartedEvent extends AbstractEvent {
    public static final String TYPE = PrecipitationStartedEvent.class.getSimpleName();

    private final PrecipitationStartedData precipitationStartedData;

    PrecipitationStartedEvent(String topic, String payload, PrecipitationStartedData precipitationStartedData) {
        super(topic, payload, precipitationStartedData.getThingUID());
        this.precipitationStartedData = precipitationStartedData;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    public PrecipitationStartedData getPrecipitationStartedData() {
        return precipitationStartedData;
    }

    @Override
    public String toString() {
        return "Precipitation Started at '" + precipitationStartedData.toString() + "'.";
    }
}
