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

public class DailyForecast {
    /*
     * "day_start_local": 1608699600,
     * "day_num": 23,
     * "month_num": 12,
     * "conditions": "Clear",
     * "icon": "clear-day",
     * "sunrise": 1608639322,
     * "sunset": 1608676362,
     * "air_temp_high": 73,
     * "air_temp_low": 39,
     * "precip_probability": 10,
     * "precip_icon": "chance-rain",
     * "precip_type": "rain"
     */
    long day_start_local;
    int day_num;
    int month_num;
    String conditions;
    String icon;
    long sunrise;
    long sunset;
    Number air_temp_high;
    Number air_temp_low;
    Number precip_probability;
    String precip_icon;
    String precip_type;

    // enriched fields
    String wi_icon;

    public String getWi_icon() {
        return wi_icon;
    }

    public void setWi_icon(String wi_icon) {
        this.wi_icon = wi_icon;
    }

    public long getDay_start_local() {
        return day_start_local;
    }

    public void setDay_start_local(long time) {
        this.day_start_local = time;
    }

    public int getDay_num() {
        return day_num;
    }

    public void setDay_num(int day_num) {
        this.day_num = day_num;
    }

    public int getMonth_num() {
        return month_num;
    }

    public void setMonth_num(int month_num) {
        this.month_num = month_num;
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public long getSunrise() {
        return sunrise;
    }

    public void setSunrise(long sunrise) {
        this.sunrise = sunrise;
    }

    public long getSunset() {
        return sunset;
    }

    public void setSunset(long sunset) {
        this.sunset = sunset;
    }

    public Number getAir_temp_high() {
        return air_temp_high;
    }

    public void setAir_temp_high(Number air_temp_high) {
        this.air_temp_high = air_temp_high;
    }

    public Number getAir_temp_low() {
        return air_temp_low;
    }

    public void setAir_temp_low(Number air_temp_low) {
        this.air_temp_low = air_temp_low;
    }

    public Number getPrecip_probability() {
        return precip_probability;
    }

    public void setPrecip_probability(Number precip_probability) {
        this.precip_probability = precip_probability;
    }

    public String getPrecip_icon() {
        return precip_icon;
    }

    public void setPrecip_icon(String precip_icon) {
        this.precip_icon = precip_icon;
    }

    public String getPrecip_type() {
        return precip_type;
    }

    public void setPrecip_type(String precip_type) {
        this.precip_type = precip_type;
    }

    @Override
    public String toString() {
        return "DailyForecast{" + "day_start_local=" + day_start_local + ", day_num=" + day_num + ", month_num="
                + month_num + ", conditions='" + conditions + '\'' + ", icon='" + icon + '\'' + ", sunrise=" + sunrise
                + ", sunset=" + sunset + ", air_temp_high=" + air_temp_high + ", air_temp_low=" + air_temp_low
                + ", precip_probability=" + precip_probability + ", precip_icon='" + precip_icon + '\''
                + ", precip_type='" + precip_type + '\'' + ", wi_icon='" + wi_icon + '\'' + '}';
    }
}
