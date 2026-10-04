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
package org.openhab.binding.weatherflowsmartweather.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Turns the {@code sensor_status} bit field of a Tempest {@code device_status} message into readable text.
 * Bit meanings follow the WeatherFlow Tempest UDP API (v171).
 *
 * @author Dave Schoepel - Initial contribution
 */
@NonNullByDefault
public final class SensorStatus {

    public static final String OK = "OK";

    private static final int[] BITS = { 0x1, 0x2, 0x4, 0x8, 0x10, 0x20, 0x40, 0x80, 0x100, 0x8000, 0x10000 };
    private static final String[] NAMES = { "Lightning sensor failed", "Lightning noise", "Lightning disturber",
            "Pressure sensor failed", "Temperature sensor failed", "Humidity sensor failed", "Wind sensor failed",
            "Rain sensor failed", "Light/UV sensor failed", "Power booster depleted", "Power booster on shore power" };

    private SensorStatus() {
    }

    /**
     * @return {@link #OK} when no bit is set, otherwise the set conditions separated by ", "
     */
    public static String describe(int status) {
        List<String> found = new ArrayList<>();
        for (int i = 0; i < BITS.length; i++) {
            if ((status & BITS[i]) != 0) {
                found.add(NAMES[i]);
            }
        }
        return found.isEmpty() ? OK : String.join(", ", found);
    }
}
