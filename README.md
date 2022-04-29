# A bit of R&D

This poc is based on [this ksqldb tutorial](https://docs.ksqldb.io/en/latest/tutorials/event-driven-microservice/)

## How to run the POC

### configure kafka and product api mockup

1) `docker-compose up -d --build` this launches the services

2) login into ksqldb-cli container and create the kafka stream (you must wait about one minute before doing this)
`docker exec -it ksqldb-cli ksql http://ksqldb-server:8088`
and copy-paste the SQL script from `./scripts/create_stream.sql` int the ksql shell

### Run the java application in intelliJ

first genereate the SerDes classes with the Avro schema : `mvn generate-sources`

then open IntelliJ, import the pom.xml and run `IsiSubscriber::main` from there

### create events (mockup)
run `./scripts/create_events.py`

## References

### debezium + postgis (pg v. 9.5)

https://github.com/52North/postgis-kafka-cdc/blob/master/postgis-debezium/Dockerfile

### SerDes avro, protobuf and json

https://docs.confluent.io/platform/current/schema-registry/serdes-develop/index.html

### commit async example

https://www.logicbig.com/tutorials/misc/kafka/kafka-manual-commit-async-example.html

### commit sync example

https://www.programcreek.com/java-api-examples/?class=org.apache.kafka.clients.consumer.KafkaConsumer&method=commitSync

### kafka config

offsets commit timeout
https://kafka.apache.org/documentation/#brokerconfigs_offsets.commit.timeout.ms
