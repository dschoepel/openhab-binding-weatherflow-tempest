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
package org.openhab.binding.weatherflowsmartweather.internal;

import static org.openhab.binding.weatherflowsmartweather.WeatherFlowSmartWeatherBindingConstants.PROPERTY_SERIAL_NUMBER;

import java.net.InetAddress;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.openhab.binding.weatherflowsmartweather.SmartWeatherEventListener;
import org.openhab.binding.weatherflowsmartweather.WeatherFlowSmartWeatherBindingConstants;
import org.openhab.binding.weatherflowsmartweather.handler.SmartWeatherHubHandler;
import org.openhab.binding.weatherflowsmartweather.model.DeviceStatusMessage;
import org.openhab.binding.weatherflowsmartweather.model.SmartWeatherMessage;
import org.openhab.binding.weatherflowsmartweather.model.StationStatusMessage;
import org.openhab.core.config.discovery.AbstractDiscoveryService;
import org.openhab.core.config.discovery.DiscoveryResult;
import org.openhab.core.config.discovery.DiscoveryResultBuilder;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.ThingUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Discovers the Tempest sensors attached to one hub, from the status messages they
 * broadcast over UDP.
 *
 * Each sensor is reported to the inbox once per background session or manual scan, and sensors that already
 * exist as Things under the hub are skipped.
 *
 * @author William Welliver - Initial contribution
 */
public class SmartWeatherStationDiscoveryService extends AbstractDiscoveryService implements SmartWeatherEventListener {

    private final Logger logger = LoggerFactory.getLogger(SmartWeatherStationDiscoveryService.class);

    private static final int SCAN_TIMEOUT_SECONDS = 30;

    private final Set<String> reportedSerials = ConcurrentHashMap.newKeySet();

    private final SmartWeatherUDPListenerService udpListener;

    private final SmartWeatherHubHandler hubHandler;

    public SmartWeatherStationDiscoveryService(SmartWeatherUDPListenerService udpListener,
            SmartWeatherHubHandler hubHandler) {
        super(Set.of(WeatherFlowSmartWeatherBindingConstants.THING_TYPE_SMART_WEATHER_TEMPEST), SCAN_TIMEOUT_SECONDS,
                true);
        this.udpListener = udpListener;
        this.hubHandler = hubHandler;
    }

    /**
     * Called by the handler factory after the service is registered.
     */
    public void start() {
        if (isBackgroundDiscoveryEnabled()) {
            startBackgroundDiscovery();
        }
    }

    /**
     * Called by the handler factory before the service is unregistered.
     */
    public void stop() {
        udpListener.unregisterListener(this);
    }

    @Override
    protected void startScan() {
        // a manual scan reports every sensor again, in case one was removed from the inbox
        reportedSerials.clear();
        udpListener.registerListener(this);
    }

    @Override
    protected synchronized void stopScan() {
        super.stopScan();
        if (!isBackgroundDiscoveryEnabled()) {
            udpListener.unregisterListener(this);
        }
    }

    @Override
    protected void startBackgroundDiscovery() {
        udpListener.registerListener(this);
    }

    @Override
    protected void stopBackgroundDiscovery() {
        udpListener.unregisterListener(this);
    }

    @Override
    public void eventReceived(InetAddress source, SmartWeatherMessage data) {
        String serial;
        String hubSerial;

        if (data instanceof StationStatusMessage message) {
            serial = message.getSerial_number();
            hubSerial = message.getHub_sn();
        } else if (data instanceof DeviceStatusMessage message) {
            serial = message.getSerial_number();
            hubSerial = message.getHub_sn();
        } else {
            return;
        }

        if (serial == null || reportedSerials.contains(serial)) {
            return;
        }

        // is this sensor attached to this hub?
        if (hubSerial == null || !hubSerial.equals(hubHandler.getThing().getProperties().get(PROPERTY_SERIAL_NUMBER))) {
            return;
        }

        // only the Tempest is supported
        if (!serial.startsWith("ST")) {
            return;
        }
        String label = "SmartWeather Tempest";
        ThingTypeUID thingType = WeatherFlowSmartWeatherBindingConstants.THING_TYPE_SMART_WEATHER_TEMPEST;

        reportedSerials.add(serial);

        ThingUID thingUid = new ThingUID(thingType, hubHandler.getThing().getUID(), serial);
        if (hubHandler.getThingByUID(thingUid) != null) {
            logger.trace("{} already exists, not reporting it", thingUid);
            return;
        }

        DiscoveryResult result = DiscoveryResultBuilder.create(thingUid).withLabel(label)
                .withBridge(hubHandler.getThing().getUID()).withProperty(PROPERTY_SERIAL_NUMBER, serial)
                .withRepresentationProperty(PROPERTY_SERIAL_NUMBER).build();
        logger.debug("Discovered {} {}", label, serial);
        thingDiscovered(result);
    }
}
