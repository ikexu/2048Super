package graduation.streaming

import argonaut.Argonaut._
import graduation.algorithm.AI
import graduation.models.Grid._
import graduation.models.{Grid, Result}
import graduation.util.HttpUtil
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.slf4j.LoggerFactory


/**
  * Created by KeXu on 2017/4/12.
  */
object AnalyzeStream extends Analyze {

  private val logger = LoggerFactory.getLogger(AnalyzeStream.getClass)
  var httpPostUri:String =_
 // val kafkaProducer = new KafkaProducer(CoreEnv.kafkaBroker, CoreEnv.returnTopic)

  override def analyzeStream(stream: InputDStream[ConsumerRecord[String, String]]): Unit = {
    transform(stream).foreachRDD { rdd =>
      rdd.foreachPartition(p => {
        p.foreach(record => {
          analyze(record)
        })
      })
    }
  }

  override def analyze(kv: (String, Grid)): Unit = {
    try {
      logger.info(s"analyze message[${kv._1}] -> [${kv._2.toString}]")
      val bestDiret = new AI(kv._2).getBest()
      val result = Result(kv._1, bestDiret._1).asJson.toString()
      logger.info(s"return message[${kv._1}] -> [result:${result}]")
      //kafkaProducer.sendMessageToKafka(kv._1, result)
      val response=HttpUtil.sendPost(httpPostUri,result)
      logger.info(s"success post message[${kv._1}] ${response.code}")
    } catch {
      case e:Throwable => logger.error(s"analyze message[${kv._1}] error:",e)
    }

  }


  private def transform(stream: InputDStream[ConsumerRecord[String, String]]): DStream[(String, Grid)] = {
    stream.transform { rdd =>
      rdd.map { message =>
        (message.key(), message.value().decodeOption[Grid].get)
      }
    }
  }
}
