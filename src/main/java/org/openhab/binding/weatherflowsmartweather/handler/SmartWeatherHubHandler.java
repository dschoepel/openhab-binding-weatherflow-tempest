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

    private final SmartWeatherUDPListenerService udpListener;

    private static final int MESSAGE_TIMEOUT_MINUTES = 3;

    private @Nullable ScheduledFuture<?> messageTimeout;

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
        // the hub broadcasts a status message every few seconds; the status becomes ONLINE when the first one arrives
        updateStatus(ThingStatus.UNKNOWN);
        udpListener.registerListener(this);
        restartMessageTimeout();
    }

    @Override
    public void dispose() {
        udpListener.unregisterListener(this);
        cancelMessageTimeout();
        super.dispose();
    }

    /**
     * Marks the hub OFFLINE if no status message arrives within {@link #MESSAGE_TIMEOUT_MINUTES}.
     */
    private synchronized void restartMessageTimeout() {
        cancelMessageTimeout();
        messageTimeout = scheduler.schedule(
                () -> updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                        "No data received from the hub for " + MESSAGE_TIMEOUT_MINUTES + " minutes"),
                MESSAGE_TIMEOUT_MINUTES, TimeUnit.MINUTES);
    }

    private synchronized void cancelMessageTimeout() {
        ScheduledFuture<?> timeout = messageTimeout;
        if (timeout != null) {
            timeout.cancel(false);
        }
        messageTimeout = null;
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
        if (serial == null) {
            return;
        }

        if (serial.equals(getThing().getProperties().get(PROPERTY_SERIAL_NUMBER))) {
            if (data instanceof HubStatusMessage message) {
                if (getThing().getStatus() != ThingStatus.ONLINE) {
                    updateStatus(ThingStatus.ONLINE);
                }
                restartMessageTimeout();
                handleHubStatusMessage(message);
            }
            return;
        }

        forwardToSensor(source, serial, data);
    }

    /**
     * Passes a sensor message to the handler of the matching child Thing, if it exists under this hub and is enabled.
     */
    private void forwardToSensor(InetAddress source, String serial, SmartWeatherMessage data) {
        ThingTypeUID deviceType = thingTypeUidFromSerial(serial);
        if (deviceType == null) {
            logger.trace("Ignoring message from unsupported device {}", serial);
            return;
        }

        Thing thing = getThingByUID(new ThingUID(deviceType, getThing().getUID(), serial));
        if (thing == null) {
            // a sensor on another hub, or one that has not been added as a Thing
            logger.trace("No Thing for device {} under this hub", serial);
            return;
        }

        // the handler is null while the Thing is disabled or still initializing
        if (thing.getHandler() instanceof SmartWeatherEventListener listener) {
            listener.eventReceived(source, data);
        }
    }

    private @Nullable ThingTypeUID thingTypeUidFromSerial(String serialNumber) {
        // only the Tempest is supported
        return serialNumber.startsWith("ST") ? THING_TYPE_SMART_WEATHER_TEMPEST : null;
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
}
