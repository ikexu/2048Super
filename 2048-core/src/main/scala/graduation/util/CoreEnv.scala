package graduation.util

/**
  * Created by KeXu on 2017/5/2.
  */
object CoreEnv extends Serializable{

  val coreAppName = "2048Super-Core"
  val computerTopic = "computer-topic"
  val returnTopic = "return-topic"
  var kafkaBroker = Constant.getString("kafkaBroker").get
  var coreStreamingCheckPoint = Constant.getString("coreStreamingCheckPoint").get
  var master = Constant.getString("master").get
  var streamingDurationMs = Constant.getInt("streamingDurationMs").get
  var sparkMongodbInputUri = Constant.getString("spark.mongodb.input.uri").get
  var sparkMongodbInputDatabase = Constant.getString("spark.mongodb.input.database").get
  var sparkMongodbInputCollection = Constant.getString("spark.mongodb.input.collection").get
  var sparkMongodbInputReadPreference = Constant.getString("spark.mongodb.input.readPreference.name").get
  var excellentThreshold = Constant.getInt("excellentThreshold").getOrElse(10000)
  var modelSavePath = Constant.getString("modelSavePath").get
  val searchTimeOut = Constant.getInt("searchTimeOut").getOrElse(1000)
  var smoothWeight: Double = Constant.getDouble("smoothWeight").getOrElse(0.1)
  var monoWeight: Double = Constant.getDouble("monoWeight").getOrElse(1.0)
  var emptyWeight: Double = Constant.getDouble("emptyWeight").getOrElse(2.7)
  var maxWeight: Double = Constant.getDouble("maxWeight").getOrElse(1.0)
  var httpPostUri:String = Constant.getString("httpPostUri").get
  val seachDepth:Int =Constant.getInt("searchDepth").getOrElse(6)
  val mlEnableStep:Int=Constant.getInt("mlEnableStep").getOrElse(50)
  val mlEnadbleWeightThreshold:Double=Constant.getDouble("mlEnadbleWeightThreshold").getOrElse(0.05)
  val startStreamingWithTrainModel:Boolean = Constant.getBoolean("startStreamingWithTrainModel").getOrElse(true)

}
