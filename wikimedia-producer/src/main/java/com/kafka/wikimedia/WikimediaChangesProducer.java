package com.kafka.wikimedia;

import java.net.URI;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;

public class WikimediaChangesProducer {
    private static final Logger log = LoggerFactory.getLogger(WikimediaChangesProducer.class);
    public static final Properties PROPERTIES = new Properties();
    private static final String TOPIC = "wikimedia.recent.change";

    static {
        // Create producer properties
        PROPERTIES.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        PROPERTIES.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        PROPERTIES.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        PROPERTIES.setProperty(ProducerConfig.PARTITIONER_ADPATIVE_PARTITIONING_ENABLE_CONFIG, "true");
    }

    public static void main(String[] args) throws InterruptedException {
        KafkaProducer<String, String> producer = new KafkaProducer<>(PROPERTIES);

        // Create event handler to produce a message to Kafka, on new events
        EventHandler eventHandler = new WikimediaEventHandler(producer, TOPIC);

        // Create event source to read messages from the specified url
        EventSource eventSource = new EventSource.Builder(eventHandler,
                URI.create("https://stream.wikimedia.org/v2/stream/recentchange")).build();

        // Start event source in another thread.
        log.info("Started event source");
        eventSource.start();

        TimeUnit.MINUTES.sleep(10);
    }
}
