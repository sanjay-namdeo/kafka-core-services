package com.kafka.open.search;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.http.HttpHost;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.index.IndexResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.client.indices.CreateIndexRequest;
import org.opensearch.client.indices.CreateIndexResponse;
import org.opensearch.client.indices.GetIndexRequest;
import org.opensearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonParser;

public class OpenSearchConsumer {
    private static Logger log = LoggerFactory.getLogger(OpenSearchConsumer.class.getSimpleName());
    private static final String INDEX_NAME = "wikimedia";
    private static final String TOPIC_NAME = "wikimedia.recent.change";
    private static final String CONSUMER_GROUP_ID = "wikimedia.consumer.group";

    // Create new open search client
    private static RestHighLevelClient createOpenSearchClient() {
        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));
    }

    // Create a new search index
    private static void createIndex(RestHighLevelClient openSearchClient) throws IOException {
        log.info("createIndex Started");

        log.info("RestHighLevelClient - {}", openSearchClient);

        boolean indexExists = openSearchClient.indices().exists(new GetIndexRequest(INDEX_NAME),
                RequestOptions.DEFAULT);

        if (!indexExists) {
            // Close client after try
            try (openSearchClient) {
                CreateIndexRequest createIndexRequest = new CreateIndexRequest(INDEX_NAME);
                CreateIndexResponse createIndexResponse = openSearchClient.indices().create(createIndexRequest,
                        RequestOptions.DEFAULT);
                log.info("The topic - {} has been created with response - {}", INDEX_NAME,
                        createIndexResponse.toString());
            }
        } else {
            log.info("The topic - {} already exists.", INDEX_NAME);
        }
    }

    // Create Kafka client
    private static KafkaConsumer<String, String> getKafkaConsumer() {
        log.info("Creating a kafka consumer, topic - {}, group_id - {}", TOPIC_NAME, CONSUMER_GROUP_ID);
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_GROUP_ID);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        return new KafkaConsumer<>(properties);
    }

    public static void main(String[] args) throws IOException {
        // Create open search client
        final RestHighLevelClient openSearchClient = createOpenSearchClient();

        // Create open search index
        createIndex(openSearchClient);

        // Create Kafka consumer
        KafkaConsumer<String, String> consumer = getKafkaConsumer();
        consumer.subscribe(Collections.singleton(TOPIC_NAME));

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(3000));

            int recordCount = records.count();
            log.info("Record {} records.", recordCount);

            for (ConsumerRecord<String, String> record : records) {
                try {
                    // Pass a unique id to make records idempotent in open search => One record gets
                    // processed only once
                    IndexRequest indexRequest = new IndexRequest(INDEX_NAME)
                            .source(record.value(), XContentType.JSON)
                            .id(extractId(record.value()));

                    IndexResponse indexResponse = openSearchClient.index(indexRequest, RequestOptions.DEFAULT);

                    log.info("IndexResponse - {}", indexResponse.getId());
                } catch (Exception e) {
                    log.error("IndexResponse Exception - {}", e.getMessage());
                }
            }

            // Commit offset after batch is completed.
            consumer.commitAsync();
            log.info("Offset commited");
        }
    }

    private static String extractId(String json) {
        return JsonParser.parseString(json).getAsJsonObject().get("meta").getAsJsonObject().get("id").getAsString();
    }
}
