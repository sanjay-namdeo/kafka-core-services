package com.kafka.wikimedia;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.launchdarkly.eventsource.MessageEvent;
import com.launchdarkly.eventsource.EventHandler;

public class WikimediaEventHandler implements EventHandler {
    private final Logger log = LoggerFactory.getLogger(WikimediaEventHandler.class.getSimpleName());
    private KafkaProducer<String, String> kafkaProducer;
    private String topic;

    public WikimediaEventHandler(KafkaProducer<String, String> kafkaProducer, String topic) {
        this.kafkaProducer = kafkaProducer;
        this.topic = topic;
    }

    @Override
    public void onOpen() {
    }

    @Override
    public void onClosed() {
        kafkaProducer.close();
    }

    @Override
    public void onMessage(String event, MessageEvent messageEvent) {
        log.info("MessageEvent - {}", messageEvent.getData());
        // On message event, get data and product to Kafka
        kafkaProducer.send(new ProducerRecord<String, String>(topic, messageEvent.getData()));
    }

    @Override
    public void onComment(String comment) {
    }

    @Override
    public void onError(Throwable t) {
        log.error("Error occured while message event - {}", t.getMessage());
    }
}
