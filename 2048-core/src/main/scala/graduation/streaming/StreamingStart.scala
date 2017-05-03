package graduation.streaming

import graduation.util.{Constant, CoreCommon, CoreEnv}
import org.apache.spark.streaming.StreamingContext

/**
  * Created by KeXu on 2017/4/12.
  */
object StreamingStart {

  private def init(): Unit = {

    Constant.ConstantInit()
  }

  def main(args: Array[String]): Unit = {

    init()

    val spark = CoreCommon.instanceSpark(CoreEnv.master)
    val ssc: StreamingContext = CoreCommon.instanceStreaming(spark,CoreEnv.streamingDurationMs)

    val kafkaParams: Map[String, String] = Map(
      "metadata.broker.list" -> CoreEnv.kafkaBroker,
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
