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

import static java.time.ZoneOffset.UTC;
import static org.openhab.binding.weatherflowsmartweather.WeatherFlowSmartWeatherBindingConstants.*;
import static org.openhab.binding.weatherflowsmartweather.WeatherFlowSmartWeatherBindingConstants.CHANNEL_EPOCH;
import static org.openhab.core.library.unit.MetricPrefix.HECTO;
import static org.openhab.core.library.unit.MetricPrefix.MILLI;

import java.net.InetAddress;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.measure.quantity.*;

import org.openhab.binding.weatherflowsmartweather.SmartWeatherEventListener;
import org.openhab.binding.weatherflowsmartweather.event.*;
import org.openhab.binding.weatherflowsmartweather.model.*;
import org.openhab.core.events.Event;
import org.openhab.core.events.EventPublisher;
import org.openhab.core.library.dimension.Intensity;
import org.openhab.core.library.types.*;
import org.openhab.core.library.unit.SIUnits;
import org.openhab.core.library.unit.Units;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * The {@link SmartWeatherTempestHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author William Welliver - Initial contribution
 */

public class SmartWeatherTempestHandler extends BaseThingHandler implements SmartWeatherEventListener {

    private final Logger logger = LoggerFactory.getLogger(SmartWeatherTempestHandler.class);

    private ScheduledFuture<?> messageTimeout;
    private Gson gson = new Gson();
    private RapidWindEventFactory rapidWindEventFactory;
    private LightningStrikeEventFactory lightningStrikeEventFactory;
    private PrecipitationStartedEventFactory precipitationStartedEventFactory;

    private EventPublisher eventPublisher;

    public SmartWeatherTempestHandler(Thing thing, RapidWindEventFactory rapidWindEventFactory,
            PrecipitationStartedEventFactory precipitationStartedEventFactory,
            LightningStrikeEventFactory lightningStrikeEventFactory, EventPublisher eventPublisher) {
        super(thing);
        this.rapidWindEventFactory = rapidWindEventFactory;
        this.lightningStrikeEventFactory = lightningStrikeEventFactory;
        this.precipitationStartedEventFactory = precipitationStartedEventFactory;
        this.eventPublisher = eventPublisher;
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

    public ScheduledFuture<?> scheduleTimeout() {

        return scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                goOffline();
            }
        }, 3, TimeUnit.MINUTES);
    }

    @Override
    public void initialize() {
        // TODO: Initialize the thing. If done set status to ONLINE to indicate proper working.
        // Long running initialization should be done asynchronously in background.
        updateStatus(ThingStatus.ONLINE);

        // Note: When initialization can NOT be done set the status with more details for further
        // analysis. See also class ThingStatusDetail for all available status details.
        // Add a description to give user information to understand why thing does not work
        // as expected. E.g.
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
        // "Can not access device as username and/or password are invalid");

        messageTimeout = scheduleTimeout();
    }

    @Override
    public void eventReceived(InetAddress source, SmartWeatherMessage data) {
        logger.debug("TempestHandler received message " + data);
        if (data instanceof StationStatusMessage || data instanceof DeviceStatusMessage) {
            logger.debug("got status message message: " + data);

            if (messageTimeout != null) {
                messageTimeout.cancel(true);
            }
            if (this.getThing().getStatus() == ThingStatus.OFFLINE) {
                goOnline();
            }
            messageTimeout = scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    goOffline();
                }
            }, 3, TimeUnit.MINUTES);

            // TODO update station status fields
        } else if (data instanceof ObservationTempestMessage) {
            handleObservationMessage((ObservationTempestMessage) data);
        } else if (data instanceof EventRapidWindMessage) {
            logger.debug("Received Rapid Wind Message.");
            handleEventRapidWindMessage((EventRapidWindMessage) data);
        } else if (data instanceof EventPrecipitationMessage) {
            logger.debug("Received Precipitation Message.");
            handleEventPrecipitationStartedMessage((EventPrecipitationMessage) data);
        } else if (data instanceof EventStrikeMessage) {
            logger.debug("Received Strike Message.");
            handleEventStrikeMessage((EventStrikeMessage) data);
        } else {
            logger.warn("not handling message: " + data);
        }
    }

    private void handleEventRapidWindMessage(EventRapidWindMessage data) {
        ThingUID uid = getThing().getUID();
        RapidWindData rapidWindData = new RapidWindData(getThing(), data);
        logger.debug("handling rapid wind record: " + rapidWindData);
        Event event = RapidWindEventFactoryImpl.createRapidWindEvent(rapidWindData);
        logger.debug("publisher: " + eventPublisher + ", event: " + event);
        eventPublisher.post(event);
    }

    private void handleEventStrikeMessage(EventStrikeMessage data) {
        ThingUID uid = getThing().getUID();
        LightningStrikeData lightningStrikeData = new LightningStrikeData(getThing(), data);
        logger.debug("handling lightning strike record: " + lightningStrikeData);
        Event event = LightningStrikeEventFactoryImpl.createLightningStrikeEvent(lightningStrikeData);
        logger.debug("publisher: " + eventPublisher + ", event: " + event);
        eventPublisher.post(event);
    }

    private void handleEventPrecipitationStartedMessage(EventPrecipitationMessage data) {
        ThingUID uid = getThing().getUID();
        PrecipitationStartedData precipitationStartedData = new PrecipitationStartedData(getThing(), data);
        logger.debug("handling precipitation record: " + precipitationStartedData);
        Event event = PrecipitationStartedEventFactoryImpl.createPrecipitionStartedEvent(precipitationStartedData);
        logger.debug("publisher: " + eventPublisher + ", event: " + event);
        eventPublisher.post(event);
    }

    public void handleObservationMessage(ObservationTempestMessage data) {
        // logger.warn("Received observation message: " + data);
        List<List> l = data.getObs();
        ThingUID uid = getThing().getUID();

        for (List obs : l) {
            logger.debug("parsing observation record: " + obs);

            String[] fields = { CHANNEL_EPOCH, CHANNEL_WIND_LULL, CHANNEL_WIND_AVG, CHANNEL_WIND_GUST,
                    CHANNEL_WIND_DIRECTION, CHANNEL_WIND_SAMPLE_INTERVAL, CHANNEL_PRESSURE, CHANNEL_TEMPERATURE,
                    CHANNEL_HUMIDITY, CHANNEL_ILLUMINANCE, CHANNEL_UV, CHANNEL_SOLAR_RADIATION,
                    CHANNEL_RAIN_ACCUMULATED, CHANNEL_PRECIPITATION_TYPE, CHANNEL_STRIKE_DISTANCE, CHANNEL_STRIKE_COUNT,
                    CHANNEL_BATTERY_LEVEL, CHANNEL_REPORT_INTERVAL };
            int i = 0;
            for (String f : fields) {
                Double val = (Double) obs.get(i++);

                State type = null;

                if (val == null)
                    type = new DecimalType(0);
                else {
                    switch (f) {
                        case CHANNEL_EPOCH:
                            type = new DateTimeType(Instant.ofEpochMilli(val.longValue() * 1000L).atZone(UTC));
                            break;
                        case CHANNEL_ILLUMINANCE:
                            type = new QuantityType<Illuminance>(val, Units.LUX);
                            break;
                        case CHANNEL_UV:
                            type = new QuantityType<Dimensionless>(val, Units.ONE);
                            break;
                        case CHANNEL_RAIN_ACCUMULATED:
                            type = new QuantityType<Length>(val, MILLI(SIUnits.METRE));
                            break;
                        case CHANNEL_WIND_LULL:
                            type = new QuantityType<Speed>(val, Units.METRE_PER_SECOND);
                            break;
                        case CHANNEL_WIND_AVG:
                            type = new QuantityType<Speed>(val, Units.METRE_PER_SECOND);
                            break;
                        case CHANNEL_WIND_GUST:
                            type = new QuantityType<Speed>(val, Units.METRE_PER_SECOND);
                            break;
                        case CHANNEL_WIND_DIRECTION:
                            type = new QuantityType<Angle>(val, Units.DEGREE_ANGLE);
                            break;
                        case CHANNEL_BATTERY_LEVEL:
                            type = new QuantityType<ElectricPotential>(val, Units.VOLT);
                            break;
                        case CHANNEL_REPORT_INTERVAL:
                            type = new QuantityType<Time>(val * 60, Units.SECOND);
                            break;
                        case CHANNEL_SOLAR_RADIATION:
                            type = new QuantityType<Intensity>(val, Units.IRRADIANCE);
                            break;
                        case CHANNEL_LOCAL_DAY_RAIN_ACCUMULATION:
                            type = new QuantityType<Length>(val, MILLI(SIUnits.METRE));
                            break;
                        case CHANNEL_PRECIPITATION_TYPE:
                            type = new StringType("" + val.intValue());
                            break;
                        case CHANNEL_WIND_SAMPLE_INTERVAL:
                            type = new QuantityType<Time>(val, Units.SECOND);
                            break;
                        case CHANNEL_PRESSURE:
                            type = new QuantityType<Pressure>(val, HECTO(SIUnits.PASCAL));
                            break;
                        case CHANNEL_TEMPERATURE:
                            type = new QuantityType<Temperature>(val, SIUnits.CELSIUS);
                            break;
                        case CHANNEL_HUMIDITY:
                            type = new QuantityType<Dimensionless>(val, Units.PERCENT);
                            break;
                        case CHANNEL_STRIKE_COUNT:
                            type = new DecimalType(val);
                            break;
                        case CHANNEL_STRIKE_DISTANCE:
                            type = new QuantityType<Length>(val, SIUnits.METRE.multiply(1000.0));
                            break;
                        default:
                            logger.info("Received unknown field " + f + " with value " + val);
                    }
                }

                if (type != null) {
                    logger.debug("posting type = " + type);
                    updateState(new ChannelUID(uid, f), type);
                } else {
                    logger.warn("passed through without a type to update.");
                }
            }
        }
    }

    private void goOnline() {
        this.updateStatus(ThingStatus.ONLINE);
        messageTimeout = null;
    }

    protected void goOffline() {
        this.updateStatus(ThingStatus.OFFLINE);
        messageTimeout = null;
    }
}
