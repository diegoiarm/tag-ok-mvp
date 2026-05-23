#!/bin/bash

kafka-topics --create --if-not-exists \
  --topic portico-cruzado \
  --bootstrap-server kafka:9092 \
  --partitions 3 \
  --replication-factor 1