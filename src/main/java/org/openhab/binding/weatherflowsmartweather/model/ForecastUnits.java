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

public class ForecastUnits {
    String units_temp;
    String units_wind;
    String units_precip;
    String units_pressure;
    String units_distance;
    String units_brightness;
    String units_solar_radiaton;
    String units_other;
    String units_air_density;

    @Override
    public String toString() {
        return "ForecastUnits{" + "units_temp='" + units_temp + '\'' + ", units_wind='" + units_wind + '\''
                + ", units_precip='" + units_precip + '\'' + ", units_pressure='" + units_pressure + '\''
                + ", units_distance='" + units_distance + '\'' + ", units_brightness='" + units_brightness + '\''
                + ", units_solar_radiaton='" + units_solar_radiaton + '\'' + ", units_other='" + units_other + '\''
                + ", units_air_density='" + units_air_density + '\'' + '}';
    }

    public String getUnits_temp() {
        return units_temp;
    }

    public void setUnits_temp(String units_temp) {
        this.units_temp = units_temp;
    }

    public String getUnits_wind() {
        return units_wind;
    }

    public void setUnits_wind(String units_wind) {
        this.units_wind = units_wind;
    }

    public String getUnits_precip() {
        return units_precip;
    }

    public void setUnits_precip(String units_precip) {
        this.units_precip = units_precip;
    }

    public String getUnits_pressure() {
        return units_pressure;
    }

    public void setUnits_pressure(String units_pressure) {
        this.units_pressure = units_pressure;
    }

    public String getUnits_distance() {
        return units_distance;
    }

    public void setUnits_distance(String units_distance) {
        this.units_distance = units_distance;
    }

    public String getUnits_brightness() {
        return units_brightness;
    }

    public void setUnits_brightness(String units_brightness) {
        this.units_brightness = units_brightness;
    }

    public String getUnits_solar_radiaton() {
        return units_solar_radiaton;
    }

    public void setUnits_solar_radiaton(String units_solar_radiaton) {
        this.units_solar_radiaton = units_solar_radiaton;
    }

    public String getUnits_other() {
        return units_other;
    }

    public void setUnits_other(String units_other) {
        this.units_other = units_other;
    }

    public String getUnits_air_density() {
        return units_air_density;
    }

    public void setUnits_air_density(String units_air_density) {
        this.units_air_density = units_air_density;
    }
}
