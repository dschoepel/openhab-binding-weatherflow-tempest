package org.openhab.binding.weatherflowsmartweather.automation;

import java.util.*;

import org.openhab.binding.weatherflowsmartweather.event.RapidWindEvent;
import org.openhab.binding.weatherflowsmartweather.model.RapidWindData;
import org.openhab.core.automation.Trigger;
import org.openhab.core.automation.handler.BaseTriggerModuleHandler;
import org.openhab.core.automation.handler.TriggerHandlerCallback;
import org.openhab.core.config.core.Configuration;
import org.openhab.core.events.Event;
import org.openhab.core.events.EventFilter;
import org.openhab.core.events.EventSubscriber;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RapidWindTrigger extends BaseTriggerModuleHandler implements EventFilter, EventSubscriber {

    private static final Logger log = LoggerFactory.getLogger(RapidWindTrigger.class);
    /**
     * This constant is used by {@link HandlerFactory} to create a correct handler instance. It must be the same as in
     * JSON definition of the module type.
     */
    public static final String UID = "RapidWindTrigger";
    public static final String EVENT_TOPIC = "openhab/things/{uid}/rapid_wind";

    /**
     * This constant is used to get the value of the 'skyThingUid' property from {@link Trigger}'s
     * {@link Configuration}.
     */
    private static final String SENSOR_UID = "sensorThingUid";

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
     * This field stores the service registration of this {@link Trigger} handler as {@link EventSubscriber} in the
     * framework.
     */
    @SuppressWarnings("rawtypes")
    private ServiceRegistration registration;
    private String topic;

    /**
     * Constructs a {@link RapidWindTrigger} instance.
     *
     * @param module - the {@link Trigger} for which the instance is created.
     * @param context - a bundle's execution context within the Framework.
     */
    public RapidWindTrigger(final Trigger module, final BundleContext context) {
        super(module);

        log.debug("Creating RapidWindTrigger.");
        if (module == null) {
            throw new IllegalArgumentException("'module' can not be null.");
        }
        final Configuration configuration = module.getConfiguration();
        if (configuration == null) {
            throw new IllegalArgumentException("Configuration can't be null.");
        }
        sensorThingUid = (String) configuration.get(SENSOR_UID);
        if (sensorThingUid == null) {
            throw new IllegalArgumentException("'skyThingUid' can not be null.");
        }

        topic = EVENT_TOPIC.replace("{uid}", sensorThingUid);

        log.debug("Created for " + sensorThingUid + ".");

        this.context = context;

        Dictionary<String, Object> properties = new Hashtable();
        properties.put("event.topics", this.topic);

        registration = this.context.registerService(EventSubscriber.class, this, properties);
        log.info("Trigger Registered EventSubscriber: Topic: {}, Properties: {}, Registration: {}",
                new Object[] { topic }, properties, registration);
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
    // log.warn("Handle event: topic=" + event.getTopic() + ", source=" + event.getProperty("source") + ".");
    // if(!skyThingUid.equals(event.getProperty("source"))) {
    // log.warn("Got rapid wind event, but not for us...");
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
    // if(data instanceof RapidWindData) {
    // log.warn("Triggering rule!");
    // final RapidWindData outputValue = (RapidWindData)data;
    // final Map<String, Object> outputProps = new HashMap<String, Object>();
    // outputProps.put(OUTPUT_NAME, outputValue);
    // ruleEngineCallback.triggered(module, outputProps);
    // } else {
    // log.warn("Got event but data not of type RapidWindData");
    // }
    // }

    /**
     * This method is used to set a callback object to the RuleEngine
     *
     * @param callback a callback object to the RuleEngine.
     */
    // public void setRuleEngineCallback(final TriggerHandlerCallback callback) {
    // ruleEngineCallback = callback;
    // log.warn("setCallback(" + callback + ")");
    // final Dictionary<String, Object> registrationProperties = new Hashtable<String, Object>();
    // String topic = EVENT_TOPIC.replace("{uid}", skyThingUid);
    // // String [] topics = {topic};
    // log.warn("topic: " + topic);
    // registrationProperties.put(EventConstants.EVENT_TOPIC, topic);
    // registration = context.registerService(EventSubscriber.class, this, registrationProperties);
    // log.warn("registration: " + registration);
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
        s.add(ALL_EVENT_TYPES);
        return s;
    }

    @Override
    public EventFilter getEventFilter() {
        return this;
    }

    public String getTopic() {
        return this.topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    public void receive(Event event) {
        log.debug("Receive oh2 event: topic=" + event.getTopic() + ", source=" + event.getSource() + ".");
        if (!sensorThingUid.equals(event.getSource())) {
            // log.warn("Got rapid wind event, but not for us...");
            return;
        }

        if (!topic.equals(event.getTopic())) {
            log.warn("Got event without correct topic.");
            return;
        }

        log.debug("EventType: " + event.getType() + ", expected " + RapidWindEvent.TYPE);
        if (event.getType() != RapidWindEvent.TYPE) {
            log.warn("Got event without correct type. this should not happen.");
            return;
        }

        RapidWindData data = ((RapidWindEvent) event).getRapidWindData();

        if (this.callback != null) {
            log.debug("Triggering rule!");
            final Event event1 = event;
            final RapidWindData outputValue = (RapidWindData) data;
            final Map<String, Object> outputProps = new HashMap<String, Object>();
            outputProps.put(EVENT_NAME, event1);
            outputProps.put(OUTPUT_NAME, outputValue);

            ((TriggerHandlerCallback) this.callback).triggered((Trigger) this.module, outputProps);
        } else {
            log.warn("No callback!");
        }
    }

    public boolean apply(Event event) {
        log.debug("->FILTER: {}:{}", event.getTopic(), topic);
        return event.getTopic().equals(topic);
    }
}
