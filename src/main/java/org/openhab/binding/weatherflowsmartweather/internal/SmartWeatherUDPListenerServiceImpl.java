package org.openhab.binding.weatherflowsmartweather.internal;

import java.net.InetAddress;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.openhab.binding.weatherflowsmartweather.SmartWeatherEventListener;
import org.openhab.binding.weatherflowsmartweather.model.SmartWeatherDeserializer;
import org.openhab.binding.weatherflowsmartweather.model.SmartWeatherMessage;
import org.openhab.binding.weatherflowsmartweather.util.UdpServer;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Component(service = SmartWeatherUDPListenerService.class, immediate = true, configurationPid = "binding.weatherflowsmartweather")
public class SmartWeatherUDPListenerServiceImpl implements SmartWeatherUDPListenerService {

    Logger logger = LoggerFactory.getLogger(SmartWeatherUDPListenerServiceImpl.class);
    // a set, so registering twice (background discovery plus a manual scan) has no effect
    Set<SmartWeatherEventListener> listeners = new CopyOnWriteArraySet<>();
    UdpServer us;
    final int weatherFlowPort = 50222;

    public void SmartWeatherUDPListenerService() {
        logger.warn("Starting SmartWeather UDP Listener on port " + weatherFlowPort);
    }

    void setupServer() {
        us = new UdpServer();
        us.setPort(weatherFlowPort);
        us.addUdpServerListener(new UdpServer.Listener() {
            @Override
            public void packetReceived(UdpServer.Event evt) { // Packet received
                logger.debug("received message: " + evt.getPacketAsString());
                processMessage(evt.getPacket().getAddress(), evt.getPacketAsString());
            }
        }); // end Listener
    }

    @Activate
    void start() {
        setupServer();
        us.start();
    }

    @Deactivate
    void stop() {
        us.stop();
    }

    Gson gson = new GsonBuilder().registerTypeAdapter(SmartWeatherMessage.class, new SmartWeatherDeserializer())
            .create();

    private void processMessage(InetAddress source, String data) {
        SmartWeatherMessage message = null;
        // logger.warn("Parsing message data for " + listeners.size() + " listeners.");

        try {
            message = gson.fromJson(data, SmartWeatherMessage.class);
        } catch (Exception e) {
            logger.error("Unable to parse message. ", e);
        }

        if (message == null)
            return;

        logger.debug("Sending message " + message + " for  " + listeners.size() + " listeners.");
        for (SmartWeatherEventListener listener : listeners) {
            listener.eventReceived(source, message);
        }
    }

    @Override
    public void registerListener(SmartWeatherEventListener listener) {
        listeners.add(listener);
    }

    @Override
    public void unregisterListener(SmartWeatherEventListener listener) {
        listeners.remove(listener);
    }
}
