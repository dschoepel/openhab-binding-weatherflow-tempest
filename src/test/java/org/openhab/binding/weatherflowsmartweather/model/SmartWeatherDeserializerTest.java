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

import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Tests that {@link SmartWeatherDeserializer} turns the UDP broadcasts a Tempest hub sends into the right message
 * classes. The sample messages follow the WeatherFlow Tempest UDP API (v171).
 *
 * @author Dave Schoepel - Initial contribution
 */
@NonNullByDefault
public class SmartWeatherDeserializerTest {

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(SmartWeatherMessage.class, new SmartWeatherDeserializer()).create();

    private @Nullable SmartWeatherMessage parse(String json) {
        return gson.fromJson(json, SmartWeatherMessage.class);
    }

    @Test
    public void parsesTempestObservation() {
        SmartWeatherMessage message = parse("""
                {"serial_number":"ST-00000512","type":"obs_st","hub_sn":"HB-00013030",
                "obs":[[1588948614,0.18,0.22,0.27,144,6,1017.57,22.37,50.26,328,0.03,3,0.000000,0,0,0,2.410,1]],
                "firmware_revision":129}
                """);

        ObservationTempestMessage observation = assertInstanceOf(ObservationTempestMessage.class, message);
        assertEquals("ST-00000512", observation.getSerial_number());
        assertEquals("HB-00013030", observation.getHub_sn());
        List<List> obs = observation.getObs();
        assertEquals(1, obs.size());
        assertEquals(18, obs.get(0).size());
        assertEquals(22.37, ((Number) obs.get(0).get(7)).doubleValue(), 0.001);
    }

    @Test
    public void parsesRapidWind() {
        SmartWeatherMessage message = parse("""
                {"serial_number":"ST-00000512","type":"rapid_wind","hub_sn":"HB-00013030",
                "ob":[1588948614,0.27,144]}
                """);

        EventRapidWindMessage rapidWind = assertInstanceOf(EventRapidWindMessage.class, message);
        assertEquals("HB-00013030", rapidWind.getHub_sn());
        assertEquals(3, rapidWind.getOb().size());
    }

    @Test
    public void parsesLightningStrike() {
        SmartWeatherMessage message = parse("""
                {"serial_number":"ST-00000512","type":"evt_strike","hub_sn":"HB-00013030",
                "evt":[1493322445,27,3848]}
                """);

        EventStrikeMessage strike = assertInstanceOf(EventStrikeMessage.class, message);
        assertEquals(3, strike.getEvt().length);
    }

    @Test
    public void parsesPrecipitationStart() {
        SmartWeatherMessage message = parse("""
                {"serial_number":"ST-00000512","type":"evt_precip","hub_sn":"HB-00013030","evt":[1493322445]}
                """);

        assertInstanceOf(EventPrecipitationMessage.class, message);
    }

    @Test
    public void parsesDeviceStatus() {
        SmartWeatherMessage message = parse("""
                {"serial_number":"ST-00000512","type":"device_status","hub_sn":"HB-00013030",
                "timestamp":1510855923,"uptime":2189,"voltage":3.50,"firmware_revision":17,
                "rssi":-17,"hub_rssi":-87,"sensor_status":0,"debug":0}
                """);

        DeviceStatusMessage status = assertInstanceOf(DeviceStatusMessage.class, message);
        assertEquals("HB-00013030", status.getHub_sn());
        assertEquals(3.5f, status.getVoltage(), 0.001f);
        assertEquals(-87, status.getHub_rssi());
    }

    @Test
    public void parsesHubStatus() {
        SmartWeatherMessage message = parse("""
                {"serial_number":"HB-00000001","type":"hub_status","firmware_revision":"35",
                "uptime":1670133,"rssi":-62,"timestamp":1495724691,"reset_flags":"BOR,PIN,POR","seq":48,
                "fs":[1,0,15675411,524288],"radio_stats":[2,1,0,3,2839],"mqtt_stats":[1,0]}
                """);

        HubStatusV30Message status = assertInstanceOf(HubStatusV30Message.class, message);
        assertEquals("HB-00000001", status.getSerial_number());
        assertEquals(-62, status.getRssi());
        assertEquals(1670133, status.getUptime());
        assertEquals(1495724691, status.getTimestamp());
    }

    @Test
    public void ignoresUnsupportedSensors() {
        assertNull(parse("""
                {"serial_number":"AR-00004049","type":"obs_air","hub_sn":"HB-00000001",
                "obs":[[1493164835,835.0,10.0,45,0,0,3.46,1]],"firmware_revision":17}
                """));
        assertNull(parse("""
                {"serial_number":"SK-00008453","type":"obs_sky","hub_sn":"HB-00000001",
                "obs":[[1493321340,9000,10,0.0,2.6,4.6,7.4,187,3.12,1,130,null,0,3]],"firmware_revision":29}
                """));
    }

    @Test
    public void ignoresUnknownMessageType() {
        assertNull(parse("""
                {"serial_number":"ST-00000512","type":"something_new"}
                """));
    }
}
