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

import java.util.List;

public class ObservationTempestMessage extends SmartWeatherMessage {
    private String hub_sn;

    private List<List> obs;

    private int firmware_revision;

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

    public List<List> getObs() {
        return obs;
    }

    public void setObs(List<List> obs) {
        this.obs = obs;
    }

    @Override
    public String toString() {
        return "ObservationTempestMessage{" + "hub_sn='" + hub_sn + '\'' + ", obs=" + obs + ", firmware_revision="
                + firmware_revision + ", serial_number='" + serial_number + '\'' + '}';
    }
}
