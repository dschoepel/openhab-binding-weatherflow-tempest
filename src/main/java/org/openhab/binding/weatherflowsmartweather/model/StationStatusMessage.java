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

public class StationStatusMessage extends SmartWeatherMessage {
    private int firmware_revision;
    private long uptime;
    private int rssi;
    private long timestamp;
    private String hub_sn;
    private float voltage;
    private int sensor_status;

    public int getFirmware_revision() {
        return firmware_revision;
    }

    public void setVersion(int version) {
        this.firmware_revision = version;
    }

    public long getUptime() {
        return uptime;
    }

    public void setUptime(long uptime) {
        this.uptime = uptime;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getHub_sn() {
        return hub_sn;
    }

    public void setHub_sn(String hub_sn) {
        this.hub_sn = hub_sn;
    }

    public float getVoltage() {
        return voltage;
    }

    public void setVoltage(float voltage) {
        this.voltage = voltage;
    }

    public int getSensor_status() {
        return sensor_status;
    }

    public void setSensor_status(int sensor_status) {
        this.sensor_status = sensor_status;
    }

    @Override
    public String toString() {
        return "StationStatusMessage [fimware_revision=" + firmware_revision + ", uptime=" + uptime + ", rssi=" + rssi
                + ", timestamp=" + timestamp + ", hub_sn=" + hub_sn + ", voltage=" + voltage + ", sensor_status="
                + sensor_status + ", serial_number=" + serial_number + "]";
    }
}
