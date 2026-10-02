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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.weatherflowsmartweather.SmartWeatherEventListener;
import org.openhab.binding.weatherflowsmartweather.model.SmartWeatherDeserializer;
import org.openhab.binding.weatherflowsmartweather.model.SmartWeatherMessage;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Listens for the JSON messages WeatherFlow hubs broadcast on UDP port 50222 and hands each one to the
 * registered listeners (hub handlers and discovery services).
 *
 * One socket and one reader thread serve the whole binding. The thread blocks in {@link DatagramSocket#receive}
 * and ends when the socket is closed on deactivation.
 *
 * @author William Welliver - Initial contribution
 */
@NonNullByDefault
@Component(service = SmartWeatherUDPListenerService.class, immediate = true, configurationPid = "binding.weatherflowsmartweather")
public class SmartWeatherUDPListenerServiceImpl implements SmartWeatherUDPListenerService {

    private static final int WEATHERFLOW_PORT = 50222;
    private static final int BUFFER_SIZE = 4096;
    private static final String THREAD_NAME = "OH-binding-weatherflowsmartweather-udp";

    private final Logger logger = LoggerFactory.getLogger(SmartWeatherUDPListenerServiceImpl.class);

    // a set, so registering twice (background discovery plus a manual scan) has no effect
    private final Set<SmartWeatherEventListener> listeners = new CopyOnWriteArraySet<>();

    private final Gson gson;

    private @Nullable DatagramSocket socket;
    private @Nullable Thread readerThread;

    public SmartWeatherUDPListenerServiceImpl() {
        SmartWeatherDeserializer deserializer = new SmartWeatherDeserializer();
        gson = new GsonBuilder().registerTypeAdapter(SmartWeatherMessage.class, deserializer).create();
    }

    @Activate
    protected void activate() {
        DatagramSocket newSocket;
        try {
            newSocket = new DatagramSocket(null);
            // other programs (such as the WeatherFlow desktop app) may listen on the same port
            newSocket.setReuseAddress(true);
            newSocket.bind(new InetSocketAddress(WEATHERFLOW_PORT));
        } catch (SocketException e) {
            logger.warn("Unable to listen for WeatherFlow broadcasts on UDP port {}: {}", WEATHERFLOW_PORT,
                    e.getMessage());
            return;
        }

        socket = newSocket;
        Thread thread = new Thread(() -> receiveLoop(newSocket), THREAD_NAME);
        thread.setDaemon(true);
        readerThread = thread;
        thread.start();
        logger.debug("Listening for WeatherFlow broadcasts on UDP port {}", WEATHERFLOW_PORT);
    }

    @Deactivate
    protected void deactivate() {
        DatagramSocket oldSocket = socket;
        socket = null;
        if (oldSocket != null) {
            // closing the socket makes the blocked receive() throw, which ends the reader thread
            oldSocket.close();
        }

        Thread oldThread = readerThread;
        readerThread = null;
        if (oldThread != null) {
            try {
                oldThread.join(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        listeners.clear();
    }

    private void receiveLoop(DatagramSocket datagramSocket) {
        byte[] buffer = new byte[BUFFER_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        while (!datagramSocket.isClosed()) {
            try {
                packet.setLength(buffer.length);
                datagramSocket.receive(packet);
                String data = new String(packet.getData(), packet.getOffset(), packet.getLength(),
                        StandardCharsets.UTF_8);
                logger.trace("Received from {}: {}", packet.getAddress(), data);
                processMessage(packet.getAddress(), data);
            } catch (IOException e) {
                if (!datagramSocket.isClosed()) {
                    logger.debug("Error receiving WeatherFlow broadcast: {}", e.getMessage());
                }
            }
        }
        logger.debug("Stopped listening for WeatherFlow broadcasts");
    }

    private void processMessage(@Nullable InetAddress source, String data) {
        SmartWeatherMessage message;
        try {
            message = gson.fromJson(data, SmartWeatherMessage.class);
        } catch (RuntimeException e) {
            // JsonParseException, or an unexpected message layout the deserializer can't handle
            logger.debug("Unable to parse WeatherFlow message {}: {}", data, e.getMessage());
            return;
        }
        if (message == null) {
            return;
        }

        for (SmartWeatherEventListener listener : listeners) {
            try {
                listener.eventReceived(source, message);
            } catch (RuntimeException e) {
                // one failing listener must not stop the others or the reader thread
                logger.warn("Error handling WeatherFlow message in {}", listener.getClass().getSimpleName(), e);
            }
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
