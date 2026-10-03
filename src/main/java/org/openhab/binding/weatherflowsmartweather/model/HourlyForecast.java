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

public class HourlyForecast {
    /*
     * "time": 1608735600,
     * "conditions": "Clear",
     * "icon": "clear-day",
     * "air_temperature": 57,
     * "sea_level_pressure": 30.257,
     * "relative_humidity": 82,
     * "precip": 0,
     * "precip_probability": 0,
     * "wind_avg": 2,
     * "wind_direction": 34,
     * "wind_direction_cardinal": "NE",
     * "wind_gust": 3,
     * "uv": 2,
     * "feels_like": 57,
     * "local_hour": 10,
     * "local_day": 23
     */
    long time;
    String conditions;
    String icon;
    Number air_temperature;
    Number sea_level_pressure;
    Number relative_humidity;
    Number precip;
    Number precip_probability;
    Number wind_avg;
    Number wind_direction;
    String wind_direction_cardinal;
    Number wind_gust;
    Number uv;
    Number feels_like;
    int local_hour;
    int local_day;

    // enriched values
    boolean sun_risen_at_start = false;
    boolean sun_set_at_start = false;
    boolean sun_rises_this_hour = false;
    boolean sun_sets_this_hour = false;

    long next_sunrise;
    long next_sunset;

    String wi_icon;

    public String getWi_icon() {
        return wi_icon;
    }

    public void setWi_icon(String wi_icon) {
        this.wi_icon = wi_icon;
    }

    public boolean isSun_set_at_start() {
        return sun_set_at_start;
    }

    public void setSun_set_at_start(boolean sun_set_at_start) {
        this.sun_set_at_start = sun_set_at_start;
    }

    public boolean isSun_rises_this_hour() {
        return sun_rises_this_hour;
    }

    public void setSun_rises_this_hour(boolean sun_rises_this_hour) {
        this.sun_rises_this_hour = sun_rises_this_hour;
    }

    public boolean isSun_sets_this_hour() {
        return sun_sets_this_hour;
    }

    public void setSun_sets_this_hour(boolean sun_sets_this_hour) {
        this.sun_sets_this_hour = sun_sets_this_hour;
    }

    public boolean isSun_risen_at_start() {
        return sun_risen_at_start;
    }

    public void setSun_risen_at_start(boolean sun_risen_at_start) {
        this.sun_risen_at_start = sun_risen_at_start;
    }

    public long getNext_sunrise() {
        return next_sunrise;
    }

    public void setNext_sunrise(long next_sunrise) {
        this.next_sunrise = next_sunrise;
    }

    public long getNext_sunset() {
        return next_sunset;
    }

    public void setNext_sunset(long next_sunset) {
        this.next_sunset = next_sunset;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
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

    public Number getAir_temperature() {
        return air_temperature;
    }

    public void setAir_temperature(Number air_temperature) {
        this.air_temperature = air_temperature;
    }

    public Number getSea_level_pressure() {
        return sea_level_pressure;
    }

    public void setSea_level_pressure(Number sea_level_pressure) {
        this.sea_level_pressure = sea_level_pressure;
    }

    public Number getRelative_humidity() {
        return relative_humidity;
    }

    public void setRelative_humidity(Number relative_humidity) {
        this.relative_humidity = relative_humidity;
    }

    public Number getPrecip() {
        return precip;
    }

    public void setPrecip(Number precip) {
        this.precip = precip;
    }

    public Number getPrecip_probability() {
        return precip_probability;
    }

    public void setPrecip_probability(Number precip_probability) {
        this.precip_probability = precip_probability;
    }

    public Number getWind_avg() {
        return wind_avg;
    }

    public void setWind_avg(Number wind_avg) {
        this.wind_avg = wind_avg;
    }

    public Number getWind_direction() {
        return wind_direction;
    }

    public void setWind_direction(Number wind_direction) {
        this.wind_direction = wind_direction;
    }

    public String getWind_direction_cardinal() {
        return wind_direction_cardinal;
    }

    public void setWind_direction_cardinal(String wind_direction_cardinal) {
        this.wind_direction_cardinal = wind_direction_cardinal;
    }

    public Number getWind_gust() {
        return wind_gust;
    }

    public void setWind_gust(Number wind_gust) {
        this.wind_gust = wind_gust;
    }

    public Number getUv() {
        return uv;
    }

    public void setUv(Number uv) {
        this.uv = uv;
    }

    public Number getFeels_like() {
        return feels_like;
    }

    public void setFeels_like(Number feels_like) {
        this.feels_like = feels_like;
    }

    public int getLocal_hour() {
        return local_hour;
    }

    public void setLocal_hour(int local_hour) {
        this.local_hour = local_hour;
    }

    public int getLocal_day() {
        return local_day;
    }

    public void setLocal_day(int local_day) {
        this.local_day = local_day;
    }

    @Override
    public String toString() {
        return "HourlyForecast{" + "time=" + time + ", conditions='" + conditions + '\'' + ", icon='" + icon + '\''
                + ", air_temperature=" + air_temperature + ", sea_level_pressure=" + sea_level_pressure
                + ", relative_humidity=" + relative_humidity + ", precip=" + precip + ", precip_probability="
                + precip_probability + ", wind_avg=" + wind_avg + ", wind_direction=" + wind_direction
                + ", wind_direction_cardinal='" + wind_direction_cardinal + '\'' + ", wind_gust=" + wind_gust + ", uv="
                + uv + ", feels_like=" + feels_like + ", local_hour=" + local_hour + ", local_day=" + local_day
                + ", sun_risen_at_start=" + sun_risen_at_start + ", sun_set_at_start=" + sun_set_at_start
                + ", sun_rises_this_hour=" + sun_rises_this_hour + ", sun_sets_this_hour=" + sun_sets_this_hour
                + ", next_sunrise=" + next_sunrise + ", next_sunset=" + next_sunset + ", wi_icon='" + wi_icon + '\''
                + '}';
    }
}
