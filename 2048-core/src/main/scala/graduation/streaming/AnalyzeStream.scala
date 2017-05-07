package graduation.streaming

import argonaut.Argonaut._
import argonaut._
import graduation.algorithm.AI
import graduation.kafka.KafkaProducer
import graduation.models.Grid._
import graduation.models.{Grid, Result}
import graduation.util.CoreEnv
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.slf4j.LoggerFactory


/**
  * Created by KeXu on 2017/4/12.
  */
object AnalyzeStream extends Analyze {

  private val logger = LoggerFactory.getLogger(StreamingStart.getClass)
  val kafkaProducer = new KafkaProducer(CoreEnv.kafkaBroker, CoreEnv.returnTopic)

  override def analyzeStream(stream: InputDStream[(String, String)]): Unit = {
    transform(stream).foreachRDD { rdd =>
      rdd.foreachPartition(p => {
        p.foreach(record => {
          analyze(record)
        })
      })
    }
  }

  override def analyze(kv: (String, Grid)): Unit = {
    logger.info(s"analyze message -> [${kv._2.toString}]")
    val bestDiret = new AI(kv._2).getBest()
    val result = Result(kv._1, bestDiret._1).asJson.toString()
    logger.info(s"return message -> [key:${kv._1} result:${result}]")
    //kafkaProducer.sendMessageToKafka(kv._1, result)
  }


  private def transform(stream: InputDStream[(String, String)]): DStream[(String, Grid)] = {
    stream.transform { rdd =>
      rdd.map { message =>
        (message._1, message._2.decodeOption[Grid].get)
      }
    }
  }
}
