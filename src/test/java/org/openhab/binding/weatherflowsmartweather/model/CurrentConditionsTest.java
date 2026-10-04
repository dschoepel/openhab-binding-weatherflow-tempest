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
package org.openhab.binding.weatherflowsmartweather.model;

import static org.junit.jupiter.api.Assertions.*;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

/**
 * Tests that the forecast's current conditions keep the values the enriched forecast channel passes on.
 *
 * @author Dave Schoepel - Initial contribution
 */
@NonNullByDefault
public class CurrentConditionsTest {

    private final Gson gson = new Gson();

    @Test
    public void keepsWetBulbGlobeTemperature() {
        CurrentConditions conditions = gson.fromJson("""
                {"wet_bulb_temperature":56.0,"wet_bulb_globe_temperature":63.0}
                """, CurrentConditions.class);

        assertNotNull(conditions);
        assertEquals(63.0, conditions.getWet_bulb_globe_temperature().doubleValue());
        assertTrue(gson.toJson(conditions).contains("\"wet_bulb_globe_temperature\":63.0"));
    }
}
