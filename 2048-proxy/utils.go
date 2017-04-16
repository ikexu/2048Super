package main

import (
	"encoding/json"
	"errors"
	"log"
	"strings"

	"github.com/Shopify/sarama"
	"github.com/renstrom/shortuuid"
)

// Send message to kafka
func sendKafka(r *Record) error {
	producer, err := sarama.NewSyncProducer(strings.Split(conf.Kafka, ","), nil)
	if err != nil {
		log.Fatalln(err)
		return err
	}
	defer func() {
		if err := producer.Close(); err != nil {
			log.Fatalln(err)
		}
	}()

	r.UUID = shortuuid.New()
	bts, _ := json.Marshal(r)

	msg := &sarama.ProducerMessage{
		Topic: ComputerTopic,
		Key:   sarama.StringEncoder(r.UUID),
		Value: sarama.StringEncoder(string(bts)),
	}
	_, _, err = producer.SendMessage(msg)
	if err != nil {
		log.Println("Send message error:", err.Error())
		return err
	}
	return nil
}

// Read message from kafka
func readKafka(r *Record) error {
	consumer, err := sarama.NewConsumer(strings.Split(conf.Kafka, ","), nil)
	if err != nil {
		return err
	}

	defer func() {
		if err := consumer.Close(); err != nil {
			log.Fatalln(err)
		}
	}()

	partitionConsumer, err := consumer.ConsumePartition(ReturnTopic, 0, sarama.OffsetOldest)
	if err != nil {
		return err
	}

	defer func() {
		if err := partitionConsumer.Close(); err != nil {
			log.Fatalln(err)
		}
	}()

	// Trap SIGINT to trigger a shutdown.
	signals := make(chan bool, 1)

	err = errors.New("time out or interrupt!")
ConsumerLoop:
	for {
		select {
		case msg := <-partitionConsumer.Messages():
			err = nil
			log.Println(msg)
			// Handle message
			signals <- true
		case <-signals:
			break ConsumerLoop
		}
	}
	return err
}
