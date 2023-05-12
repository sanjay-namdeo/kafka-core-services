package com.kafka.basic.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;

public class ConsumerDemo {
    private static final Logger log = LoggerFactory.getLogger(ConsumerDemo.class);

    public static void main(String[] args) {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(ConsumerProperties.PROPERTIES);

        consumer.subscribe(Collections.singletonList(ConsumerProperties.TOPIC));

        final Thread mainThread = Thread.currentThread();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                log.info("Detected a shutdown, let exit by calling consumer.wakeup()");

                // Calling wakeup on a consumer causes poll method an WakeupException
                consumer.wakeup();

                // join the main thread to allow the execution of the code in the main thread
                try {
                    mainThread.join();
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
            }
        });

        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, String> record : records) {
                    log.info("Key- {}, Value- {}, Partition- {}, Offset- {}", record.key(), record.value(),
                            record.partition(), record.offset());
                }
            }
        } catch (WakeupException ignored) {
            log.info("Wakeup exception");
        } catch (Exception exception) {
            log.error("Unexpected exceptions");
        } finally {
            // Closing consumer also commits the offset
            consumer.close();
            log.info("Consumer is now gracefully closed");
        }
    }
}
