#!/bin/bash

docker exec -it kafka-1 bash
cd cd opt/bitnami/kafka/bin
./kafka-topics.sh --create --topic notifications --partitions 3 --replication-factor 3 --bootstrap-server kafka-1:9093