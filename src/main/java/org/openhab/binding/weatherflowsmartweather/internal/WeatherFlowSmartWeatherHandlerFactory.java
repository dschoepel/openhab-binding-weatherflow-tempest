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

import static org.openhab.binding.weatherflowsmartweather.WeatherFlowSmartWeatherBindingConstants.*;

import java.util.*;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.weatherflowsmartweather.WeatherFlowSmartWeatherBindingConstants;
import org.openhab.binding.weatherflowsmartweather.event.LightningStrikeEventFactory;
import org.openhab.binding.weatherflowsmartweather.event.PrecipitationStartedEventFactory;
import org.openhab.binding.weatherflowsmartweather.event.RapidWindEventFactory;
import org.openhab.binding.weatherflowsmartweather.handler.*;
import org.openhab.core.config.core.Configuration;
import org.openhab.core.config.discovery.DiscoveryService;
import org.openhab.core.events.EventPublisher;
import org.openhab.core.io.net.http.HttpClientFactory;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.binding.BaseThingHandlerFactory;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link WeatherFlowSmartWeatherHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author William Welliver - Initial contribution
 */

@NonNullByDefault
@Component(service = ThingHandlerFactory.class, configurationPid = "binding.weatherflowsmartweather")
public class WeatherFlowSmartWeatherHandlerFactory extends BaseThingHandlerFactory {

    protected static final Logger logger = LoggerFactory.getLogger(WeatherFlowSmartWeatherHandlerFactory.class);

    static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = WeatherFlowSmartWeatherBindingConstants.SUPPORTED_THING_TYPES;
    private Map<ThingUID, ServiceRegistration<?>> discoveryServiceRegs = new HashMap<>();

    @Nullable
    SmartWeatherUDPListenerService udpListener;
    @Nullable
    private RapidWindEventFactory rapidWindEventFactory;
    @Nullable
    private PrecipitationStartedEventFactory precipitationStartedEventFactory;
    @Nullable
    LightningStrikeEventFactory lightningStrikeEventFactory;
    @Nullable
    EventPublisher eventPublisher;
    @Nullable
    HttpClientFactory httpClientFactory;

    public WeatherFlowSmartWeatherHandlerFactory() {
        logger.debug("Creating WeatherFlowSmartWeatherHandlerFactory.");
    }

    @Reference
    protected void setUdpListener(SmartWeatherUDPListenerService service) {
        udpListener = service;
    };

    protected void unsetUdpListener(Object service) {
        udpListener = null;
    };

    @Reference
    protected void setRapidWindEventFactory(RapidWindEventFactory factory) {
        rapidWindEventFactory = factory;
    }

    protected void unsetRapidWindEventFactory(RapidWindEventFactory factory) {
        rapidWindEventFactory = null;
    }

    @Reference
    protected void setLightningStrikeEventFactory(LightningStrikeEventFactory factory) {
        lightningStrikeEventFactory = factory;
    };

    protected void unsetLightningStrikeEventFactory(LightningStrikeEventFactory factory) {
        lightningStrikeEventFactory = null;
    };

    @Reference
    protected void setPrecipitationStartedEventFactory(PrecipitationStartedEventFactory factory) {
        precipitationStartedEventFactory = factory;
    };

    protected void unsetLightningStrikeEventFactory(PrecipitationStartedEventFactory factory) {
        precipitationStartedEventFactory = null;
    };

    @Reference()
    protected void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    protected void unsetEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = null;
    }

    @Reference
    protected void setHttpClientFactory(HttpClientFactory httpClientFactory) {
        this.httpClientFactory = httpClientFactory;
    }

    protected void unsetHttpClientFactory(HttpClientFactory httpClientFactory) {
        this.httpClientFactory = null;
    }

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        logger.debug("Creating handler for thing={}", thingTypeUID);

        if (thingTypeUID.equals(THING_TYPE_SMART_WEATHER_HUB)) {
            SmartWeatherHubHandler hubHandler = new SmartWeatherHubHandler((Bridge) thing, udpListener);
            registerDeviceDiscoveryService(hubHandler);
            return hubHandler;
        } else if (thingTypeUID.equals(THING_TYPE_SMART_WEATHER_TEMPEST)) {
            return new SmartWeatherTempestHandler(thing, rapidWindEventFactory, precipitationStartedEventFactory,
                    lightningStrikeEventFactory, eventPublisher);
        } else if (thingTypeUID.equals(THING_TYPE_SMART_WEATHER_BETTER_FORECAST)) {
            HttpClientFactory clientFactory = httpClientFactory;
            if (clientFactory == null) {
                logger.warn("HTTP client factory is not available, cannot create {}", thing.getUID());
                return null;
            }
            return new SmartWeatherBetterForecastHandler(thing, clientFactory.getCommonHttpClient());
        } else {
            logger.warn("No handler for thingTypeUID={}", thingTypeUID);
            return null;
        }
    }

    @Override
    protected @Nullable Thing createThing(ThingTypeUID thingTypeUID, Configuration configuration, ThingUID thingUID) {
        logger.debug("Creating thing for thing={}", thingTypeUID);
        logger.debug("ThingType: {}", getThingTypeByUID(thingTypeUID));
        return super.createThing(thingTypeUID, configuration, thingUID);
    }

    @Override
    public @Nullable Thing createThing(ThingTypeUID thingTypeUID, Configuration configuration,
            @Nullable ThingUID thingUID, @Nullable ThingUID bridgeUID) {
        logger.debug("Creating thing for thing={}", thingTypeUID);
        logger.debug("ThingType: {}", getThingTypeByUID(thingTypeUID));
        return super.createThing(thingTypeUID, configuration, thingUID, bridgeUID);
    }

    private synchronized void registerDeviceDiscoveryService(SmartWeatherHubHandler hubHandler) {
        logger.debug("Registering Device Discovery Service {}", hubHandler);
        SmartWeatherStationDiscoveryService discoveryService = new SmartWeatherStationDiscoveryService(udpListener,
                hubHandler);
        discoveryService.start();
        this.discoveryServiceRegs.put(hubHandler.getThing().getUID(), bundleContext
                .registerService(DiscoveryService.class.getName(), discoveryService, new Hashtable<String, Object>()));
    }

    @Override
    protected synchronized void removeHandler(ThingHandler thingHandler) {
        if (thingHandler instanceof SmartWeatherHubHandler) {
            ServiceRegistration<?> serviceReg = this.discoveryServiceRegs.get(thingHandler.getThing().getUID());
            if (serviceReg != null) {
                // remove discovery service, if bridge handler is removed
                SmartWeatherStationDiscoveryService service = (SmartWeatherStationDiscoveryService) bundleContext
                        .getService(serviceReg.getReference());
                service.stop();
                serviceReg.unregister();
                discoveryServiceRegs.remove(thingHandler.getThing().getUID());
            }
        }
    }
}
