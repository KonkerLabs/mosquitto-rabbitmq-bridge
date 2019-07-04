package com.konkerlabs.mosquittobridge.endpoints;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;

import com.konkerlabs.mosquittobridge.gateways.RabbitGateway;

@MessageEndpoint
public class DeviceEventMqttEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceEventMqttEndpoint.class);

    private Pattern subPattern = Pattern.compile("pub/(.*?)/(.*)");
    private Pattern subDataPattern = Pattern.compile("data/(.*?)/pub/(.*)");

    @Autowired
    private RabbitGateway rabbitGateway;

    @ServiceActivator(inputChannel = "mqttEventInputChannel")
    public void onEvent(Message<byte[]> message) throws MessagingException {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("A message has arrived -> " + message.toString());
        }

        Object topic = message.getHeaders().get(MqttHeaders.TOPIC);
        if (topic == null || topic.toString().isEmpty()) {
            throw new MessagingException(message,"Topic cannot be null or empty");
        }

        String apiKey = null;
        String channel = null;
        byte[] payload = message.getPayload();

        Matcher subMatcher = subPattern.matcher(topic.toString());

        if (!subMatcher.matches()) {
            subMatcher = subDataPattern.matcher(topic.toString());
        }

        if (subMatcher.matches()) {
            apiKey = subMatcher.group(1);
            channel = subMatcher.group(2);

            rabbitGateway.publishEvent(apiKey, channel, payload);

        } else {
            LOGGER.error("Invalid topic: " + topic.toString());
        }

    }

}
