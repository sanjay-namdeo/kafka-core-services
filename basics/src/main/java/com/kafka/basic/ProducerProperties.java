package com.kafka.basic;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class ProducerProperties {
    public static final Properties properties = new Properties();

    static {
        // Create producer properties
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "punevmsinvrs:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.PARTITIONER_ADPATIVE_PARTITIONING_ENABLE_CONFIG, "true");
    }
}