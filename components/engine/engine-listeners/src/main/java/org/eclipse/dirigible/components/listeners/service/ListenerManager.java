/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.listeners.service;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.broker.region.policy.RedeliveryPolicyMap;
import org.apache.activemq.command.ActiveMQDestination;
import org.eclipse.dirigible.components.listeners.config.ActiveMQConnectionArtifactsFactory;
import org.eclipse.dirigible.components.listeners.domain.Listener;
import org.eclipse.dirigible.components.listeners.domain.ListenerKind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.jms.Connection;
import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.MessageConsumer;
import jakarta.jms.Session;

/**
 * The Class BackgroundListenerManager.
 */
public class ListenerManager {

    private static final int INITIAL_REDELIVERY_DELAY = 1000;
    private static final int REDELIVERY_DELAY = 5000;
    private static final int MAXIMUM_REDELIVERIES = 3;

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ListenerManager.class);

    /** The listener. */
    private final Listener listener;

    /** The connection artifacts factory. */
    private final ActiveMQConnectionArtifactsFactory connectionArtifactsFactory;

    /** The connection artifacts. */
    private ConnectionArtifacts connectionArtifacts;

    /**
     * Instantiates a new background listener manager.
     *
     * @param listener the listener
     * @param connectionArtifactsFactory the connection artifacts factory
     */
    public ListenerManager(Listener listener, ActiveMQConnectionArtifactsFactory connectionArtifactsFactory) {
        this.listener = listener;
        this.connectionArtifactsFactory = connectionArtifactsFactory;
    }

    /**
     * Start listener.
     */
    @SuppressWarnings("resource")
    public synchronized void startListener() {
        if (null != connectionArtifacts) {
            LOGGER.debug("Listener [{}] IS already configured", listener);
            return;
        }

        LOGGER.info("Starting a message listener for {} ...", listener);
        try {
            String handlerPath = listener.getHandler();
            ListenerExceptionHandler exceptionListener = new ListenerExceptionHandler(handlerPath);

            Connection connection = connectionArtifactsFactory.createConnection(exceptionListener);
            Session session = connectionArtifactsFactory.createSession(connection);

            Destination destination = createDestination(session);
            configureRedeliveryPolicy(connection, destination);

            MessageConsumer consumer = session.createConsumer(destination);

            AsynchronousMessageListener messageListener = new AsynchronousMessageListener(listener);
            consumer.setMessageListener(messageListener);

            connectionArtifacts = new ConnectionArtifacts(connection, session, consumer);
        } catch (JMSException ex) {
            throw new IllegalStateException("Failed to start listener for " + listener, ex);
        }
    }

    /**
     * Create destination.
     *
     * @param session the session
     * @return the destination
     * @throws JMSException the JMS exception
     */
    private Destination createDestination(Session session) throws JMSException {
        String destination = listener.getName();
        ListenerKind kind = getKind();
        return switch (kind) {
            case QUEUE -> session.createQueue(destination);
            case TOPIC -> session.createTopic(destination);
            default -> throw new IllegalArgumentException("Invalid kind: " + kind);
        };
    }

    private void configureRedeliveryPolicy(Connection connection, Destination destination) {
        if (connection instanceof ActiveMQConnection amqConnection && destination instanceof ActiveMQDestination amqDestination) {
            RedeliveryPolicy redeliveryPolicy = createRedeliveryPolicy();
            RedeliveryPolicyMap policyMap = amqConnection.getRedeliveryPolicyMap();
            policyMap.put(amqDestination, redeliveryPolicy);
        }
    }

    private RedeliveryPolicy createRedeliveryPolicy() {
        RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();

        redeliveryPolicy.setInitialRedeliveryDelay(INITIAL_REDELIVERY_DELAY);
        redeliveryPolicy.setRedeliveryDelay(REDELIVERY_DELAY);
        redeliveryPolicy.setUseExponentialBackOff(true);
        redeliveryPolicy.setMaximumRedeliveries(MAXIMUM_REDELIVERIES);

        return redeliveryPolicy;
    }

    private ListenerKind getKind() {
        ListenerKind kind = listener.getKind();
        if (null == kind) {
            throw new IllegalArgumentException("Invalid listener: " + listener + ", kind IS null");
        }
        return kind;
    }

    /**
     * Stop listener.
     */
    public synchronized void stopListener() {
        if (null == connectionArtifacts) {
            LOGGER.debug("Listener [{}] is NOT started", listener);
            return;
        }
        LOGGER.info("Stopping message listener for {} ...", listener);
        connectionArtifacts.closeAll();
        connectionArtifacts = null;
        LOGGER.info("Stopped message listener for {}", listener);
    }
}
