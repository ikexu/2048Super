package graduation.streaming

import graduation.algorithm.AI
import graduation.util.{Constant, CoreCommon, CoreEnv}
import org.apache.spark.mllib.classification.LogisticRegressionModel
import org.apache.spark.streaming.StreamingContext
import org.slf4j.LoggerFactory

/**
  * Created by KeXu on 2017/4/12.
  */
object StreamingStart {

  private val logger = LoggerFactory.getLogger(StreamingStart.getClass)

  private def init(): Unit = {

    Constant.ConstantInit()
    AI.searchTimeOut = CoreEnv.searchTimeOut
    AI.emptyWeight = CoreEnv.emptyWeight
    AI.maxWeight = CoreEnv.maxWeight
    AI.monoWeight = CoreEnv.monoWeight
    AI.smoothWeight = CoreEnv.smoothWeight
  }

  def main(args: Array[String]): Unit = {

    logger.info("开始初始化2048Super-Core.")
    init()
    logger.info("启动Streaming实例...")
    val spark = CoreCommon.instanceSpark(CoreEnv.master)
    val ssc: StreamingContext = CoreCommon.instanceStreaming(spark, CoreEnv.streamingDurationMs)


    /*//加载离线模型
    logger.info(s"加载离线模型:LogisticRegressionModel->${CoreEnv.modelSavePath}")
    val newModel = LogisticRegressionModel.load(spark, CoreEnv.modelSavePath)
    AI.model=newModel
    */
    val kafkaParams: Map[String, String] = Map(
      "metadata.broker.list" -> CoreEnv.kafkaBroker,
      "serializer.class" -> "kafka.serializer.StringEncoder"
    )

    val topics: Set[String] = Set(
      CoreEnv.computerTopic
    )

    val ks = new KafkaStream(kafkaParams, topics)

    val stream = ks.createStringStream(ssc)

    //分析
    AnalyzeStream.analyzeStream(stream)

    ssc.start()
    ssc.awaitTermination()

  }

}
