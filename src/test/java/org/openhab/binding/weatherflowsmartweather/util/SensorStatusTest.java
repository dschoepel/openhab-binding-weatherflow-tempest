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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

/**
 * Tests the decoding of the Tempest {@code sensor_status} bit field.
 *
 * @author Dave Schoepel - Initial contribution
 */
@NonNullByDefault
public class SensorStatusTest {

    @Test
    public void noBitsIsOk() {
        assertEquals("OK", SensorStatus.describe(0));
    }

    @Test
    public void listsEachSetBit() {
        assertEquals("Lightning sensor failed, Wind sensor failed", SensorStatus.describe(0x41));
        assertEquals("Power booster depleted", SensorStatus.describe(0x8000));
    }
}
