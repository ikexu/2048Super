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
    val ssc: StreamingContext = CoreCommon.instanceStreaming(spark)

    val kafkaParams: Map[String, String] = Map(
      "metadata.broker.list" -> "localhost:9091"
    )

    val topics: Set[String] = Set(
      CoreCommon.receveTopic
    )

    val ks = new KafkaStream(kafkaParams, topics)
    val stream = ks.createStringStream(ssc)

    //分析
    AnalyzeStream.analyzeStream[String, String](stream)

    ssc.start()
    ssc.awaitTermination()

  }

}
