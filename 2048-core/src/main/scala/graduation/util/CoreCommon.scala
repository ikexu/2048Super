package graduation.util

import org.apache.spark.streaming.{Duration, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by KeXu on 2017/4/12.
  */
object CoreCommon {

  private val coreAppName = "2048Super-Core"
  val ComputerTopic = "computer-topic"
  val ReturnTopic   = "return-topic"

  /**
    * get [[SparkContext]] instance
    *
    * @param master
    * @param conf
    * @return new SparkContext
    */
  def instanceSpark(master: String, conf: Array[(String, String)] = Array.empty[(String, String)]): SparkContext = {

    val sparkConf = new SparkConf().setAppName(coreAppName).setMaster(master)

    conf.foreach { case (k, v) =>
      sparkConf.set(k, v)
    }

    SparkContext.getOrCreate(sparkConf)
  }

  /**
    * get [[StreamingContext]] instance
    *
    * @param spark
    * @param durationMs
    * @return new StreamingContext
    */
  def instanceStreaming(spark: SparkContext, durationMs: Long = 500): StreamingContext = {

    val ssc = new StreamingContext(spark, Duration(durationMs))
    //ssc.checkpoint(coreStreamingCheckPoint)
    ssc
  }
}
