# Kafka Core Services
1. Start zookeeper
```bash
./zookeeper-server-start.sh -daemon /opt/kafka/config/zookeeper.properties
```
2. Start kafka
```bash
./kafka-server-start.sh -daemon /opt/kafka/config/server.properties
```
3. Create a topic
```bash
/opt/kafka/bin/kafka-topics.sh --create --topic demo_java --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
```