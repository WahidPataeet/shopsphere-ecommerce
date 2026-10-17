# Day 19 — Kafka Fundamentals

## Purpose

Demonstrate asynchronous messaging with Apache Kafka and Spring Kafka
without coupling the first exercise to the order lifecycle.

## Local topology

- Kafka broker: localhost:9092
- Topic: shopsphere.day19.demo
- Partitions: 1
- Replication factor: 1
- Producer API: POST /api/v1/kafka-demo/messages
- Notification Service: localhost:8087
- Consumer group: shopsphere-notification-demo

## Message format

For this learning exercise, the record value is a plain string.
The record key is a generated message ID.

Structured business events and JSON serialization will be introduced
in the next stage.

## Delivery semantics

HTTP 202 means the Kafka broker acknowledged the record.
It does not mean the consumer completed any business action.

This exercise does not yet implement a durable notification workflow,
dead-letter handling, the transactional outbox, or end-to-end exactly-once
business processing.

## Local testing

1. Start the existing Kafka broker.
2. Verify the topic exists.
3. Run the Notification Service tests.
4. Start the Notification Service.
5. Publish a demo message directly to port 8087.
6. Verify the consumer log.
7. Test the same endpoint through the API Gateway.
