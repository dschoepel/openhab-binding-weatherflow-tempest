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
import org.openhab.core.events.EventPublisher;
import org.openhab.core.library.dimension.Intensity;
import org.openhab.core.library.types.*;
import org.openhab.core.library.unit.SIUnits;
import org.openhab.core.library.unit.Units;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.ThingStatusInfo;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.openhab.core.types.UnDefType;
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

    private static final int MESSAGE_TIMEOUT_MINUTES = 3;

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
        // all channels are read-only
    }

    @Override
    public void initialize() {
        // the Tempest reports a status message every minute; the status becomes ONLINE when the first one arrives
        updateStatus(ThingStatus.UNKNOWN);
        restartMessageTimeout();
    }

    @Override
    public void bridgeStatusChanged(ThingStatusInfo bridgeStatusInfo) {
        if (bridgeStatusInfo.getStatus() == ThingStatus.OFFLINE) {
            cancelMessageTimeout();
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_OFFLINE);
        } else if (bridgeStatusInfo.getStatus() == ThingStatus.ONLINE) {
            updateStatus(ThingStatus.UNKNOWN);
            restartMessageTimeout();
        }
    }

    @Override
    public void dispose() {
        cancelMessageTimeout();
        super.dispose();
    }

    /**
     * Marks the Tempest OFFLINE if no status message arrives within {@link #MESSAGE_TIMEOUT_MINUTES}.
     */
    private synchronized void restartMessageTimeout() {
        cancelMessageTimeout();
        messageTimeout = scheduler.schedule(() -> updateStatus(ThingStatus.OFFLINE,
                ThingStatusDetail.COMMUNICATION_ERROR,
                "No data received from the Tempest for " + MESSAGE_TIMEOUT_MINUTES + " minutes"),
                MESSAGE_TIMEOUT_MINUTES, TimeUnit.MINUTES);
    }

    private synchronized void cancelMessageTimeout() {
        ScheduledFuture<?> timeout = messageTimeout;
        if (timeout != null) {
            timeout.cancel(false);
        }
        messageTimeout = null;
    }

    @Override
    public void eventReceived(InetAddress source, SmartWeatherMessage data) {
        logger.trace("Tempest received message {}", data);
        if (data instanceof StationStatusMessage || data instanceof DeviceStatusMessage) {
            if (getThing().getStatus() != ThingStatus.ONLINE) {
                updateStatus(ThingStatus.ONLINE);
            }
            restartMessageTimeout();
            // TODO update station status fields
        } else if (data instanceof ObservationTempestMessage message) {
            handleObservationMessage(message);
        } else if (data instanceof EventRapidWindMessage message) {
            handleEventRapidWindMessage(message);
        } else if (data instanceof EventPrecipitationMessage message) {
            handleEventPrecipitationStartedMessage(message);
        } else if (data instanceof EventStrikeMessage message) {
            handleEventStrikeMessage(message);
        } else {
            logger.debug("Ignoring message {}", data);
        }
    }

    private void handleEventRapidWindMessage(EventRapidWindMessage data) {
        RapidWindData rapidWindData = new RapidWindData(getThing(), data);
        logger.trace("Posting rapid wind event {}", rapidWindData);
        eventPublisher.post(RapidWindEventFactoryImpl.createRapidWindEvent(rapidWindData));
    }

    private void handleEventStrikeMessage(EventStrikeMessage data) {
        LightningStrikeData lightningStrikeData = new LightningStrikeData(getThing(), data);
        logger.debug("Posting lightning strike event {}", lightningStrikeData);
        eventPublisher.post(LightningStrikeEventFactoryImpl.createLightningStrikeEvent(lightningStrikeData));
    }

    private void handleEventPrecipitationStartedMessage(EventPrecipitationMessage data) {
        PrecipitationStartedData precipitationStartedData = new PrecipitationStartedData(getThing(), data);
        logger.debug("Posting precipitation started event {}", precipitationStartedData);
        eventPublisher.post(
                PrecipitationStartedEventFactoryImpl.createPrecipitionStartedEvent(precipitationStartedData));
    }

    public void handleObservationMessage(ObservationTempestMessage data) {
        List<List> l = data.getObs();
        ThingUID uid = getThing().getUID();

        for (List obs : l) {
            logger.trace("Parsing observation record {}", obs);

            String[] fields = { CHANNEL_EPOCH, CHANNEL_WIND_LULL, CHANNEL_WIND_AVG, CHANNEL_WIND_GUST,
                    CHANNEL_WIND_DIRECTION, CHANNEL_WIND_SAMPLE_INTERVAL, CHANNEL_PRESSURE, CHANNEL_TEMPERATURE,
                    CHANNEL_HUMIDITY, CHANNEL_ILLUMINANCE, CHANNEL_UV, CHANNEL_SOLAR_RADIATION,
                    CHANNEL_RAIN_ACCUMULATED, CHANNEL_PRECIPITATION_TYPE, CHANNEL_STRIKE_DISTANCE, CHANNEL_STRIKE_COUNT,
                    CHANNEL_BATTERY_LEVEL, CHANNEL_REPORT_INTERVAL };
            int i = 0;
            for (String f : fields) {
                Double val = (Double) obs.get(i++);

                State type = null;

                if (val == null) {
                    // the Tempest sends null for values it could not measure
                    type = UnDefType.UNDEF;
                } else {
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
                            logger.debug("Received unknown field {} with value {}", f, val);
                    }
                }

                if (type != null) {
                    updateState(new ChannelUID(uid, f), type);
                }
            }
        }
    }

}
