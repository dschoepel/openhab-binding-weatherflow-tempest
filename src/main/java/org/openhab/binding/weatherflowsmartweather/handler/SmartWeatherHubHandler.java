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

import java.net.InetAddress;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.weatherflowsmartweather.SmartWeatherEventListener;
import org.openhab.binding.weatherflowsmartweather.WeatherFlowSmartWeatherBindingConstants;
import org.openhab.binding.weatherflowsmartweather.internal.SmartWeatherUDPListenerService;
import org.openhab.binding.weatherflowsmartweather.model.*;
import org.openhab.core.library.types.DateTimeType;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.thing.*;
import org.openhab.core.thing.binding.BaseBridgeHandler;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SmartWeatherHubHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author William Welliver - Initial contribution
 */

public class SmartWeatherHubHandler extends BaseBridgeHandler implements SmartWeatherEventListener {

    private final Logger logger = LoggerFactory.getLogger(SmartWeatherHubHandler.class);

    protected SmartWeatherUDPListenerService udpListener;

    private ScheduledFuture<?> messageTimeout;

    public SmartWeatherHubHandler(Bridge bridge, SmartWeatherUDPListenerService udpListener) {
        super(bridge);
        this.udpListener = udpListener;
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // we don't really have any commands to handle.
    }

    @Override
    public void initialize() {
        if (udpListener != null) {
            udpListener.registerListener(this);
            updateStatus(ThingStatus.ONLINE);

        } else {
            updateStatus(ThingStatus.OFFLINE);
        }
    }

    @Nullable
    public Thing getThingByUID(ThingUID uid) {
        Bridge bridge = this.getThing();
        List<Thing> things = bridge.getThings();
        Iterator thingIterator = things.iterator();

        while (thingIterator.hasNext()) {
            Thing thing = (Thing) thingIterator.next();
            if (thing.getUID().equals(uid)) {
                return thing;
            }
        }

        return null;
    }

    @Override
    public void eventReceived(InetAddress source, SmartWeatherMessage data) {
        String serial = data.getSerial_number();

        if (serial != null && serial.equals(
                this.getThing().getProperties().get(WeatherFlowSmartWeatherBindingConstants.PROPERTY_SERIAL_NUMBER))) {
            // logger.warn("Bridge Got message!");

            if (data instanceof HubStatusMessage) {
                try {
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

                    handleHubStatusMessage((HubStatusMessage) data);
                } catch (Exception e) {
                    logger.warn("Got exception", e);
                }
            }
        } else if (serial != null) { // TODO need a better approach to this
            if (data instanceof EventRapidWindMessage) {
                EventRapidWindMessage message = (EventRapidWindMessage) data;
                String serialNumber = message.getSerial_number();

                ThingTypeUID deviceType = thingTypeUidFromSerial(serialNumber);
                ThingUID thingUid = new ThingUID(deviceType, getThing().getUID(), serialNumber);

                Thing t = this.getThingByUID(thingUid);
                if (t == null) {
                    logger.debug("rapid wind observation but not for us.");
                    return;
                } // not our hub and sensor combo.
                SmartWeatherEventListener handler = (SmartWeatherEventListener) t.getHandler();
                handler.eventReceived(source, message);
            } else if (data instanceof ObservationAirQualityMessage) {
                ObservationAirQualityMessage message = (ObservationAirQualityMessage) data;
                String serialNumber = message.getSerial_number();

                ThingTypeUID deviceType = thingTypeUidFromSerial(serialNumber);
                ThingUID thingUid = new ThingUID(deviceType, getThing().getUID(), serialNumber);

                Thing t = this.getThingByUID(thingUid);
                if (t == null) {
                    logger.debug("airquality observation but not for us.");
                    return;
                } // not our hub and sensor combo.
                SmartWeatherEventListener handler = (SmartWeatherEventListener) t.getHandler();
                handler.eventReceived(source, message);
            } else if (data instanceof ObservationAirMessage) {
                ObservationAirMessage message = (ObservationAirMessage) data;
                String serialNumber = message.getSerial_number();

                ThingTypeUID deviceType = thingTypeUidFromSerial(serialNumber);
                ThingUID thingUid = new ThingUID(deviceType, getThing().getUID(), serialNumber);

                Thing t = this.getThingByUID(thingUid);
                if (t == null) {
                    logger.debug("air observation but not for us.");
                    return;
                } // not our hub and sensor combo.
                SmartWeatherEventListener handler = (SmartWeatherEventListener) t.getHandler();
                handler.eventReceived(source, message);
            } else if (data instanceof ObservationSkyMessage) {
                ObservationSkyMessage message = (ObservationSkyMessage) data;
                String serialNumber = message.getSerial_number();

                ThingTypeUID deviceType = thingTypeUidFromSerial(serialNumber);
                ThingUID thingUid = new ThingUID(deviceType, getThing().getUID(), serialNumber);

                Thing t = this.getThingByUID(thingUid);
                if (t == null) {
                    logger.debug("sky observation but not for us.");
                    return;
                } // not our hub and sensor combo.
                SmartWeatherEventListener handler = (SmartWeatherEventListener) t.getHandler();
                handler.eventReceived(source, message);
            } else if (data instanceof ObservationTempestMessage) {
                ObservationTempestMessage message = (ObservationTempestMessage) data;
                String serialNumber = message.getSerial_number();

                ThingTypeUID deviceType = thingTypeUidFromSerial(serialNumber);
                ThingUID thingUid = new ThingUID(deviceType, getThing().getUID(), serialNumber);

                Thing t = this.getThingByUID(thingUid);
                if (t == null) {
                    logger.debug("tempest observation but not for us.");
                    return;
                } // not our hub and sensor combo.
                SmartWeatherEventListener handler = (SmartWeatherEventListener) t.getHandler();
                handler.eventReceived(source, message);
            } else if (data instanceof EventStrikeMessage) {
                EventStrikeMessage message = (EventStrikeMessage) data;
                String serialNumber = message.getSerial_number();

                ThingTypeUID deviceType = thingTypeUidFromSerial(serialNumber);
                ThingUID thingUid = new ThingUID(deviceType, getThing().getUID(), serialNumber);

                Thing t = this.getThingByUID(thingUid);
                if (t == null) {
                    logger.debug("event strike message but not for us.");
                    return;
                } // not our hub and sensor combo.
                SmartWeatherEventListener handler = (SmartWeatherEventListener) t.getHandler();
                handler.eventReceived(source, message);
            } else if (data instanceof EventPrecipitationMessage) {
                EventPrecipitationMessage message = (EventPrecipitationMessage) data;
                String serialNumber = message.getSerial_number();

                ThingTypeUID deviceType = thingTypeUidFromSerial(serialNumber);
                ThingUID thingUid = new ThingUID(deviceType, getThing().getUID(), serialNumber);

                Thing t = this.getThingByUID(thingUid);
                if (t == null) {
                    logger.debug("event precipitation message but not for us.");
                    return;
                } // not our hub and sensor combo.
                SmartWeatherEventListener handler = (SmartWeatherEventListener) t.getHandler();
                handler.eventReceived(source, message);
            } else if (data instanceof DeviceStatusMessage) {
                DeviceStatusMessage message = (DeviceStatusMessage) data;
                String serialNumber = message.getSerial_number();

                ThingTypeUID deviceType = thingTypeUidFromSerial(serialNumber);
                ThingUID thingUid = new ThingUID(deviceType, getThing().getUID(), serialNumber);

                Thing t = this.getThingByUID(thingUid);
                if (t == null) {
                    logger.debug("device status but not for us: " + thingUid);
                    return;
                } // not our hub and sensor combo.
                SmartWeatherEventListener handler = (SmartWeatherEventListener) t.getHandler();
                handler.eventReceived(source, message);
            } else if (data instanceof StationStatusMessage) {
                StationStatusMessage message = (StationStatusMessage) data;
                String serialNumber = message.getSerial_number();

                ThingTypeUID deviceType = thingTypeUidFromSerial(serialNumber);
                ThingUID thingUid = new ThingUID(deviceType, getThing().getUID(), serialNumber);

                Thing t = this.getThingByUID(thingUid);
                if (t == null) {
                    logger.debug("station status but not for us.");
                    return;
                } // not our hub and sensor combo.
                SmartWeatherEventListener handler = (SmartWeatherEventListener) t.getHandler();
                handler.eventReceived(source, message);
            }
        }
    }

    private ThingTypeUID thingTypeUidFromSerial(String serialNumber) {
        if (serialNumber.startsWith("SK"))
            return THING_TYPE_SMART_WEATHER_SKY;
        else if (serialNumber.startsWith("AR"))
            return THING_TYPE_SMART_WEATHER_AIR;
        else if (serialNumber.startsWith("AQ"))
            return THING_TYPE_SMART_WEATHER_AIRQUALITY;
        else if (serialNumber.startsWith("ST"))
            return THING_TYPE_SMART_WEATHER_TEMPEST;
        return null;
    }

    // wonder if perhaps the refresh rate on this data may be too high by default... do we really need to
    // update this information multiple times a minute?
    private void handleHubStatusMessage(HubStatusMessage data) {
        updateState(new ChannelUID(getThing().getUID(), CHANNEL_RSSI), new DecimalType(data.getRssi()));
        updateState(new ChannelUID(getThing().getUID(), CHANNEL_FIRMWARE_VERSION),
                new StringType(data.getFirmware_version()));
        updateState(new ChannelUID(getThing().getUID(), CHANNEL_LAST_REPORT),
                new DateTimeType(Instant.ofEpochMilli(data.getTimestamp() * 1000L).atZone(UTC)));
        updateState(new ChannelUID(getThing().getUID(), CHANNEL_UPTIME), new DecimalType(data.getUptime()));

        // TODO Does it make sense to include the new fields from the v30 status message? Mostly debug info, it seems.
    }

    private void goOnline() {
        this.updateStatus(ThingStatus.ONLINE);
        messageTimeout = null;
    }

    protected void goOffline() {
        this.updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_OFFLINE);
        messageTimeout = null;
    }

    @Override
    public void dispose() {
        if (udpListener != null) {
            udpListener.unregisterListener(this);
        }
        super.dispose();
    }
}
