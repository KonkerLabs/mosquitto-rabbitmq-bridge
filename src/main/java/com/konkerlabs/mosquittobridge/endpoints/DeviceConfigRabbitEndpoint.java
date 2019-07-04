package com.konkerlabs.mosquittobridge.endpoints;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.konkerlabs.mosquittobridge.config.RabbitMQConfig;
import com.konkerlabs.mosquittobridge.gateways.MqttMessageGateway;

@Service
public class DeviceConfigRabbitEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceConfigRabbitEndpoint.class);

    private static final String MQTT_OUTGOING_TEMPLATE = "mgmt/{0}/sub/cfg";

    @Autowired
    private MqttMessageGateway mqttMessageGateway;

    @RabbitListener(queues = "mgmt.config.sub")
    public void processConfigSub(Message message) {

        String apiKey = (String) message.getMessageProperties().getHeaders().get(RabbitMQConfig.MSG_HEADER_APIKEY);
        byte[] payload = message.getBody();

        String destinationTopic = MessageFormat.format(MQTT_OUTGOING_TEMPLATE, apiKey);
        mqttMessageGateway.send(payload, destinationTopic);

        LOGGER.info("Config sent to device {}", apiKey);
    }

}
