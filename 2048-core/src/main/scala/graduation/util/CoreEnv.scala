package graduation.util

/**
  * Created by KeXu on 2017/5/2.
  */
object CoreEnv {

  val coreAppName = "2048Super-Core"
  val computerTopic = "computer-topic"
  val returnTopic = "return-topic"
  val kafkaBroker = Constant.getString("kafkaBroker").get
  val coreStreamingCheckPoint = Constant.getString("coreStreamingCheckPoint").get
  val master = Constant.getString("master").get
  val streamingDurationMs = Constant.getInt("streamingDurationMs").get
  val sparkMongodbInputUri = Constant.getString("spark.mongodb.input.uri").get
  val sparkMongodbInputDatabase = Constant.getString("spark.mongodb.input.database").get
  val sparkMongodbInputCollection = Constant.getString("spark.mongodb.input.collection").get
  val sparkMongodbInputReadPreference = Constant.getString("spark.mongodb.input.readPreference.name").get
  val excellentThreshold = Constant.getInt("excellentThreshold").getOrElse(10000)
  val modelSavePath = Constant.getString("modelSavePath").get
  val searchTimeOut = Constant.getInt("searchTimeOut").getOrElse(1000)
  var smoothWeight: Double = Constant.getDouble("smoothWeight").getOrElse(0.1)
  var monoWeight: Double = Constant.getDouble("monoWeight").getOrElse(1.0)
  var emptyWeight: Double = Constant.getDouble("emptyWeight").getOrElse(2.7)
  var maxWeight: Double = Constant.getDouble("maxWeight").getOrElse(1.0)
  var httpPostUri:String = Constant.getString("httpPostUri").get

}
