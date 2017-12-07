package graduation.streaming

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}

import scala.reflect.ClassTag

/**
  * Created by KeXu on 2017/4/12.
  */
class KafkaStream(kafkaParams: Map[String, Object], kafkaTopic: Set[String]) {

  private var kafkaParams_ : Map[String, Object] = kafkaParams
  private var kafkaTopics_ : Set[String] = kafkaTopic

  /**
    * get a stream that receving kafka
    *
    * @param ssc         a new StreamingContext
    * @param kafkaParams kafka params
    * @param topics      consume kafka topics
    * @return
    */
  def createStream[
  K: ClassTag,
  V: ClassTag]
  (ssc: StreamingContext, kafkaParams: Map[String, Object], topics: Set[String]): InputDStream[ConsumerRecord[K, V]] = {
    KafkaUtils.createDirectStream[K, V](ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[K, V](topics, kafkaParams))
  }

  def createStream[
  K: ClassTag,
  V: ClassTag]
  (ssc: StreamingContext): InputDStream[ConsumerRecord[K, V]] = {
    createStream[K, V](ssc, kafkaParams_, kafkaTopics_)
  }

  def createStringStream(ssc: StreamingContext, kafkaParams: Map[String, Object]
                         , topic: Set[String]): InputDStream[ConsumerRecord[String, String]] = {
    createStream[String, String](ssc, kafkaParams, topic)
  }

  def createStringStream(ssc: StreamingContext): InputDStream[ConsumerRecord[String, String]] = {
    createStringStream(ssc, kafkaParams_, kafkaTopics_)
  }

  def setKafkaParams(params: Map[String, String]): Unit = {
    kafkaParams_ = params
  }

  def setkafkaTopics(topics: Set[String]): Unit = {
    kafkaTopics_ = topics
  }
}
