package graduation.util

/**
  * Created by KeXu on 2017/5/2.
  */
object CoreEnv {

  val kafkaBroker=Constant.getString("kafkaBroker").get
  val coreStreamingCheckPoint=Constant.getString("coreStreamingCheckPoint").get
  val master=Constant.getString("master").get
  val streamingDurationMs=Constant.getInt("streamingDurationMs").get
  val sparkMongodbInputUri=Constant.getString("spark.mongodb.input.uri").get
  val sparkMongodbInputDatabase=Constant.getString("spark.mongodb.input.database").get
  val sparkMongodbInputCollection=Constant.getString("spark.mongodb.input.collection").get
  val sparkMongodbInputReadPreference=Constant.getString("spark.mongodb.input.readPreference.name").get

}
