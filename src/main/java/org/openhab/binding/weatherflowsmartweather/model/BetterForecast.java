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

public class BetterForecast {
    ForecastStatus status;
    ForecastUnits units;
    Forecast forecast;
    CurrentConditions current_conditions;
    String location_name;
    Number latitude;
    Number longitude;
    String timezone;
    Number timezone_offset_minutes;

    public void enrich() {
        getForecast().enrichForecast();
        getCurrentConditions().enrichCurrentConditions();
    }

    public void enrich(int keepHourly, int keepDaily) {
        getForecast().enrichForecast(keepHourly, keepDaily);
        getCurrentConditions().enrichCurrentConditions();
    }

    @Override
    public String toString() {
        return "BetterForecast{" + "status=" + status + ", units=" + units + ", forecast=" + forecast
                + ", current_conditions=" + current_conditions + ", location_name='" + location_name + '\''
                + ", latitude=" + latitude + ", longitude=" + longitude + ", timezone='" + timezone + '\''
                + ", timezone_offset_minutes=" + timezone_offset_minutes + '}';
    }

    public ForecastUnits getUnits() {
        return units;
    }

    public void setUnits(ForecastUnits units) {
        this.units = units;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public Number getLatitude() {
        return latitude;
    }

    public void setLatitude(Number latitude) {
        this.latitude = latitude;
    }

    public Number getLongitude() {
        return longitude;
    }

    public void setLongitude(Number longitude) {
        this.longitude = longitude;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Number getTimezone_offset_minutes() {
        return timezone_offset_minutes;
    }

    public void setTimezone_offset_minutes(Number timezone_offset_minutes) {
        this.timezone_offset_minutes = timezone_offset_minutes;
    }

    public ForecastStatus getStatus() {
        return status;
    }

    public void setStatus(ForecastStatus status) {
        this.status = status;
    }

    public Forecast getForecast() {
        return forecast;
    }

    public void setForecast(Forecast forecast) {
        this.forecast = forecast;
    }

    public CurrentConditions getCurrentConditions() {
        return current_conditions;
    }

    public void setCurrentConditions(CurrentConditions current_conditions) {
        this.current_conditions = current_conditions;
    }
}
