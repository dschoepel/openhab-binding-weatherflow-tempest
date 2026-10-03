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

import static java.time.ZoneOffset.UTC;
import static org.openhab.core.library.unit.MetricPrefix.KILO;

import java.time.Instant;
import java.time.ZonedDateTime;

import javax.measure.quantity.Length;

import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.library.unit.SIUnits;
import org.openhab.core.thing.Thing;

public class LightningStrikeData {
    private String bridgeUID;
    private String thingUID;

    private String hubSerialNumber;
    private String serialNumber;

    private ZonedDateTime epoch;
    private final DecimalType energy;
    private final QuantityType<Length> distance;

    // private Logger log = LoggerFactory.getLogger(RapidWindData.class);

    public LightningStrikeData(Thing sky, EventStrikeMessage message) {
        bridgeUID = sky.getBridgeUID().getAsString();
        thingUID = sky.getUID().getAsString();

        serialNumber = message.getSerial_number();
        hubSerialNumber = message.getHub_sn();
        Object[] ob = message.getEvt();

        epoch = Instant.ofEpochMilli(((Double) ob[0]).intValue() * 1000L).atZone(UTC);

        distance = new QuantityType<Length>((Double) ob[1], KILO(SIUnits.METRE));
        energy = new DecimalType((Double) ob[2]);
    }

    public DecimalType getEnergy() {
        return energy;
    }

    public QuantityType<Length> getDistance() {
        return distance;
    }

    public String getBridgeUID() {
        return bridgeUID;
    }

    public String getThingUID() {
        return thingUID;
    }

    public String getHubSerialNumber() {
        return hubSerialNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public ZonedDateTime getEpoch() {
        return epoch;
    }

    @Override
    public String toString() {
        return "LightningStrikeData{" + "bridgeUID=" + bridgeUID + ", thingUID=" + thingUID + ", epoch=" + epoch
                + ", distance=" + distance + ", energy=" + energy + '}';
    }
}
