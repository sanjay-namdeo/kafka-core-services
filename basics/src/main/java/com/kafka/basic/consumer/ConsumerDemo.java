package com.kafka.basic.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class ConsumerDemo {
    private static final Logger log = LoggerFactory.getLogger(ConsumerDemo.class);

    public static void main(String[] args) {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(ConsumerProperties.PROPERTIES);

        consumer.subscribe(Collections.singletonList(ConsumerProperties.TOPIC));

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);

            for (ConsumerRecord<String, String> record : records) {
                log.info("Key- {}, Value- {}, Partition- {}, Offset- {}", record.key(), record.value(), record.partition(), record.offset());
            }
        }
    }
}
