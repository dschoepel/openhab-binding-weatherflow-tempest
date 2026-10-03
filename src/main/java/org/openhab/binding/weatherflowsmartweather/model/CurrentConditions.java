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

import org.openhab.binding.weatherflowsmartweather.util.ForecastUtils;

public class CurrentConditions {
    /*
     * "time": 1608668142,
     * "conditions": "Clear",
     * "icon": "clear-day",
     * "air_temperature": 67,
     * "sea_level_pressure": 30.195,
     * "station_pressure": 30.079,
     * "pressure_trend": "falling",
     * "relative_humidity": 55,
     * "wind_avg": 3.2,
     * "wind_direction": 15,
     * "wind_direction_cardinal": "NNE",
     * "wind_direction_icon": "wind-rose-nne",
     * "wind_gust": 3.4,
     * "solar_radiation": 22,
     * "uv": 0,
     * "brightness": 2582,
     * "feels_like": 68.9,
     * "dew_point": 50,
     * "wet_bulb_temperature": 57,
     * "delta_t": 10,
     * "air_density": 0.08,
     * "lightning_strike_count_last_1hr": 0,
     * "lightning_strike_count_last_3hr": 1,
     * "lightning_strike_last_distance": 7,
     * "lightning_strike_last_distance_msg": "6 - 8 mi",
     * "lightning_strike_last_epoch": 1608150959,
     * "precip_accum_local_day": 0,
     * "precip_accum_local_yesterday": 0,
     * "precip_minutes_local_day": 0,
     * "precip_minutes_local_yesterday": 0,
     * "is_precip_local_day_rain_check": false,
     * "is_precip_local_yesterday_rain_check": true
     */
    long time;
    String conditions;
    String icon;
    Number air_temperature;
    Number sea_level_pressure;
    Number station_pressure;
    String pressure_trend;
    Number relative_humidity;
    Number wind_avg;
    Number wind_direction;
    String wind_direction_cardinal;
    String wind_direction_icon;
    Number wind_gust;
    Number solar_radiation;
    Number uv;
    Number brightness;
    Number feels_like;
    Number dew_point;
    Number wet_bulb_temperature;
    Number delta_t;
    Number air_density;
    Number local_hour;
    Number local_day;
    Number lightning_strike_count_last_1hr;
    Number lightning_strike_count_last_3hr;
    Number lightning_strike_last_distance;
    String lightning_strike_last_distance_msg;
    Number lightning_strike_last_epoch;
    Number precip_accum_local_day;
    Number precip_accum_local_yesterday;
    Number precip_minutes_local_day;
    Number precip_minutes_local_yesterday;
    Boolean is_precip_local_day_rain_check;
    Boolean is_precip_local_yesterday_rain_check;

    // enriched fields
    String wi_icon;

    public void enrichCurrentConditions() {
        setWi_icon(ForecastUtils.mapWeatherIcon(getIcon()));
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

    public Number getStation_pressure() {
        return station_pressure;
    }

    public void setStation_pressure(Number station_pressure) {
        this.station_pressure = station_pressure;
    }

    public String getPressure_trend() {
        return pressure_trend;
    }

    public void setPressure_trend(String pressure_trend) {
        this.pressure_trend = pressure_trend;
    }

    public Number getRelative_humidity() {
        return relative_humidity;
    }

    public void setRelative_humidity(Number relative_humidity) {
        this.relative_humidity = relative_humidity;
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

    public String getWind_direction_icon() {
        return wind_direction_icon;
    }

    public void setWind_direction_icon(String wind_direction_icon) {
        this.wind_direction_icon = wind_direction_icon;
    }

    public Number getWind_gust() {
        return wind_gust;
    }

    public void setWind_gust(Number wind_gust) {
        this.wind_gust = wind_gust;
    }

    public Number getSolar_radiation() {
        return solar_radiation;
    }

    public void setSolar_radiation(Number solar_radiation) {
        this.solar_radiation = solar_radiation;
    }

    public Number getUv() {
        return uv;
    }

    public void setUv(Number uv) {
        this.uv = uv;
    }

    public Number getBrightness() {
        return brightness;
    }

    public void setBrightness(Number brightness) {
        this.brightness = brightness;
    }

    public Number getFeels_like() {
        return feels_like;
    }

    public void setFeels_like(Number feels_like) {
        this.feels_like = feels_like;
    }

    public Number getDew_point() {
        return dew_point;
    }

    public void setDew_point(Number dew_point) {
        this.dew_point = dew_point;
    }

    public Number getWet_bulb_temperature() {
        return wet_bulb_temperature;
    }

    public void setWet_bulb_temperature(Number wet_bulb_temperature) {
        this.wet_bulb_temperature = wet_bulb_temperature;
    }

    public Number getDelta_t() {
        return delta_t;
    }

    public void setDelta_t(Number delta_t) {
        this.delta_t = delta_t;
    }

    public Number getAir_density() {
        return air_density;
    }

    public void setAir_density(Number air_density) {
        this.air_density = air_density;
    }

    public Number getLocal_hour() {
        return local_hour;
    }

    public void setLocal_hour(Number local_hour) {
        this.local_hour = local_hour;
    }

    public Number getLocal_day() {
        return local_day;
    }

    public void setLocal_day(Number local_day) {
        this.local_day = local_day;
    }

    public Number getLightning_strike_count_last_1hr() {
        return lightning_strike_count_last_1hr;
    }

    public void setLightning_strike_count_last_1hr(Number lightning_strike_count_last_1hr) {
        this.lightning_strike_count_last_1hr = lightning_strike_count_last_1hr;
    }

    public Number getLightning_strike_count_last_3hr() {
        return lightning_strike_count_last_3hr;
    }

    public void setLightning_strike_count_last_3hr(Number lightning_strike_count_last_3hr) {
        this.lightning_strike_count_last_3hr = lightning_strike_count_last_3hr;
    }

    public Number getLightning_strike_last_distance() {
        return lightning_strike_last_distance;
    }

    public void setLightning_strike_last_distance(Number lightning_strike_last_distance) {
        this.lightning_strike_last_distance = lightning_strike_last_distance;
    }

    public String getLightning_strike_last_distance_msg() {
        return lightning_strike_last_distance_msg;
    }

    public void setLightning_strike_last_distance_msg(String lightning_strike_last_distance_msg) {
        this.lightning_strike_last_distance_msg = lightning_strike_last_distance_msg;
    }

    public Number getLightning_strike_last_epoch() {
        return lightning_strike_last_epoch;
    }

    public void setLightning_strike_last_epoch(Number lightning_strike_last_epoch) {
        this.lightning_strike_last_epoch = lightning_strike_last_epoch;
    }

    public Number getPrecip_accum_local_day() {
        return precip_accum_local_day;
    }

    public void setPrecip_accum_local_day(Number precip_accum_local_day) {
        this.precip_accum_local_day = precip_accum_local_day;
    }

    public Number getPrecip_accum_local_yesterday() {
        return precip_accum_local_yesterday;
    }

    public void setPrecip_accum_local_yesterday(Number precip_accum_local_yesterday) {
        this.precip_accum_local_yesterday = precip_accum_local_yesterday;
    }

    public Number getPrecip_minutes_local_day() {
        return precip_minutes_local_day;
    }

    public void setPrecip_minutes_local_day(Number precip_minutes_local_day) {
        this.precip_minutes_local_day = precip_minutes_local_day;
    }

    public Number getPrecip_minutes_local_yesterday() {
        return precip_minutes_local_yesterday;
    }

    public void setPrecip_minutes_local_yesterday(Number precip_minutes_local_yesterday) {
        this.precip_minutes_local_yesterday = precip_minutes_local_yesterday;
    }

    public Boolean getIs_precip_local_day_rain_check() {
        return is_precip_local_day_rain_check;
    }

    public void setIs_precip_local_day_rain_check(Boolean is_precip_local_day_rain_check) {
        this.is_precip_local_day_rain_check = is_precip_local_day_rain_check;
    }

    public Boolean getIs_precip_local_yesterday_rain_check() {
        return is_precip_local_yesterday_rain_check;
    }

    public void setIs_precip_local_yesterday_rain_check(Boolean is_precip_local_yesterday_rain_check) {
        this.is_precip_local_yesterday_rain_check = is_precip_local_yesterday_rain_check;
    }

    public String getWi_icon() {
        return this.wi_icon;
    }

    public void setWi_icon(String wi_icon) {
        this.wi_icon = wi_icon;
    }
}
