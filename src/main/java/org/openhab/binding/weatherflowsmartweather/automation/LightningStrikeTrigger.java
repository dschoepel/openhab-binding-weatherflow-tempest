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
package org.openhab.binding.weatherflowsmartweather.automation;

import java.util.*;

import org.openhab.binding.weatherflowsmartweather.event.LightningStrikeEvent;
import org.openhab.binding.weatherflowsmartweather.model.LightningStrikeData;
import org.openhab.core.automation.Trigger;
import org.openhab.core.automation.handler.BaseTriggerModuleHandler;
import org.openhab.core.automation.handler.TriggerHandlerCallback;
import org.openhab.core.config.core.Configuration;
import org.openhab.core.events.EventFilter;
import org.openhab.core.events.EventSubscriber;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LightningStrikeTrigger extends BaseTriggerModuleHandler implements EventSubscriber, EventFilter {

    private static final Logger log = LoggerFactory.getLogger(LightningStrikeTrigger.class);
    /**
     * This constant is used by {@link HandlerFactory} to create a correct handler instance. It must be the same as in
     * JSON definition of the module type.
     */
    public static final String UID = "LightningStrikeTrigger";
    public static final String EVENT_TOPIC = "openhab/things/{uid}/lightning_strike";

    /**
     * This constant is used to get the value of the 'skyThingUid' property from {@link Trigger}'s
     * {@link Configuration}.
     */
    private static final String AIR_UID = "sensorThingUid";

    /**
     * This constant defines the output name of this {@link Trigger} handler.
     */
    private static final String EVENT_NAME = "event";
    private static final String OUTPUT_NAME = "eventData";

    /**
     * This field will contain the sky thing's uid with which this {@link Trigger} handler is subscribed for
     * {@link Event}s.
     */
    private final String sensorThingUid;

    /**
     * A bundle's execution context within the Framework.
     */
    private final BundleContext context;

    /**
     * This field stores the service registration of this {@link Trigger} handler as {@link EventHandler} in the
     * framework.
     */
    @SuppressWarnings("rawtypes")
    private ServiceRegistration registration;
    private String topic;

    /**
     * Constructs a {@link LightningStrikeTrigger} instance.
     *
     * @param module - the {@link Trigger} for which the instance is created.
     * @param context - a bundle's execution context within the Framework.
     */
    public LightningStrikeTrigger(final Trigger module, final BundleContext context) {
        super(module);

        log.debug("Creating LightningStrikeTrigger.");
        if (module == null) {
            throw new IllegalArgumentException("'module' can not be null.");
        }
        final Configuration configuration = module.getConfiguration();
        if (configuration == null) {
            throw new IllegalArgumentException("Configuration can't be null.");
        }
        sensorThingUid = (String) configuration.get(AIR_UID);
        if (sensorThingUid == null) {
            throw new IllegalArgumentException("'airThingUid' can not be null.");
        }

        topic = EVENT_TOPIC.replace("{uid}", sensorThingUid);

        log.debug("Created for {}", sensorThingUid);

        this.context = context;

        Dictionary<String, Object> properties = new Hashtable();
        properties.put("event.topics", topic);

        this.registration = context.registerService(EventSubscriber.class, this, properties);
        log.debug("Trigger Registered EventSubscriber: Topic: {}", new Object[] { this.topic });
    }

    /**
     * This method is called from {@link EventAdmin} service.
     * It gets the value of 'value' event property, pass it to the output of this {@link Trigger} handler and notifies
     * the RuleEngine that this handler is fired.
     *
     * @param event - {@link Event} that is passed from {@link EventAdmin} service.
     */
    // @Override
    // public void handleEvent(Event event) {
    // if(true) return;
    // log.debug("Handle event: topic={}, source={}", event.getTopic(), event.getProperty("source"));
    // if(!airThingUid.equals(event.getProperty("source"))) {
    // log.warn("Got lightning strike event, but not for us...");
    // return;
    // }
    //
    // if(event.getProperty("payload") == null) {
    // log.warn("Got event without payload.");
    // return;
    // }
    //
    // Object data = event.getProperty("payload");
    //
    // if(data instanceof LightningStrikeData) {
    // log.warn("Triggering rule!");
    // final LightningStrikeData outputValue = (LightningStrikeData)data;
    // final Map<String, Object> outputProps = new HashMap<String, Object>();
    // outputProps.put(OUTPUT_NAME, outputValue);
    // ruleEngineCallback.triggered(module, outputProps);
    // } else {
    // log.warn("Got event but data not of type LightningStrikeData");
    // }
    // }

    /**
     * This method is used to set a callback object to the RuleEngine
     *
     * @param callback a callback object to the RuleEngine.
     */

    // @Override
    // public void setRuleEngineCallback(final TriggerHandlerCallback callback) {
    // ruleEngineCallback = callback;
    // log.debug("setCallback({})", callback);
    // final Dictionary<String, Object> registrationProperties = new Hashtable<String, Object>();
    // String topic = EVENT_TOPIC.replace("{uid}", airThingUid);
    // // String [] topics = {topic};
    // log.debug("topic: {}", topic);
    // registrationProperties.put(EventConstants.EVENT_TOPIC, topic);
    // registration = context.registerService(EventSubscriber.class, this, registrationProperties);
    // log.debug("registration: {}", registration);
    // }

    /**
     * This method is used to unregister this handler.
     */
    @Override
    public void dispose() {
        super.dispose();
        if (registration != null) {
            registration.unregister();
            registration = null;
        }
    }

    @Override
    public Set<String> getSubscribedEventTypes() {
        Set<String> s = new HashSet<>();
        s.add(LightningStrikeEvent.TYPE);
        return s;
    }

    @Override
    public EventFilter getEventFilter() {
        return this;
    }

    @Override
    public void receive(org.openhab.core.events.Event event) {
        log.debug("Receive oh2 event: topic={}, source={}", event.getTopic(), event.getSource());
        if (!sensorThingUid.equals(event.getSource())) {
            // log.warn("Got rapid wind event, but not for us...");
            return;
        }

        if (!topic.equals(event.getTopic())) {
            // log.trace("Got event without correct topic.");
            return;
        }

        if (event.getType() != LightningStrikeEvent.TYPE) {
            log.warn("Got event without correct type. this should not happen.");
            return;
        }

        if (this.callback != null) {
            LightningStrikeData data = ((LightningStrikeEvent) event).getLightningStrikeData();

            log.debug("Triggering rule!");
            final LightningStrikeData outputValue = (LightningStrikeData) data;
            final org.openhab.core.events.Event event1 = event;
            final Map<String, Object> outputProps = new HashMap<String, Object>();
            outputProps.put(EVENT_NAME, event1);
            outputProps.put(OUTPUT_NAME, outputValue);
            ((TriggerHandlerCallback) this.callback).triggered((Trigger) this.module, outputProps);

        } else {
            log.warn("No callback!");
        }
    }

    public boolean apply(org.openhab.core.events.Event event) {
        log.debug("->FILTER: {}:{}", event.getTopic(), topic);
        return event.getTopic().equals(topic);
    }
}
