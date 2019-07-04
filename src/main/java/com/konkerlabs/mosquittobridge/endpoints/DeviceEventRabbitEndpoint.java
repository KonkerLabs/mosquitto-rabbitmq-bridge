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
public class DeviceEventRabbitEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceEventRabbitEndpoint.class);

    private static final String MQTT_OUTGOING_TOPIC_TEMPLATE = "sub/{0}/{1}";

    private static final String MQTT_OUTGOING_TOPIC_DATA_TEMPLATE = "data/{0}/sub/{1}";

    @Autowired
    private MqttMessageGateway mqttMessageGateway;

    @RabbitListener(queues = "data.sub")
    public void processDataSub(Message message) {

        String apiKey = (String) message.getMessageProperties().getHeaders().get(RabbitMQConfig.MSG_HEADER_APIKEY);
        String channel = (String) message.getMessageProperties().getHeaders().get(RabbitMQConfig.MSG_HEADER_CHANNEL);
        byte[] payload = message.getBody();

        // pub
        String destinationTopic = MessageFormat.format(MQTT_OUTGOING_TOPIC_TEMPLATE, apiKey, channel);
        mqttMessageGateway.send(payload, destinationTopic);

        // data/pub
        String destinationDataTopic = MessageFormat.format(MQTT_OUTGOING_TOPIC_DATA_TEMPLATE, apiKey, channel);
        mqttMessageGateway.send(payload, destinationDataTopic);
        LOGGER.info("Message sent to device {}", apiKey);
    }

}
