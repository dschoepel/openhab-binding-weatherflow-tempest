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

public class DeviceStatusMessage extends SmartWeatherMessage {
    private String hub_sn;
    private long timestamp;
    private long uptime;
    private int firmware_revision;
    private int rssi;
    private int hub_rssi;
    private float voltage;
    private int sensor_status;
    private int debug;

    public String getHub_sn() {
        return hub_sn;
    }

    public void setHub_sn(String hub_sn) {
        this.hub_sn = hub_sn;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getUptime() {
        return uptime;
    }

    public void setUptime(long uptime) {
        this.uptime = uptime;
    }

    public int getFirmware_revision() {
        return firmware_revision;
    }

    public void setFirmware_revision(int firmware_revision) {
        this.firmware_revision = firmware_revision;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public int getHub_rssi() {
        return hub_rssi;
    }

    public void setHub_rssi(int hub_rssi) {
        this.hub_rssi = hub_rssi;
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

    public int getDebug() {
        return debug;
    }

    public void setDebug(int debug) {
        this.debug = debug;
    }

    @Override
    public String toString() {
        return "DeviceStatusMessage{" + "hub_sn='" + hub_sn + '\'' + ", timestamp=" + timestamp + ", uptime=" + uptime
                + ", firmware_revision=" + firmware_revision + ", rssi=" + rssi + ", hub_rssi=" + hub_rssi
                + ", voltage=" + voltage + ", sensor_status=" + sensor_status + ", debug=" + debug + ", type='" + type
                + '\'' + ", serial_number='" + serial_number + '\'' + '}';
    }
}
