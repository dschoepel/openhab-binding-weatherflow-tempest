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
package org.openhab.binding.weatherflowsmartweather.event;

import org.eclipse.jdt.annotation.NonNull;
import org.openhab.core.events.AbstractTypedEventSubscriber;
import org.openhab.core.events.Event;
import org.openhab.core.events.EventFilter;
import org.openhab.core.events.EventSubscriber;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = { EventSubscriber.class,
        WeatherFlowEventSubscriber.class }, configurationPid = "binding.weatherflowsmartweather")
public class WeatherFlowEventSubscriberImpl extends AbstractTypedEventSubscriber<@NonNull Event>
        implements WeatherFlowEventSubscriber {

    private final Logger logger = LoggerFactory.getLogger(WeatherFlowEventSubscriberImpl.class);

    EventFilter eventFilter = new EventFilter() {
        @Override
        public boolean apply(Event event) {
            // logger.debug("Event: {}", event);
            return false;
        }
    };

    public WeatherFlowEventSubscriberImpl() {
        super(AbstractTypedEventSubscriber.ALL_EVENT_TYPES);
        logger.debug("Starting!");
    }

    @Override
    public EventFilter getEventFilter() {
        return eventFilter;
    }

    @Override
    protected void receiveTypedEvent(Event rapidWindEvent) {
    }
}
