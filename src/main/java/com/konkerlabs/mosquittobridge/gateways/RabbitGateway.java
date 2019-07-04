package com.konkerlabs.mosquittobridge.gateways;

import java.io.UnsupportedEncodingException;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.konkerlabs.mosquittobridge.config.RabbitMQConfig;

@Service
public class RabbitGateway {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitGateway.class);

    public void publishEvent(String apiKey, String channel, byte[] payload) {

        try {
            Instant now = Instant.now();

            MessageProperties properties = new MessageProperties();
            properties.setHeader(RabbitMQConfig.MSG_HEADER_APIKEY, apiKey);
            properties.setHeader(RabbitMQConfig.MSG_HEADER_CHANNEL, channel);
            properties.setHeader(RabbitMQConfig.MSG_HEADER_TIMESTAMP, now.toEpochMilli());

            Message message = new Message(payload, properties);

            rabbitTemplate.convertAndSend("data.pub", message);
            LOGGER.info("Message added in queue by device {}", apiKey);
        } catch (AmqpException ex) {
            LOGGER.error("AmqpException while sending message to RabbitMQ...", ex);
        }

    }

    public void publishConfigRequest(String apiKey, byte[] config) {

        try {
            MessageProperties properties = new MessageProperties();
            properties.setHeader(RabbitMQConfig.MSG_HEADER_APIKEY, apiKey);

            Message message = new Message(config, properties);

            rabbitTemplate.convertAndSend("mgmt.config.pub", message);
            LOGGER.info("Config added in queue by device {}", apiKey);
        } catch (AmqpException ex) {
            LOGGER.error("AmqpException while sending message to RabbitMQ...", ex);
        }

    }


}
