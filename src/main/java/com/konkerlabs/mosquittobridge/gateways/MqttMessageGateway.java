package com.konkerlabs.mosquittobridge.gateways;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;

@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel",
                  defaultReplyTimeout = "1")
public interface MqttMessageGateway {

    String send(byte[] message, @Header(MqttHeaders.TOPIC) String topic);

}
