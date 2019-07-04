package com.konkerlabs.mosquittobridge.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import lombok.Data;

@Data
@Configuration
public class MqttSubConfig {

    private String[] subcribeUris;
    private String subcribeUsername;
    private String subcribePassword;

    @Autowired
    private Executor executor;

    public MqttSubConfig() {
        Map<String, Object> defaultMap = new HashMap<>();
        defaultMap.put("mqtt.subcribe.uris", Collections.singleton("tcp://localhost:1883"));
        defaultMap.put("mqtt.subcribe.username", "user");
        defaultMap.put("mqtt.subcribe.password", "pass");

        Config defaultConf = ConfigFactory.parseMap(defaultMap);
        Config config = ConfigFactory.load().withFallback(defaultConf);

        setSubcribeUris(config.getStringList("mqtt.subcribe.uris").toArray(new String[] {}));
        setSubcribeUsername(config.getString("mqtt.subcribe.username"));
        setSubcribePassword(config.getString("mqtt.subcribe.password"));
    }

    private MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setServerURIs(getSubcribeUris());
        factory.setUserName(getSubcribeUsername());
        factory.setPassword(getSubcribePassword());
        return factory;
    }

    ///////////////////////////// DATA

    @Bean
    public MessageChannel mqttEventInputChannel() {
        return new ExecutorChannel(executor);
    }

    @Bean
    public MessageProducer eventInbound() {

        String topics[] = { "pub/+/+", "data/+/pub/+" };

        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                UUID.randomUUID().toString(),
                mqttClientFactory(),
                topics
                );
        adapter.setCompletionTimeout(5000);
        DefaultPahoMessageConverter pahoMessageConverter = new DefaultPahoMessageConverter();
        pahoMessageConverter.setPayloadAsBytes(true);
        adapter.setConverter(pahoMessageConverter);
        adapter.setQos(1);
        adapter.setOutputChannel(mqttEventInputChannel());

        return adapter;

    }

    ///////////////////////////// MGMT

    @Bean
    public MessageChannel mqttConfigInputChannel() {
        return new ExecutorChannel(executor);
    }

    @Bean
    public MessageProducer configInbound() {

        String topics[] = { "mgmt/+/pub/cfg" };

        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                UUID.randomUUID().toString(),
                mqttClientFactory(),
                topics
                );
        adapter.setCompletionTimeout(5000);
        DefaultPahoMessageConverter pahoMessageConverter = new DefaultPahoMessageConverter();
        pahoMessageConverter.setPayloadAsBytes(true);
        adapter.setConverter(pahoMessageConverter);
        adapter.setQos(1);
        adapter.setOutputChannel(mqttConfigInputChannel());

        return adapter;

    }

}