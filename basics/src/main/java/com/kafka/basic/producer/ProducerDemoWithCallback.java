package com.kafka.basic.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerDemoWithCallback {
    private static final Logger log = LoggerFactory.getLogger(ProducerDemoWithCallback.class);

    public static void main(String[] args) throws InterruptedException {
        log.info("Producer Demo With Callback Started");
        // Create a producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(ProducerProperties.properties);

        for (int i = 0; i < 10; i++) {
            // Send a producer record
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>("demo_java", "Hello, world " + i);

            // Send data - asynchronous operation
            producer.send(producerRecord, (metadata, exception) -> {
                if (exception == null) {
                    log.info("Received new metadata: \nTopic - {} \nPartition - {} \nOffset - {}, \nTimestamp - {}, ",
                            metadata.topic(),
                            metadata.partition(),
                            metadata.offset(),
                            metadata.timestamp());
                } else {
                    log.error("Error while producing - {}", exception.getMessage());
                }
            });

            Thread.sleep(100);
        }

        // Flush and close the producer
        producer.close();
        log.info("Producer Demo With Callback Finished");
    }
}