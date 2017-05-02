package graduation.streaming

import kafka.serializer.{Decoder, StringDecoder}
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka.KafkaUtils

import scala.reflect.ClassTag

/**
  * Created by KeXu on 2017/4/12.
  */
class KafkaStream(kafkaParams: Map[String, String], kafkaTopic: Set[String]) {

  private var kafkaParams_ : Map[String, String] = kafkaParams
  private var kafkaTopics_ : Set[String] = kafkaTopic

  /**
    * get a stream that receving kafka
    *
    * @param ssc         a new StreamingContext
    * @param kafkaParams kafka params
    * @param topic       consume kafka topic
    * @return
    */
  def createStream[
  K: ClassTag,
  V: ClassTag,
  KD <: Decoder[K] : ClassTag,
  VD <: Decoder[V] : ClassTag]
  (ssc: StreamingContext, kafkaParams: Map[String, String], topic: Set[String]): InputDStream[(K, V)] = {
    KafkaUtils.createDirectStream[K, V, KD, VD](ssc, kafkaParams, topic)
  }

  def createStream[
  K: ClassTag,
  V: ClassTag,
  KD <: Decoder[K] : ClassTag,
  VD <: Decoder[V] : ClassTag]
  (ssc: StreamingContext): InputDStream[(K, V)] = {
    createStream[K, V, KD, VD](ssc, kafkaParams_, kafkaTopics_)
  }

  def createStringStream(ssc: StreamingContext, kafkaParams: Map[String, String]
                         , topic: Set[String]): InputDStream[(String, String)] = {
    createStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topic)
  }

  def createStringStream(ssc: StreamingContext): InputDStream[(String, String)] = {
    createStringStream(ssc, kafkaParams_, kafkaTopics_)
  }

  def setKafkaParams(params: Map[String, String]): Unit = {
    kafkaParams_ = params
  }

  def setkafkaTopics(topics: Set[String]): Unit = {
    kafkaTopics_ = topics
  }
}
