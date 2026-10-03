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

import java.util.Arrays;
import java.util.List;

public class EventRapidWindMessage extends SmartWeatherMessage {
    private int firmware_revision;
    private String hub_sn;
    private int device_id;
    private List ob;

    public int getFirmware_revision() {
        return firmware_revision;
    }

    public void setFirmware_revision(int firmware_revision) {
        this.firmware_revision = firmware_revision;
    }

    public String getHub_sn() {
        return hub_sn;
    }

    public void setHub_sn(String hub_sn) {
        this.hub_sn = hub_sn;
    }

    public int getDevice_id() {
        return device_id;
    }

    public void setDevice_id(int device_id) {
        this.device_id = device_id;
    }

    public List getOb() {
        return ob;
    }

    public void setOb(List ob) {
        this.ob = ob;
    }

    @Override
    public String toString() {
        return "EventRapidWindMessage [firmware_revision=" + firmware_revision + ", hub_sn=" + hub_sn + ", device_id="
                + device_id + ", ob=" + Arrays.toString(ob.toArray()) + ", serial_number=" + serial_number + "]";
    }
}
