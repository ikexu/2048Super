package graduation.streaming

import graduation.util.CoreCommon
import org.apache.spark.streaming.StreamingContext

/**
  * Created by KeXu on 2017/4/12.
  */
object StreamingStart {

  private def init(): Unit = {

  }

  def main(args: Array[String]): Unit = {

    val spark = CoreCommon.instanceSpark("local[*]")
    val ssc: StreamingContext = CoreCommon.instanceStreaming(spark,2000)

    val kafkaParams: Map[String, String] = Map(
      "metadata.broker.list" -> CoreCommon.kafkaBroker,
       "serializer.class" -> "kafka.serializer.StringEncoder"
    )

    val topics: Set[String] = Set(
      CoreCommon.ComputerTopic
    )

    val ks = new KafkaStream(kafkaParams, topics)

    val stream = ks.createStringStream(ssc)

    //分析
    AnalyzeStream.analyzeStream(stream)

    ssc.start()
    ssc.awaitTermination()

  }

}
