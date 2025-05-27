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
import static org.openhab.core.library.unit.MetricPrefix.HECTO;

import java.net.InetAddress;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.measure.quantity.*;

import org.openhab.binding.weatherflowsmartweather.SmartWeatherEventListener;
import org.openhab.binding.weatherflowsmartweather.event.LightningStrikeEventFactory;
import org.openhab.binding.weatherflowsmartweather.event.LightningStrikeEventFactoryImpl;
import org.openhab.binding.weatherflowsmartweather.model.*;
import org.openhab.core.events.Event;
import org.openhab.core.events.EventPublisher;
import org.openhab.core.library.types.DateTimeType;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.QuantityType;
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
 * The {@link SmartWeatherAirHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author William Welliver - Initial contribution
 */

public class SmartWeatherAirHandler extends BaseThingHandler implements SmartWeatherEventListener {

    private final Logger logger = LoggerFactory.getLogger(SmartWeatherAirHandler.class);

    private Gson gson = new Gson();
    private ScheduledFuture<?> messageTimeout;

    private LightningStrikeEventFactory lightningStrikeEventFactory;
    private EventPublisher eventPublisher;

    public SmartWeatherAirHandler(Thing thing, LightningStrikeEventFactory lightningStrikeEventFactory,
            EventPublisher eventPublisher) {
        super(thing);
        this.lightningStrikeEventFactory = lightningStrikeEventFactory;
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

    // wonder if perhaps the refresh rate on this data may be too high by default... do we really need to
    // update this information multiple times a minute?

    @Override
    public void eventReceived(InetAddress source, SmartWeatherMessage data) {
        if (data instanceof StationStatusMessage || data instanceof DeviceStatusMessage) {
            logger.debug("got status message message: " + data);

            if (messageTimeout != null) {
                messageTimeout.cancel(true);
            }
            if (this.getThing().getStatus() == ThingStatus.OFFLINE) {
                goOnline();
            }
            messageTimeout = scheduleTimeout();

            // TODO update station status fields
        } else if (data instanceof ObservationAirMessage) {
            // EventStrikeMessage m = new EventStrikeMessage();
            // m.setSerial_number(((ObservationAirMessage) data).getSerial_number());
            // m.setFirmware_revision(((ObservationAirMessage) data).getFirmware_revision());
            // m.setHub_sn(((ObservationAirMessage) data).getHub_sn());
            // m.setSerial_number(data.getSerial_number());
            // m.setEvt(new Object[] { 0.0d, 1.0d, 1.2d });
            // logger.debug("Handling Strike Event");
            // handleEventStrikeMessage(m);
            handleObservationMessage((ObservationAirMessage) data);
        } else if (data instanceof EventStrikeMessage) {
            logger.debug("Received Strike Message.");
            handleEventStrikeMessage((EventStrikeMessage) data);
        } else {
            logger.warn("not handling message: " + data);
        }
    }
    /*
     * private void handleEventStrikeMessage(EventStrikeMessage data) {
     * ThingUID uid = getThing().getUID();
     * String s = gson.toJson(data);
     * updateState(new ChannelUID(uid, CHANNEL_STRIKE_EVENTS), new RawType(s.getBytes(), MIME_TYPE_JSON));
     * }
     */

    private void handleEventStrikeMessage(EventStrikeMessage data) {
        ThingUID uid = getThing().getUID();
        LightningStrikeData lightningStrikeData = new LightningStrikeData(getThing(), data);
        logger.debug("handling lightning strike record: " + lightningStrikeData);
        Event event = LightningStrikeEventFactoryImpl.createLightningStrikeEvent(lightningStrikeData);
        logger.debug("publisher: " + eventPublisher + ", event: " + event);
        eventPublisher.post(event);
    }

    public void handleObservationMessage(ObservationAirMessage data) {
        // logger.warn("Received observation message: " + data);
        List<List> l = data.getObs();
        ThingUID uid = getThing().getUID();

        for (List obs : l) {
            // logger.info("parsing observation record: " + obs);

            String[] fields = { CHANNEL_EPOCH, CHANNEL_PRESSURE, CHANNEL_TEMPERATURE, CHANNEL_HUMIDITY,
                    CHANNEL_STRIKE_COUNT, CHANNEL_STRIKE_DISTANCE, CHANNEL_BATTERY_LEVEL };
            int i = 0;
            for (String f : fields) {
                Double val = (Double) obs.get(i++);
                State type = null;
                switch (f) {
                    case CHANNEL_EPOCH:
                        type = new DateTimeType(Instant.ofEpochMilli(val.longValue() * 1000).atZone(UTC));
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
                    case CHANNEL_BATTERY_LEVEL:
                        type = new QuantityType<ElectricPotential>(val, Units.VOLT);
                        break;
                    default:
                        logger.info("Received unknown field " + f + " with value " + val);
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
