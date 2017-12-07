package graduation.streaming

import com.mongodb.spark.config.ReadConfig
import graduation.algorithm.{AI, OfflineAnalysisStart}
import graduation.util.{Constant, CoreCommon, CoreEnv}
import org.apache.kafka.common.serialization.StringDeserializer
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
    AI.seachDepthLow = CoreEnv.seachDepthLow
    AI.seachDepthHigh = CoreEnv.seachDepthHigh
    AI.mlEnableStep =CoreEnv.mlEnableStep
    AI.mlEnadbleWeightThreshold=CoreEnv.mlEnadbleWeightThreshold
    AI.useOfflineAnalysis = CoreEnv.useOfflineAnalysis
    AnalyzeStream.httpPostUri=CoreEnv.httpPostUri
  }

  def main(args: Array[String]): Unit = {

    logger.info("开始初始化2048Super-Core.")
    init()
    logger.info("启动spark实例...")
    val conf = Array(
      ("spark.mongodb.input.uri", CoreEnv.sparkMongodbInputUri),
      ("spark.mongodb.input.database", CoreEnv.sparkMongodbInputDatabase),
      ("spark.mongodb.input.collection", CoreEnv.sparkMongodbInputCollection),
      ("spark.mongodb.input.readPreference.name", CoreEnv.sparkMongodbInputReadPreference)
    )
    val spark = CoreCommon.instanceSpark(CoreEnv.master,conf)
    logger.info("Start application:"+spark.applicationId)
    var model:LogisticRegressionModel=null
    if(CoreEnv.useOfflineAnalysis){
      if(CoreEnv.startStreamingWithTrainModel){
        logger.info("训练离线模型LogisticRegressionModel")
        val readConfig = ReadConfig(
          Map.empty[String, String], Some(ReadConfig(spark)))
        model = OfflineAnalysisStart.train(spark,readConfig)
      } else {
        logger.info(s"加载离线模型LogisticRegressionModel by path:${CoreEnv.modelSavePath}")
        model = LogisticRegressionModel.load(spark,CoreEnv.modelSavePath)
      }
      AI.model=model
    }

    logger.info("Create LogisticRegressionModel instance successful!")
    logger.info("Start 2048Super Core-Streaming...")
    val ssc: StreamingContext = CoreCommon.instanceStreaming(spark, CoreEnv.streamingDurationMs)
    val kafkaBroker:String = CoreEnv.kafkaBroker
    val kafkaParams: Map[String, Object] = Map(
      "bootstrap.servers" -> kafkaBroker,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer]
    )
    val kafkaTopic:String = CoreEnv.computerTopic
    val topics: Set[String] = Set(
      kafkaTopic
    )

    val ks = new KafkaStream(kafkaParams, topics)
    val stream = ks.createStringStream(ssc)
    //分析
    AnalyzeStream.analyzeStream(stream)
    ssc.start()
    ssc.awaitTermination()

  }

}
