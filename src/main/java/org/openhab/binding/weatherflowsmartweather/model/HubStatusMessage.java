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

public class HubStatusMessage extends SmartWeatherMessage {
    protected String firmware_revision;
    protected long uptime;
    protected int rssi;
    protected long timestamp;

    public String getFirmware_version() {
        return firmware_revision;
    }

    public void setFirmware_version(String firmware_version) {
        this.firmware_revision = firmware_version;
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

    @Override
    public String toString() {
        return "HubStatusMessage [firmware_version=" + firmware_revision + ", uptime=" + uptime + ", rssi=" + rssi
                + ", timestamp=" + timestamp + ", serial_number=" + serial_number + "]";
    }
}
