package com.konkerlabs.mosquittobridge.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import lombok.Data;

@Data
@Configuration
public class MqttPubConfig {

    private String[] publishUris;
    private String publishUsername;
    private String publishPassword;

    public MqttPubConfig() {
        Map<String, Object> defaultMap = new HashMap<>();
        defaultMap.put("mqtt.publish.uris", Collections.singleton("tcp://localhost:1883"));
        defaultMap.put("mqtt.publish.username", "user");
        defaultMap.put("mqtt.publish.password", "pass");

        Config defaultConf = ConfigFactory.parseMap(defaultMap);
        Config config = ConfigFactory.load().withFallback(defaultConf);

        setPublishUris(config.getStringList("mqtt.publish.uris").toArray(new String[] {}));
        setPublishUsername(config.getString("mqtt.publish.username"));
        setPublishPassword(config.getString("mqtt.publish.password"));
    }

    private MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setServerURIs(getPublishUris());
        factory.setUserName(getPublishUsername());
        factory.setPassword(getPublishPassword());
        return factory;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(
                UUID.randomUUID().toString(),
                mqttClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setCompletionTimeout(5000);
        messageHandler.setDefaultTopic("testTopic");
        return messageHandler;
    }

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

}