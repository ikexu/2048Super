package main

import (
	"encoding/json"
	"errors"
	"fmt"
	"log"
	"strings"

	"github.com/Shopify/sarama"
	"github.com/iris-contrib/middleware/cors"
	"github.com/iris-contrib/middleware/logger"
	"github.com/kataras/iris"
	"github.com/renstrom/shortuuid"
)

const (
	ComputerTopic = "computer-topic"
	ReturnTopic   = "return-topic"
)

func main() {

	api := iris.New()
	api.Use(cors.Default())
	api.Use(logger.New())
	api.Post("/", computer)

	api.Listen(fmt.Sprintf(":%d", conf.Port))
}

func computer(ctx *iris.Context) {
	r := &Record{}
	if err := ctx.ReadJSON(r); err != nil {
		log.Println("Read json error:", err.Error())
		return
	}
	if err := sendKafka(r); err != nil {
		ctx.JSON(iris.StatusOK, Resp{
			Code:    01,
			Message: err.Error(),
		})
		return
	}
	if err := readKafka(r); err != nil {
		ctx.JSON(iris.StatusOK, Resp{
			Code:    01,
			Message: err.Error(),
		})
		return
	}
	ctx.JSON(iris.StatusOK, Resp{
		Code:    00,
		Message: "Success!",
	})
}

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
		Topic:     ComputerTopic,
		Key:       sarama.StringEncoder(r.UUID),
		Value:     sarama.StringEncoder(string(bts)),
		Partition: 0,
	}
	_, _, err = producer.SendMessage(msg)
	if err != nil {
		log.Println("Send message error:", err.Error())
		return err
	}
	return nil
}

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

	partitionConsumer, err := consumer.ConsumePartition(ComputerTopic, 0, sarama.OffsetOldest)
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
