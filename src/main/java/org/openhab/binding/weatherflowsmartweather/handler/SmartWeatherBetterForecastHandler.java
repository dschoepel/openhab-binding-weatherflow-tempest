/**
 * Copyright (c) 2014,2017 by the respective copyright holders.
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.weatherflowsmartweather.handler;

import static org.openhab.binding.weatherflowsmartweather.WeatherFlowSmartWeatherBindingConstants.*;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.openhab.binding.weatherflowsmartweather.model.BetterForecast;
import org.openhab.binding.weatherflowsmartweather.model.BetterForecastThingConfig;
import org.openhab.core.library.types.DateTimeType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.thing.*;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * The {@link SmartWeatherBetterForecastHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author William Welliver - Initial contribution
 */

public class SmartWeatherBetterForecastHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(SmartWeatherBetterForecastHandler.class);

    protected final HttpClient httpClient;

    protected Gson gson = new Gson();

    protected Duration updateInterval = Duration.ofMinutes(15);
    protected ScheduledFuture<?> forecastUpdateTask = null;

    /**
     * @param httpClient openHAB's shared HTTP client, which is already started and checks certificates
     */
    public SmartWeatherBetterForecastHandler(Thing thing, HttpClient httpClient) {
        super(thing);
        this.httpClient = httpClient;
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // if (channelUID.getId().equals(CHANNEL_1)) {
        // TODO: handle command

        // Note: if communication with thing fails for some reason,
        // indicate that by setting the status with detail information
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
        // "Could not control device at IP address x.x.x.x");
        // }
    }
    //
    // @Override
    // public Collection<ConfigStatusMessage> getConfigStatus() {
    // Set<ConfigStatusMessage> status = new HashSet<>();
    //
    // BetterForecastThingConfig config = getConfigAs(BetterForecastThingConfig.class);
    //
    // if (config.getStationId() == 0) {
    // logger.warn("station_id is empty");
    // status.add(ConfigStatusMessage.Builder.error(CONFIG_STATION_ID).withMessageKeySuffix(EMPTY_INVALID)
    // .withArguments(CONFIG_STATION_ID).build());
    // }
    //
    // if (config.getToken() == null || config.getToken().isEmpty()) {
    // logger.warn("token is empty");
    // status.add(ConfigStatusMessage.Builder.error(CONFIG_TOKEN).withMessageKeySuffix(EMPTY_INVALID)
    // .withArguments(CONFIG_TOKEN).build());
    // }
    //
    // return status;
    // }

    @Override
    public void initialize() {
        BetterForecastThingConfig config = getConfigAs(BetterForecastThingConfig.class);

        if (config.getStationId() == 0) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                    "Station ID is missing. It is the number in your station's address on tempestwx.com.");
            return;
        }

        String token = config.getToken();
        if (token == null || token.isBlank()) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                    "Authorization token is missing. Create one at tempestwx.com > Settings > Data Authorizations.");
            return;
        }

        // the status becomes ONLINE or OFFLINE after the first fetch
        updateStatus(ThingStatus.UNKNOWN);
        logger.debug("Fetching the forecast every {}", updateInterval);
        forecastUpdateTask = scheduler.scheduleWithFixedDelay(this::fetchForecast, 5, updateInterval.toSeconds(),
                TimeUnit.SECONDS);
    }

    protected void fetchForecast() {
        BetterForecastThingConfig config = getConfigAs(BetterForecastThingConfig.class);

        Request request = httpClient.newRequest(FORECAST_URL).timeout(10, TimeUnit.SECONDS);
        request.param(CONFIG_STATION_ID, String.valueOf(config.getStationId()));
        request.param(CONFIG_TOKEN, config.getToken());

        // TODO make advanced option to select each individually
        if (!SYSTEM_OF_MEASUREMENT_METRIC.equals(config.getSystem_of_measurement())) {
            request.param("units_temp", "f");
            request.param("units_wind", "mph");
            request.param("units_pressure", "inhg");
            request.param("units_precip", "in");
            request.param("units_distance", "mi");
        }

        ContentResponse response;
        try {
            response = request.send();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        } catch (TimeoutException | ExecutionException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                    "Unable to reach WeatherFlow: " + e.getMessage());
            return;
        }

        int status = response.getStatus();
        logger.trace("Forecast response code {}", status);
        if (status == 401 || status == 403) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                    "WeatherFlow rejected the token (HTTP " + status + "). Check the token and station ID.");
            return;
        }
        if (status == 404) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                    "WeatherFlow does not know station " + config.getStationId() + " (HTTP 404).");
            return;
        }
        if (status != 200) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                    "WeatherFlow returned HTTP " + status);
            return;
        }

        String content = response.getContentAsString();
        logger.trace("Forecast response {}", content);

        BetterForecast forecast;
        try {
            forecast = gson.fromJson(content, BetterForecast.class);
            if (forecast == null || forecast.getCurrentConditions() == null) {
                throw new IllegalStateException("response has no current conditions");
            }
            forecast.enrich(config.getKeep_hourly(), config.getKeep_daily());
        } catch (RuntimeException e) {
            logger.debug("Unable to read forecast response {}", content, e);
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                    "Unexpected forecast response from WeatherFlow: " + e.getMessage());
            return;
        }

        updateStatus(ThingStatus.ONLINE);
        updateState(CHANNEL_FORECAST_RAW, new StringType(content));
        updateState(CHANNEL_FORECAST_ENRICHED, new StringType(gson.toJson(forecast)));
        updateState(CHANNEL_STATION_NAME, new StringType(forecast.getLocation_name()));

        Instant time = Instant.ofEpochSecond(forecast.getCurrentConditions().getTime());
        updateState(CHANNEL_EPOCH, new DateTimeType(ZonedDateTime.ofInstant(time, ZoneOffset.UTC)));
    }

    @Override
    public void dispose() {
        ScheduledFuture<?> task = forecastUpdateTask;
        if (task != null) {
            task.cancel(true);
        }
        forecastUpdateTask = null;
        super.dispose();
    }
}
