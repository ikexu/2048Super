package graduation.kafka

import java.util.Properties

import kafka.producer.{KeyedMessage, Producer, ProducerConfig}

class KafkaProducer(val brokers: String, val topic: String) {
  private val producer: Producer[String, String] = buildProducer

  private def buildProducer = {
    val props = new Properties()
    props.put("metadata.broker.list", brokers)
    props.put("serializer.class", "kafka.serializer.StringEncoder")
    props.put("key.serializer.class", "kafka.serializer.StringEncoder")
    props.put("compression.codec", "snappy")
    props.put("request.required.acks", "-1")
    props.put("send.buffer.bytes", String.valueOf(1024 * 1024))
    val config = new ProducerConfig(props)
    new Producer[String, String](config)
  }

  def sendMessageToKafka(message: String) = {
    val data: KeyedMessage[String, String] = new KeyedMessage[String, String](
      topic, message)
    producer.send(data)
  }

  def close(): Unit = {
    producer.close
  }
}