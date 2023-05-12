package com.kafka.basic.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Properties;

public class ConsumerProperties {
    public static final Properties PROPERTIES = new Properties();
    public static final String TOPIC = "demo_java";

    static {
        // Create producer properties
        PROPERTIES.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        PROPERTIES.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        PROPERTIES.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        PROPERTIES.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "java-demo");
        PROPERTIES.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    }
}
