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

## Installation
1. Update the System

```bash
sudo yum update
```
2. Install Java Development Kit (JDK). Kafka requires Java to run, so you need to install the JDK, if not already installed.

```bash
sudo yum install java-1.8.0-openjdk
```
3. Download and Extract Kafka. You can download the Kafka binaries from the Apache Kafka website. Choose the version you want to install and replace VERSION_NUMBER in the following command with the appropriate version number.

```bash
wget https://downloads.apache.org/kafka/VERSION_NUMBER/kafka_2.12-VERSION_NUMBER.tgz
tar -xzf kafka_2.12-VERSION_NUMBER.tgz
```
4. Open the config/server.properties file and add the following:

```java
# Listners and Advertisers
listeners=PLAINTEXT://<host_name>:9092,SASL_PLAINTEXT://<host_name>:9093,SASL_SSL://<host_name>:9094
advertised.listeners=PLAINTEXT://<host_name>:9092,SASL_PLAINTEXT://<host_name>:9093,SASL_SSL://<host_name>:9094

# SSL Changes
ssl.keystore.location=/data/kafka/secrets/kafka.jks
ssl.keystore.password=password
ssl.key.password=password
ssl.truststore.location=/data/kafka/secrets/kafka.jks
ssl.truststore.password=password
ssl.client.auth=required
security.inter.broker.protocol=SASL_SSL

# SASL Configurations
security.inter.broker.protocol=SASL_PLAINTEXT
sasl.enabled.mechanisms=PLAIN
sasl.mechanism.inter.broker.protocol=PLAIN
sasl.mechanism=PLAIN
```
5. Create a file config/kafka_server_jaas.conf

```bash
KafkaServer {
    org.apache.kafka.common.security.plain.PlainLoginModule required
    username="kafka"
    password="pass123"
    user_kafka="pass123";
};
```
6. Create a file /etc/systemd/system/kafka.service with the following contents

```bash
Description=Apache Kafka Service
After=zookeeper.service

[Service]
Type=simple
User=ubuntu
ExecStart=/bin/bash -c "export KAFKA_OPTS='-Djava.security.auth.login.config=/data/kafka/kafka_2.12-2.8.2/config/kafka_server_jaas.conf'; /data/kafka/kafka_2.12-2.8.2/bin/kafka-server-start.sh /data/kafka/kafka_2.12-2.8.2/config/server.properties"
Restart=always
RestartSec=3

[Install]
WantedBy=default.target
```
7. Create a file /etc/systemd/system/zookeeper.service with the following contents

```bash
[Unit]
Description=ZooKeeper Service
After=network.target

[Service]
Type=simple
User=ubuntu
ExecStart=/data/kafka/kafka_2.12-2.8.2/bin/zookeeper-server-start.sh /data/kafka/kafka_2.12-2.8.2/config/zookeeper.properties
Restart=always
RestartSec=3

[Install]
WantedBy=default.target
```
8. Enable and start the services

```bash
sudo systemctl enable kafka
sudo systemctl enable zookeeper

sudo systemctl start kafka
sudo systemctl start zookeeper
```
9. Check the status

```bash
sudo systemctl status kafka
sudo systemctl status zookeeper
```
## Steps to generate self signed certificates
1. Generate Key

```bash
openssl genrsa -des3 -out server.key 2048
```
2. Generate CSR

```bash
openssl req -key server.key -new -out server.csr
```
3. Create certificate

```bash
openssl x509 -signkey server.key -in server.csr -req -days 365 -out kafka.crt
openssl x509 -signkey server.key -in server.csr -req -days 365 -out client.crt
```
4. Create root certificate

```bash
openssl req -x509 -sha256 -days 1825 -newkey rsa:2048 -keyout rootCA.key -out rootCA.crt
```
5. Create server.ext

```bash
authorityKeyIdentifier=keyid,issuer
basicConstraints=CA:FALSE
subjectAltName = @alt_names
[alt_names]
DNS.1 = localhost
```
6. Sign certificates with root certificate

```bash
openssl x509 -req -CA rootCA.crt -CAkey rootCA.key -in server.csr -out client.crt -days 365 -CAcreateserial -extfile server.ext
openssl x509 -req -CA rootCA.crt -CAkey rootCA.key -in server.csr -out kafka.crt -days 365 -CAcreateserial -extfile server.ext
```
7. Create PKS and import certificate

```bash
openssl pkcs12 -export -in kafka.crt -inkey server.key -name 'kafka' -out kafka.p12
openssl pkcs12 -export -in client.crt -inkey server.key -name 'client' -out client.p12
```
8. Convert P12 to JKS

```bash
keytool -importkeystore -srckeystore kafka.p12 -srcstoretype pkcs12 -srcalias kafka -destkeystore kafka.jks -deststoretype jks -deststorepass password -destalias kafka
keytool -importkeystore -srckeystore client.p12 -srcstoretype pkcs12 -srcalias client -destkeystore client.jks -deststoretype jks -deststorepass password -destalias client
```
9. Import trust certificates

```bash
keytool -import -alias kafka -file kafka.crt -storetype JKS -keystore client.jks
keytool -import -alias client -file client.crt -storetype JKS -keystore kafka.jks
```
10. Check keystore, each should have one private key and one trust certificate

```bash
keytool -v -list -keystore client.jks
keytool -v -list -keystore kafka.jks
```