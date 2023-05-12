package com.kafka.basic.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerDemo {
    private static final Logger log = LoggerFactory.getLogger(ProducerDemo.class);

    public static void main(String[] args) {
        log.info("Producer Demo Started");
        // Create a producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(ProducerProperties.PROPERTIES);

        // Send a producer record
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>("demo_java", "Hello, world!");

        // Send data - asynchronous operation
        producer.send(producerRecord);

        // Flush and close => close method does both
        producer.close();

        log.info("Producer Demo Finished");
    }
}
