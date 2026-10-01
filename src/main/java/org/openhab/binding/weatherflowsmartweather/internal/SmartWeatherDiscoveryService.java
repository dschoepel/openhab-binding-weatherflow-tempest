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
import static org.openhab.binding.weatherflowsmartweather.WeatherFlowSmartWeatherBindingConstants.THING_TYPE_SMART_WEATHER_HUB;

import java.net.InetAddress;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.openhab.binding.weatherflowsmartweather.SmartWeatherEventListener;
import org.openhab.binding.weatherflowsmartweather.model.HubStatusMessage;
import org.openhab.binding.weatherflowsmartweather.model.SmartWeatherMessage;
import org.openhab.core.config.discovery.AbstractDiscoveryService;
import org.openhab.core.config.discovery.DiscoveryResult;
import org.openhab.core.config.discovery.DiscoveryResultBuilder;
import org.openhab.core.config.discovery.DiscoveryService;
import org.openhab.core.thing.ThingUID;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Discovers SmartWeather hubs from the status messages they broadcast over UDP.
 *
 * Each hub is reported to the inbox once per background session or manual scan. The hub broadcasts a status
 * message every few seconds, so reporting on every message would keep the inbox busy for no reason.
 *
 * @author William Welliver - Initial contribution
 */
@Component(service = DiscoveryService.class, immediate = true, configurationPid = "binding.weatherflowsmartweather", name = "org.openhab.binding.weatherflowsmartweather.discovery.hub")
public class SmartWeatherDiscoveryService extends AbstractDiscoveryService implements SmartWeatherEventListener {

    private final Logger logger = LoggerFactory.getLogger(SmartWeatherDiscoveryService.class);

    private static final int SCAN_TIMEOUT_SECONDS = 30;

    private final Set<String> reportedSerials = ConcurrentHashMap.newKeySet();

    protected SmartWeatherUDPListenerService udpListener;

    @Reference()
    protected void bindUdpListener(SmartWeatherUDPListenerService service) {
        udpListener = service;
    };

    protected void unbindUdpListener(SmartWeatherUDPListenerService service) {
        udpListener = null;
    };

    public SmartWeatherDiscoveryService() {
        super(Set.of(THING_TYPE_SMART_WEATHER_HUB), SCAN_TIMEOUT_SECONDS, true);
    }

    @Override
    protected void startScan() {
        // a manual scan reports every hub again, in case one was removed from the inbox
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
        if (!(data instanceof HubStatusMessage message)) {
            return;
        }

        String serial = message.getSerial_number();
        if (serial == null || !reportedSerials.add(serial)) {
            return;
        }

        ThingUID thingUid = new ThingUID(THING_TYPE_SMART_WEATHER_HUB, serial);
        logger.debug("Discovered SmartWeather hub {}", thingUid);

        DiscoveryResult result = DiscoveryResultBuilder.create(thingUid).withLabel("SmartWeather Hub")
                .withProperty(PROPERTY_SERIAL_NUMBER, serial).withRepresentationProperty(PROPERTY_SERIAL_NUMBER)
                .build();
        thingDiscovered(result);
    }
}
