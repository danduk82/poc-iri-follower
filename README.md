# A bit of R&D on kafka, debezium and RetryableTopic

The goal of the POC is to test the RetryableTopics feature of spring-boot

It works like this:

- a new line is written in a product database
- have Debezium emit the ID of the new object to kafka
- a consumer takes this ID
- the consumer makes a REST GET request on the product service
- it does a stdout print of the full message if successful
- it register it the retryable topics if not successful for further processing

learn more about spring-boot kafka retryable topics [here](https://docs.spring.io/spring-kafka/api/org/springframework/kafka/retrytopic/RetryTopicConfigurer.html) and [here](https://docs.spring.io/spring-kafka/api/org/springframework/kafka/retrytopic/RetryTopicConfigurer.html)


(deprecated)
we also use the confluent schema-registry for usage with Avro, and KSQLDB as QOL tool.

![schema of the different services working together](./schema.jpeg)

This poc is partially based on [this ksqldb tutorial](https://docs.ksqldb.io/en/latest/tutorials/event-driven-microservice/)

## How to run the POC

### configure kafka and product api mockup

1. `docker-compose up -d --build` this launches the services

2. setup debezium replication

   `curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" localhost:8083/connectors/ -d @debezium/products-dz-config.json`

3. (deprecated) login into ksqldb-cli container and create the kafka stream (you must wait about one minute before doing this)
   `docker exec -it ksqldb-cli ksql http://ksqldb-server:8088`
   and copy-paste the SQL script from `./scripts/create_stream.sql` int the ksql shell

### Run the java application in intelliJ

simply open IntelliJ, import the pom.xml and run `RetryApplication::main` from there

(deprecated) first genereate the SerDes classes with the Avro schema : `mvn generate-sources`

### create events (mockup)

run `./scripts/create_events.py`


### checks what happens

The python server will raise a 500 error every even event. This is very rough but useful to see what happens with the retryable topics.

To check what happens, you can use the kafka command line utilities to:

- list the topics

```
# example
kafka-topics.sh --bootstrap-server localhost:29092 --list
```

- check what happens in a topic

```
# example
kafka-console-consumer.sh --bootstrap-server localhost:29092 --topic products.public.product-retry-0 --from-beginning
```


## References

### debezium + postgis (pg v. 9.5)

https://github.com/52North/postgis-kafka-cdc/blob/master/postgis-debezium/Dockerfile

### debezium auto-create topics

https://debezium.io/blog/2020/09/15/debezium-auto-create-topics/

### SerDes avro, protobuf and json

https://docs.confluent.io/platform/current/schema-registry/serdes-develop/index.html

### commit async example

https://www.logicbig.com/tutorials/misc/kafka/kafka-manual-commit-async-example.html

### commit sync example

https://www.programcreek.com/java-api-examples/?class=org.apache.kafka.clients.consumer.KafkaConsumer&method=commitSync

### kafka config

offsets commit timeout
https://kafka.apache.org/documentation/#brokerconfigs_offsets.commit.timeout.ms
