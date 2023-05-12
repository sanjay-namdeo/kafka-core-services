package com.kafka.basic.producer;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerDemo {
    private static final Logger log = LoggerFactory.getLogger(ProducerDemo.class);
    public static final Properties PROPERTIES = new Properties();
    private static final String TOPIC = "demo_java";

    static {
        // Create producer properties
        PROPERTIES.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        PROPERTIES.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        PROPERTIES.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        PROPERTIES.setProperty(ProducerConfig.PARTITIONER_ADPATIVE_PARTITIONING_ENABLE_CONFIG, "true");
    }

    public static void main(String[] args) {
        log.info("Producer Demo Started");
        // Create a producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(PROPERTIES);

        // Send a producer record
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(TOPIC, "Hello, world!");

        // Send data - asynchronous operation
        producer.send(producerRecord);

        // Flush and close => close method does both
        producer.close();

        log.info("Producer Demo Finished");
    }
}
